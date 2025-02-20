package remocra.usecase.modelecourrier

import jakarta.inject.Inject
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
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import remocra.db.jooq.remocra.tables.pojos.LModeleCourrierProfilDroit
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrierParametre
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.UpsertDocumentModeleCourrierUseCase
import remocra.utils.RequeteSqlUtils

class CreateModeleCourrierUseCase : AbstractCUDUseCase<ModeleCourrierData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    private lateinit var upsertDocumentModeleCourrierUseCase: UpsertDocumentModeleCourrierUseCase

    @Inject
    private lateinit var documentRepository: DocumentRepository

    @Inject
    private lateinit var requeteSqlUtils: RequeteSqlUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_FORBIDDEN)
        }
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

    override fun execute(userInfo: UserInfo?, element: ModeleCourrierData): ModeleCourrierData {
        // On insère le rapport personnalisé
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
                ),
            )
        }

        // Puis les profils droit
        element.listeProfilDroitId.forEach {
            modeleCourrierRepository.insertLModeleCourrierProfilDroit(
                LModeleCourrierProfilDroit(
                    profilDroitId = it,
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
                    modeleCourrierParametreSourceSqlLibelle = param.modeleCourrierParametreLibelle.takeUnless { param.modeleCourrierParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    modeleCourrierParametreValeurDefaut = param.modeleCourrierParametreValeurDefaut,
                    modeleCourrierParametreIsRequired = param.modeleCourrierParametreIsRequired,
                    modeleCourrierParametreType = param.modeleCourrierParametreType,
                    modeleCourrierParametreOrdre = param.modeleCourrierParametreOrdre,
                ),
            )
        }

        if (element.documents != null) {
            upsertDocumentModeleCourrierUseCase.execute(
                userInfo,
                element.documents,
                transactionManager,
            )
        }

        return element.copy(documents = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ModeleCourrierData) {
        if (modeleCourrierRepository.checkCodeExists(element.modeleCourrierCode, element.modeleCourrierId)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_CODE_EXISTS)
        }

        requeteSqlUtils.checkContraintes(userInfo, element)
    }
}
