package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.RapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.db.jooq.remocra.tables.pojos.LRapportPersonnaliseProfilDroit
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnalise
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnaliseParametre
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateRapportPersonnaliseUseCase : AbstractCUDUseCase<RapportPersonnaliseData>(TypeOperation.UPDATE) {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var rapportPersonnaliseUtils: RapportPersonnaliseUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_RAPPORTS_PERSO)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_FORBIDDEN)
        }
    }

    override fun postEvent(element: RapportPersonnaliseData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rapportPersonnaliseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RAPPORT_PERSONNALISE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: RapportPersonnaliseData): RapportPersonnaliseData {
        // On insère le rapport personnalisé
        rapportPersonnaliseRepository.updateRapportPersonnalise(
            RapportPersonnalise(
                rapportPersonnaliseId = element.rapportPersonnaliseId,
                rapportPersonnaliseActif = element.rapportPersonnaliseActif,
                rapportPersonnaliseCode = element.rapportPersonnaliseCode,
                rapportPersonnaliseLibelle = element.rapportPersonnaliseLibelle,
                rapportPersonnaliseProtected = false,
                rapportPersonnaliseChampGeometrie = element.rapportPersonnaliseChampGeometrie,
                rapportPersonnaliseDescription = element.rapportPersonnaliseDescription,
                rapportPersonnaliseSourceSql = element.rapportPersonnaliseSourceSql,
                rapportPersonnaliseModule = TypeModule.entries.find { it.name == element.rapportPersonnaliseModule.name }!!,
            ),
        )

        // On delete les profils droit
        rapportPersonnaliseRepository.deleteLRapportPersonnaliseProfilDroit(element.rapportPersonnaliseId)

        // Puis on les remet
        element.listeProfilDroitId.forEach {
            rapportPersonnaliseRepository.insertLRapportPersonnaliseProfilDroit(
                LRapportPersonnaliseProfilDroit(
                    profilDroitId = it,
                    rapportPersonnaliseId = element.rapportPersonnaliseId,
                ),
            )
        }

        // Les paramètres UPSERT
        element.listeRapportPersonnaliseParametre.forEach { param ->
            rapportPersonnaliseRepository.upsertRapportPersonnaliseParametre(
                RapportPersonnaliseParametre(
                    rapportPersonnaliseParametreId = param.rapportPersonnaliseParametreId,
                    rapportPersonnaliseParametreRapportPersonnaliseId = element.rapportPersonnaliseId,
                    rapportPersonnaliseParametreCode = param.rapportPersonnaliseParametreCode,
                    rapportPersonnaliseParametreLibelle = param.rapportPersonnaliseParametreLibelle,
                    rapportPersonnaliseParametreSourceSql = param.rapportPersonnaliseParametreSourceSql.takeUnless { param.rapportPersonnaliseParametreType != TypeParametreRapportPersonnalise.SELECT_INPUT },
                    rapportPersonnaliseParametreDescription = param.rapportPersonnaliseParametreDescription,
                    rapportPersonnaliseParametreSourceSqlId = param.rapportPersonnaliseParametreSourceSqlId.takeUnless { param.rapportPersonnaliseParametreType != TypeParametreRapportPersonnalise.SELECT_INPUT },
                    rapportPersonnaliseParametreSourceSqlLibelle = param.rapportPersonnaliseParametreSourceSqlLibelle.takeUnless { param.rapportPersonnaliseParametreType != TypeParametreRapportPersonnalise.SELECT_INPUT },
                    rapportPersonnaliseParametreValeurDefaut = param.rapportPersonnaliseParametreValeurDefaut,
                    rapportPersonnaliseParametreIsRequired = param.rapportPersonnaliseParametreIsRequired,
                    rapportPersonnaliseParametreType = param.rapportPersonnaliseParametreType,
                    rapportPersonnaliseParametreOrdre = param.rapportPersonnaliseParametreOrdre,
                ),
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: RapportPersonnaliseData) {
        rapportPersonnaliseUtils.checkContraintes(element)
    }
}
