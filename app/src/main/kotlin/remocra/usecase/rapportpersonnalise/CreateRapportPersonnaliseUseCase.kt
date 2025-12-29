package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.RapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import remocra.db.jooq.remocra.tables.pojos.LRapportPersonnaliseGroupeFonctionnalites
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnalise
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnaliseParametre
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.utils.RequeteSqlUtils

class CreateRapportPersonnaliseUseCase : AbstractCUDUseCase<RapportPersonnaliseData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var requeteSqlUtils: RequeteSqlUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_RAPPORTS_PERSO)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_FORBIDDEN)
        }
    }

    override fun postEvent(element: RapportPersonnaliseData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rapportPersonnaliseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RAPPORT_PERSONNALISE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: RapportPersonnaliseData): RapportPersonnaliseData {
        // On insère le rapport personnalisé
        rapportPersonnaliseRepository.insertRapportPersonnalise(
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

        // Puis les groupes de fonctionnalités
        element.listeGroupeFonctionnalitesId.forEach {
            rapportPersonnaliseRepository.insertLRapportPersonnaliseGroupeFonctionnalites(
                LRapportPersonnaliseGroupeFonctionnalites(
                    groupeFonctionnalitesId = it,
                    rapportPersonnaliseId = element.rapportPersonnaliseId,
                ),
            )
        }

        // Les paramètres
        element.listeRapportPersonnaliseParametre.forEach { param ->
            rapportPersonnaliseRepository.upsertRapportPersonnaliseParametre(
                RapportPersonnaliseParametre(
                    rapportPersonnaliseParametreId = param.rapportPersonnaliseParametreId,
                    rapportPersonnaliseParametreRapportPersonnaliseId = element.rapportPersonnaliseId,
                    rapportPersonnaliseParametreCode = param.rapportPersonnaliseParametreCode,
                    rapportPersonnaliseParametreLibelle = param.rapportPersonnaliseParametreLibelle,
                    rapportPersonnaliseParametreSourceSql = param.rapportPersonnaliseParametreSourceSql.takeUnless { param.rapportPersonnaliseParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    rapportPersonnaliseParametreDescription = param.rapportPersonnaliseParametreDescription,
                    rapportPersonnaliseParametreSourceSqlId = param.rapportPersonnaliseParametreSourceSqlId.takeUnless { param.rapportPersonnaliseParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    rapportPersonnaliseParametreSourceSqlLibelle = param.rapportPersonnaliseParametreSourceSqlLibelle.takeUnless { param.rapportPersonnaliseParametreType != TypeParametreRapportCourrier.SELECT_INPUT },
                    rapportPersonnaliseParametreValeurDefaut = param.rapportPersonnaliseParametreValeurDefaut,
                    rapportPersonnaliseParametreIsRequired = param.rapportPersonnaliseParametreIsRequired,
                    rapportPersonnaliseParametreType = param.rapportPersonnaliseParametreType,
                    rapportPersonnaliseParametreOrdre = param.rapportPersonnaliseParametreOrdre,
                ),
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: RapportPersonnaliseData) {
        if (rapportPersonnaliseRepository.checkCodeExists(element.rapportPersonnaliseCode, element.rapportPersonnaliseId)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_CODE_UNIQUE)
        }

        requeteSqlUtils.checkContraintes(userInfo = userInfo, element = element)
    }
}
