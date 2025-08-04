package remocra.usecase.courrier

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.data.TypeDestinataire
import remocra.data.courrier.form.CourrierData
import remocra.data.enums.ErrorType
import remocra.db.CourrierRepository
import remocra.db.DocumentRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.ThematiqueRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Courrier
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LCourrierContactGestionnaire
import remocra.db.jooq.remocra.tables.pojos.LCourrierContactOrganisme
import remocra.db.jooq.remocra.tables.pojos.LCourrierOrganisme
import remocra.db.jooq.remocra.tables.pojos.LCourrierUtilisateur
import remocra.eventbus.notification.NotificationEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.nio.file.Paths

class CreateCourrierUseCase : AbstractCUDUseCase<CourrierData>(TypeOperation.INSERT) {
    @Inject private lateinit var documentUtils: DocumentUtils

    @Inject private lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject private lateinit var documentRepository: DocumentRepository

    @Inject private lateinit var courrierRepository: CourrierRepository

    @Inject private lateinit var thematiqueRepository: ThematiqueRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.COURRIER_C)) {
            throw RemocraResponseException(ErrorType.COURRIER_DROIT_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CourrierData) {
        // no-op
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: CourrierData,
    ): CourrierData {
        // On va chercher le le modeleCourrier
        val modeleCourrier = modeleCourrierRepository.getById(element.modeleCourrierId)
        val thematiques = thematiqueRepository.getAll()

        val repertoire = Paths.get(GlobalConstants.DOSSIER_DOCUMENT_COURRIER, element.documentId.toString())
        // On va créer un document
        documentRepository.insertDocument(
            Document(
                documentId = element.documentId,
                documentDate = dateUtils.now(),
                documentNomFichier = element.nomDocumentTmp,
                documentRepertoire = repertoire.toString(),
            ),
        )

        // on déplace le fichier
        documentUtils.moveFile(element.nomDocumentTmp, GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE, repertoire.toString())

        // Puis on crée le courrier
        courrierRepository.insertCourrier(
            Courrier(
                courrierId = element.courrierId,
                courrierDocumentId = element.documentId,
                courrierReference = element.courrierReference,
                courrierObjet = modeleCourrier.modeleCourrierObjetEmail,
                courrierExpediteur = userInfo?.organismeId,
            ),
        )

        thematiqueRepository.insertLThematiqueCourrier(
            thematiqueId = thematiques.find { it.code == element.codeThematique }!!.id,
            courrierId = element.courrierId,
        )

        // Puis pour chaque destinataire on insère dans la table de liaison qui va bien
        element.listeDestinataire.forEach {
            when (it.typeDestinataire) {
                TypeDestinataire.ORGANISME.libelle -> courrierRepository.insertLCourrierOrganisme(
                    LCourrierOrganisme(
                        courrierId = element.courrierId,
                        organismeId = it.destinataireId,
                    ),
                )
                TypeDestinataire.UTILISATEUR.libelle -> courrierRepository.insertLCourrierUtilisateur(
                    LCourrierUtilisateur(
                        courrierId = element.courrierId,
                        utilisateurId = it.destinataireId,
                        accuseReception = null,
                    ),
                )
                TypeDestinataire.CONTACT_GESTIONNAIRE.libelle -> courrierRepository.insertLCourrierContactOrganisme(
                    LCourrierContactOrganisme(
                        courrierId = element.courrierId,
                        contactId = it.destinataireId,
                    ),
                )
                TypeDestinataire.CONTACT_ORGANISME.libelle -> courrierRepository.insertLCourrierContactGestionnaire(
                    LCourrierContactGestionnaire(
                        courrierId = element.courrierId,
                        contactId = it.destinataireId,
                    ),
                )
            }
        }

        // TODO alimenter la table thématiques !!

        return element
    }

    override fun postEvent(element: CourrierData, userInfo: WrappedUserInfo) {
        // On va chercher le le modeleCourrier
        val modeleCourrier = modeleCourrierRepository.getById(element.modeleCourrierId)
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.courrierId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.COURRIER,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )

        // Puis on post les notificationEvent pour chaque destinataire
        eventBus.post(
            NotificationEvent(
                notificationData =
                NotificationMailData(
                    destinataires = element.listeDestinataire.map { it.emailDestinataire }.toSet(),
                    objet = modeleCourrier.modeleCourrierObjetEmail,
                    corps = modeleCourrier.modeleCourrierCorpsEmail,
                    documentId = element.documentId,
                ),
                idJob = null,
            ),
        )
    }
}
