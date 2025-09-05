package remocra.usecase.admin.couches

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheFormData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.db.CoucheRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpsertCoucheUseCase : AbstractCUDUseCase<CoucheFormData>(TypeOperation.INSERT) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var objectMapper: ObjectMapper

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_COUCHE_CARTOGRAPHIQUE)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: CoucheFormData, userInfo: WrappedUserInfo) {
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.COUCHE))
    }

    override fun execute(userInfo: WrappedUserInfo, element: CoucheFormData): CoucheFormData {
        coucheRepository.clearProfilDroit()
        coucheRepository.clearModule()
        coucheRepository.removeOldCouche(element.data.map { groupe -> groupe.coucheList.map { couche -> couche.coucheId } }.flatten())
        coucheRepository.removeOldGroupeCouche(element.data.map { groupe -> groupe.groupeCoucheId })

        element.data.forEach {
                groupeCouche ->
            coucheRepository.upsertGroupeCouche(
                GroupeCouche(
                    groupeCoucheId = groupeCouche.groupeCoucheId,
                    groupeCoucheCode = groupeCouche.groupeCoucheCode,
                    groupeCoucheLibelle = groupeCouche.groupeCoucheLibelle,
                    groupeCoucheOrdre = groupeCouche.groupeCoucheOrdre,
                    groupeCoucheProtected = TODO(),
                ),
            )
            groupeCouche.coucheList.forEach {
                    couche ->
                coucheRepository.upsertCouche(
                    Couche(
                        coucheId = couche.coucheId,
                        coucheGroupeCoucheId = groupeCouche.groupeCoucheId,
                        coucheCode = couche.coucheCode,
                        coucheLibelle = couche.coucheLibelle,
                        coucheOrdre = couche.coucheOrdre,
                        coucheSource = couche.coucheSource,
                        coucheProjection = couche.coucheProjection,
                        coucheUrl = couche.coucheUrl,
                        coucheNom = couche.coucheNom,
                        coucheFormat = couche.coucheFormat,
                        couchePublic = couche.couchePublic,
                        coucheActive = couche.coucheActive,
                        coucheIcone = null,
                        coucheLegende = null,
                        coucheProxy = couche.coucheProxy,
                        coucheCrossOrigin = couche.coucheCrossOrigin,
                        coucheProtected = TODO(),
                    ),
                )

                if (couche.coucheIconeUrl.isNullOrEmpty()) {
                    coucheRepository.updateIcone(couche.coucheId, element.iconeList.find { icone -> icone.code == couche.coucheCode }?.data?.inputStream?.readAllBytes())
                }
                if (couche.coucheLegendeUrl.isNullOrEmpty()) {
                    coucheRepository.updateLegende(couche.coucheId, element.legendeList.find { legende -> legende.code == couche.coucheCode }?.data?.inputStream?.readAllBytes())
                }

                couche.profilDroitList.forEach {
                        profilDroitId ->
                    coucheRepository.insertProfilDroit(couche.coucheId, profilDroitId)
                }

                couche.moduleList.forEach {
                        moduleType ->
                    coucheRepository.insertModule(couche.coucheId, moduleType)
                }
            }
        }

        return element.copy(iconeList = listOf(), legendeList = listOf())
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheFormData) {
        // L'ordre des groupes et des couches doit être unique
        val coucheOrdres = element.data.flatMap { it.coucheList }.map { it.coucheOrdre }
        val groupeCoucheOrdres = element.data.map { it.groupeCoucheOrdre }
        if (groupeCoucheOrdres.distinct().size != groupeCoucheOrdres.size ||
            coucheOrdres.distinct().size != coucheOrdres.size
        ) {
            throw RemocraResponseException(
                ErrorType.ADMIN_COUCHES_ORDRE_UNIQUE,
            )
        }

        // Le code de chaque couche et groupe de couches doit être unique
        val coucheCodes = element.data.flatMap { it.coucheList }.map { it.coucheCode }
        val groupeCoucheCodes = element.data.map { it.groupeCoucheCode }
        if (coucheCodes.distinct().size != coucheCodes.size || groupeCoucheCodes.distinct().size != groupeCoucheCodes.size) {
            throw RemocraResponseException(
                ErrorType.ADMIN_COUCHES_CODE_UNIQUE,
            )
        }
    }
}
