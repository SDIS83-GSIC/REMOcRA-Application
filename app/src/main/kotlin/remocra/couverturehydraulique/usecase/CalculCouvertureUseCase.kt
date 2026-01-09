package remocra.couverturehydraulique.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.couverturehydraulique.CalculData
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CalculCouvertureUseCase @Inject constructor(
    private val couvertureHydrauliqueUseCase: CouvertureHydrauliqueUseCase,
    private val reseauUseCase: ReseauUseCase,
    private val createTopologieUseCase: CreateTopologieUseCase,
    private val parametresProvider: ParametresProvider,
    private val objectMapper: ObjectMapper,
) : AbstractCUDUseCase<CalculData>(TypeOperation.UPDATE) {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: CalculData, userInfo: WrappedUserInfo) {
        // On ne trace pas les tracés de la couverture hydraulique
    }

    override fun execute(userInfo: WrappedUserInfo, element: CalculData): CalculData {
        // On va chercher les paramètres que l'on doit utiliser
        val profondeurCouverture = parametresProvider.getParametreInt(ParametreEnum.PROFONDEUR_COUVERTURE.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_PARAMETRE_PROFONDEUR_MANQUANT)
        val distanceMaxParcours = parametresProvider.getParametreInt(ParametreEnum.DECI_DISTANCE_MAX_PARCOURS.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_DISTANCE_MAX_PARCOURS_MANQUANT)
        val distances = parametresProvider.getParametreString(ParametreEnum.DECI_ISODISTANCES.name)
            ?.let { objectMapper.readValue<List<Int>>(it) } ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_ISODISTANCES_MANQUANT)

        // Si on utilise que le réseau importé dans l'étude en question, on utilise l'etudeId
        val etudeId = if (element.useReseauImporte || element.useReseauImporteWithReseauCourant) element.etudeId else null
        val listePeiIdWithProjets = element.listPeiId.plus(element.listPeiProjetId)

        // Insérer les jonctions PEI pour les PEI avant de commencer les parcours
        listePeiIdWithProjets.forEach { peiId ->
            val result = reseauUseCase.insertJonctionPei(peiId, distanceMaxParcours, element.etudeId, etudeId, element.useReseauImporteWithReseauCourant)
            if (!result) {
                // Si pas inséré, trop loin du réseau
                logger.warn("WARN: Échec de création de la jonction PEI pour PEI: $peiId")
            }
        }

        try {
            // Parcours pour chaque PEI
            listePeiIdWithProjets.forEach { peiId ->
                couvertureHydrauliqueUseCase.parcoursCouvertureHydraulique(
                    depart = peiId,
                    idEtude = element.etudeId,
                    idReseauImporte = etudeId,
                    isodistances = distances,
                    profondeurCouverture = profondeurCouverture,
                    useReseauImporteWithCourant = element.useReseauImporteWithReseauCourant,
                )
            }

            // Calcul du zonage après tous les parcours
            couvertureHydrauliqueUseCase.calculerCouvertureHydrauliqueZonage(
                idEtude = element.etudeId,
                isodistances = distances,
                profondeurCouverture = profondeurCouverture,
            )
        } finally {
            // Retirer les jonctions PEI après tous les calculs
            listePeiIdWithProjets.forEach { peiId ->
                try {
                    reseauUseCase.removeJonctionPei(peiId)
                } catch (e: Exception) {
                    logger.warn("WARN: Échec de suppression de la jonction PEI pour PEI: $peiId - ${e.message}")
                }
            }
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CalculData) {
        // pas de contraintes
    }
}
