package remocra.couverturehydraulique.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.app.AppSettings
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.couverturehydraulique.CalculData
import remocra.couverturehydraulique.GeometrieUtils
import remocra.couverturehydraulique.graphe.Chemin
import remocra.couverturehydraulique.graphe.CreateTopologie
import remocra.couverturehydraulique.graphe.GrapheManager
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CalculCouvertureUseCase @Inject constructor(
    private val couvertureHydrauliqueUseCase: CouvertureHydrauliqueUseCase,
    private val reseauUseCase: ReseauUseCase,
    private val parametresProvider: ParametresProvider,
    private val objectMapper: ObjectMapper,
    private val createTopologie: CreateTopologie,
    private val grapheManager: GrapheManager,
    private val appSettings: AppSettings,
    private val geometrieUtils: GeometrieUtils,
    private val parcoursUseCase: ParcoursUseCase,
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
        val profondeurCouverture = parametresProvider.getParametreInt(ParametreEnum.PROFONDEUR_COUVERTURE.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_PARAMETRE_PROFONDEUR_MANQUANT)
        val distanceMaxParcours = parametresProvider.getParametreInt(ParametreEnum.DECI_DISTANCE_MAX_PARCOURS.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_DISTANCE_MAX_PARCOURS_MANQUANT)
        val distances = parametresProvider.getParametreString(ParametreEnum.DECI_ISODISTANCES.name)
            ?.let { objectMapper.readValue<List<Int>>(it) } ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_ISODISTANCES_MANQUANT)

        val etudeId = if (element.useReseauImporte || element.useReseauImporteWithReseauCourant) element.etudeId else null
        val graphe = grapheManager.loadGraphe(etudeId, element.useReseauImporteWithReseauCourant)
        createTopologie.createTopologie(graphe)

        val listePeiIdWithProjets = element.listPeiId.plus(element.listPeiProjetId)

        // --- Nettoyage préventif des jonctions PEI ---
        listePeiIdWithProjets.forEach { peiId ->
            try {
                reseauUseCase.removeJonctionPei(peiId, graphe)
            } catch (e: Exception) {
                logger.warn("WARN: Nettoyage préalable de la jonction PEI pour PEI: $peiId - ${e.message}")
            }
        }
        // --- Insertion des jonctions PEI pour tous les PEI dans le graphe mémoire ---
        listePeiIdWithProjets.forEach { peiId ->
            val result = reseauUseCase.insertJonctionPei(
                peiId,
                distanceMaxParcours,
                graphe,
            )
            if (!result) {
                logger.warn("Échec de création de la jonction PEI pour PEI: $peiId")
            }
        }

        try {
            val tabDistances = distances.map { it - profondeurCouverture }.sortedDescending().toIntArray()
            Chemin.Exploration.purgeOldTrees()
            // Parcours pour chaque PEI sur la plus grande distance
            couvertureHydrauliqueUseCase.parcoursCouvertureHydraulique(
                listePeiIdWithProjets = listePeiIdWithProjets,
                idEtude = element.etudeId,
                distance = tabDistances[0],
                profondeurCouverture = profondeurCouverture,
                graphe = graphe,
            )
            // Pour les distances inférieures, tronquer et sauvegarder
            tabDistances.drop(1).forEach { distance ->
                listePeiIdWithProjets.forEachIndexed { idxPei, _ ->
                    val tree = Chemin.Exploration.trees.getOrNull(idxPei)
                    if (tree != null) {
                        val sousTree = tree.truncate(
                            distance = distance,
                            geometrieUtils = geometrieUtils,
                            profondeurCouverture = profondeurCouverture,
                            appSettings = appSettings,
                        )
                        parcoursUseCase.saveCouverture(sousTree.start, element.etudeId, distance, sousTree)
                    }
                }
            }
            // Calcul du zonage après tous les parcours
            couvertureHydrauliqueUseCase.calculerCouvertureHydrauliqueZonage(
                idEtude = element.etudeId,
                isodistances = distances,
                profondeurCouverture = profondeurCouverture,
                graphe = graphe,
            )
        } finally {
            // --- Suppression des jonctions PEI après tous les calculs ---
            listePeiIdWithProjets.forEach { peiId ->
                try {
                    reseauUseCase.removeJonctionPei(peiId, graphe)
                } catch (e: Exception) {
                    logger.warn("Échec de suppression de la jonction PEI pour PEI: $peiId - ${e.message}")
                }
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CalculData) {
        // pas de contraintes
    }
}
