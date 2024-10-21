package remocra.usecase.couverturehydraulique

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import remocra.app.AppSettings
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.couverturehydraulique.CalculData
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.CouvertureHydrauliqueCalculRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CalculCouvertureUseCase : AbstractCUDUseCase<CalculData>(TypeOperation.UPDATE) {

    @Inject
    lateinit var couvertureHydrauliqueCalculRepository: CouvertureHydrauliqueCalculRepository

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var objectMapper: ObjectMapper

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: CalculData, userInfo: UserInfo) {
        // On ne trace pas les tracés de la couverture hydraulique
    }

    override fun execute(userInfo: UserInfo?, element: CalculData): CalculData {
        // On va chercher les paramètres que l'on doit utiliser
        val profondeurCouverture = parametresProvider.getParametreInt(ParametreEnum.PROFONDEUR_COUVERTURE.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_PARAMETRE_PROFONDEUR_MANQUANT)
        val distanceMaxParcours = parametresProvider.getParametreInt(ParametreEnum.DECI_DISTANCE_MAX_PARCOURS.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_DISTANCE_MAX_PARCOURS_MANQUANT)
        val distances = parametresProvider.getParametreString(ParametreEnum.DECI_ISODISTANCES.name)
            ?.let { objectMapper.readValue<List<Int>>(it) } ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_ISODISTANCES_MANQUANT)

        couvertureHydrauliqueCalculRepository.deleteCouverture(element.etudeId)

        // Si on utilise que le réseau importé dans l'étude en question, on utilise l'etudeId
        val etudeId = if (element.useReseauImporte) element.etudeId else null

        couvertureHydrauliqueCalculRepository.executeInsererJoinctionPei(
            distanceMaxParcours,
            etudeId,
            element.listPeiId,
            element.listPeiProjetId,
            element.useReseauImporteWithReseauCourant,
        )

        couvertureHydrauliqueCalculRepository.executeParcoursCouverture(
            etudeId,
            element.etudeId,
            distances,
            element.listPeiId,
            element.listPeiProjetId,
            profondeurCouverture,
            element.useReseauImporteWithReseauCourant,
        )

        couvertureHydrauliqueCalculRepository.executeCouvertureHydrauliqueZonage(
            element.etudeId,
            distances,
            element.listPeiId,
            element.listPeiProjetId,
            profondeurCouverture,
            appSettings.srid,
            appSettings.codeSdis,
        )

        couvertureHydrauliqueCalculRepository.executeRetirerJonctionPei()

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: CalculData) {
        // pas de contraintes
    }
}
