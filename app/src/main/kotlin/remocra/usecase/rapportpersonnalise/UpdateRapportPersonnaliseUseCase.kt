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

class UpdateRapportPersonnaliseUseCase : AbstractCUDUseCase<RapportPersonnaliseData>(TypeOperation.UPDATE) {

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
        // On récupère les informations d'origine pour s'assurer que l'élément n'est pas protected
        val reference = rapportPersonnaliseRepository.getRapportPersonnalisePojo(element.rapportPersonnaliseId)
        val elementToUpdate =
            if (reference.rapportPersonnaliseProtected) {
                // L'élément est protected, on n'autorise alors la modification que du libelle, du flag actif (et les profils (c'est fait plus loin))
                reference.copy(
                    rapportPersonnaliseLibelle = element.rapportPersonnaliseLibelle,
                    rapportPersonnaliseActif = element.rapportPersonnaliseActif,
                )
            } else {
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
                )
            }

        // On met à jour le rapport personnalisé
        rapportPersonnaliseRepository.updateRapportPersonnalise(elementToUpdate)

        // On delete les groupes de fonctionnalités
        rapportPersonnaliseRepository.deleteLRapportPersonnaliseGroupeFonctionnalites(element.rapportPersonnaliseId)

        // Puis on les remet
        element.listeGroupeFonctionnalitesId.forEach {
            rapportPersonnaliseRepository.insertLRapportPersonnaliseGroupeFonctionnalites(
                LRapportPersonnaliseGroupeFonctionnalites(
                    groupeFonctionnalitesId = it,
                    rapportPersonnaliseId = element.rapportPersonnaliseId,
                ),
            )
        }

        // Les paramètres UPSERT
        // Si l'élément est protégé, on ne touche a rien d'autre que ce qui a déja été mis à jour pour le moment
        if (!reference.rapportPersonnaliseProtected) {
            val existingIds = element.listeRapportPersonnaliseParametre
                .map { it.rapportPersonnaliseParametreId }

            val listeParam = rapportPersonnaliseRepository
                .getRapportPersonnaliseParametreId(element.rapportPersonnaliseId)
                .filterNot { it in existingIds }

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
            listeParam.forEach { param ->
                rapportPersonnaliseRepository.deleteFromRapportPersonnaliseParametre(param)
            }
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: RapportPersonnaliseData) {
        if (rapportPersonnaliseRepository.checkCodeExists(element.rapportPersonnaliseCode, element.rapportPersonnaliseId)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_CODE_UNIQUE)
        }
        requeteSqlUtils.checkContraintes(userInfo, element)
    }
}
