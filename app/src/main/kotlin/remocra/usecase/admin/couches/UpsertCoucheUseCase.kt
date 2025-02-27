package remocra.usecase.admin.couches

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.CoucheFormData
import remocra.data.enums.ErrorType
import remocra.db.CoucheRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpsertCoucheUseCase : AbstractCUDUseCase<CoucheFormData>(TypeOperation.INSERT) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var objectMapper: ObjectMapper

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_COUCHE_CARTOGRAPHIQUE)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: CoucheFormData, userInfo: UserInfo) {
        // no-op
    }

    override fun execute(userInfo: UserInfo?, element: CoucheFormData): CoucheFormData {
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
                ),
            )
            groupeCouche.coucheList?.forEach {
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

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: CoucheFormData) {
        // no-op
    }
}
