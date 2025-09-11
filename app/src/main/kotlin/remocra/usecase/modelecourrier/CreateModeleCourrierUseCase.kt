package remocra.usecase.modelecourrier

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.ModeleCourrierData
import remocra.data.enums.ErrorType
import remocra.db.DocumentRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LModeleCourrierGroupeFonctionnalites
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrierParametre
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.RequeteSqlUtils
import java.util.UUID

class CreateModeleCourrierUseCase : AbstractCUDUseCase<ModeleCourrierData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    @Inject
    private lateinit var documentRepository: DocumentRepository

    @Inject
    private lateinit var requeteSqlUtils: RequeteSqlUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_FORBIDDEN)
        }
    }

    override fun postEvent(element: ModeleCourrierData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(part = null),
                pojoId = element.modeleCourrierId!!,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.MODELE_COURRIER,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: ModeleCourrierData): ModeleCourrierData {
        val documentId = UUID.randomUUID()

        // On sauvegarde le document sur le disque
        val repertoire = GlobalConstants.DOSSIER_MODELES_COURRIERS + "/${element.modeleCourrierId}"
        documentUtils.saveFile(element.part!!.inputStream.readAllBytes(), element.part.submittedFileName, repertoire)

        // On récupère le document et on l'enregistre
        documentRepository.insertDocument(
            Document(
                documentId = documentId,
                documentDate = dateUtils.now(),
                documentNomFichier = element.part.submittedFileName,
                documentRepertoire = repertoire,
            ),
        )

        modeleCourrierRepository.run {
            insertModeleCourrier(
                ModeleCourrier(
                    modeleCourrierId = element.modeleCourrierId!!,
                    modeleCourrierActif = element.modeleCourrierActif,
                    modeleCourrierCode = element.modeleCourrierCode,
                    modeleCourrierLibelle = element.modeleCourrierLibelle,
                    modeleCourrierProtected = false,
                    modeleCourrierDescription = element.modeleCourrierDescription,
                    modeleCourrierSourceSql = element.modeleCourrierSourceSql,
                    modeleCourrierModule = TypeModule.entries.find { it.name == element.modeleCourrierModule.name }!!,
                    modeleCourrierCorpsEmail = element.modeleCourrierCorpsEmail,
                    modeleCourrierObjetEmail = element.modeleCourrierObjetEmail,
                    modeleCourrierDocumentId = documentId,
                ),
            )
        }

        // Puis les groupes de fonctionnalités
        element.listeGroupeFonctionnalitesId.forEach {
            modeleCourrierRepository.insertLModeleCourrierGroupeFonctionnalites(
                LModeleCourrierGroupeFonctionnalites(
                    groupeFonctionnalitesId = it,
                    modeleCourrierId = element.modeleCourrierId!!,
                ),
            )
        }

        // Les paramètres
        element.listeModeleCourrierParametre.forEach { param ->
            modeleCourrierRepository.upsertModeleCourrierParametre(
                ModeleCourrierParametre(
                    modeleCourrierParametreId = param.modeleCourrierParametreId,
                    modeleCourrierParametreModeleCourrierId = element.modeleCourrierId!!,
                    modeleCourrierParametreCode = param.modeleCourrierParametreCode,
                    modeleCourrierParametreLibelle = param.modeleCourrierParametreLibelle,
                    modeleCourrierParametreSourceSql = param.modeleCourrierParametreSourceSql.takeUnless { param.modeleCourrierParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    modeleCourrierParametreDescription = param.modeleCourrierParametreDescription,
                    modeleCourrierParametreSourceSqlId = param.modeleCourrierParametreSourceSqlId.takeUnless { param.modeleCourrierParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    modeleCourrierParametreSourceSqlLibelle = param.modeleCourrierParametreSourceSqlLibelle.takeUnless { param.modeleCourrierParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    modeleCourrierParametreValeurDefaut = param.modeleCourrierParametreValeurDefaut,
                    modeleCourrierParametreIsRequired = param.modeleCourrierParametreIsRequired,
                    modeleCourrierParametreType = param.modeleCourrierParametreType,
                    modeleCourrierParametreOrdre = param.modeleCourrierParametreOrdre,
                ),
            )
        }

        return element.copy(part = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ModeleCourrierData) {
        if (modeleCourrierRepository.checkCodeExists(element.modeleCourrierCode, element.modeleCourrierId)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_CODE_EXISTS)
        }

        requeteSqlUtils.checkContraintes(userInfo, element)
    }
}
