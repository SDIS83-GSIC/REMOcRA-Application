package remocra.usecase.modelecourrier

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.ModeleCourrierData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DocumentRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteModeleCourrierUseCase : AbstractCUDUseCase<ModeleCourrierData>(TypeOperation.DELETE) {

    @Inject
    private lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    private lateinit var documentRepository: DocumentRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ModeleCourrierData) {
        // no-op
    }

    override fun postEvent(element: ModeleCourrierData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(documents = null),
                pojoId = element.modeleCourrierId!!,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.MODELE_COURRIER,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(
        userInfo: UserInfo?,
        element: ModeleCourrierData,
    ): ModeleCourrierData {
        modeleCourrierRepository.deleteLProfilDroit(element.modeleCourrierId!!)

        val repertoire = GlobalConstants.DOSSIER_MODELES_COURRIERS + "${element.modeleCourrierId}"
        documentUtils.deleteDirectory(repertoire)

        // Puis on supprime les paramètres
        modeleCourrierRepository.deleteModeleCourrierParametre(element.modeleCourrierId)

        // La table de liaison document / modèle
        val documentsId = modeleCourrierRepository.getDocumentsId(element.modeleCourrierId)

        modeleCourrierRepository.deleteLModeleCourrierDocument(documentsId)

        // Le modèle de courrier
        modeleCourrierRepository.deleteModeleCourrier(element.modeleCourrierId)

        // Et enfin les documents
        documentRepository.deleteDocumentByIds(documentsId.toList())

        return element.copy(documents = null)
    }
}
