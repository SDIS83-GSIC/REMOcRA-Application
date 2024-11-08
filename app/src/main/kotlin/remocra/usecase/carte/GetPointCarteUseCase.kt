package remocra.usecase.carte

import com.google.inject.Inject
import org.slf4j.LoggerFactory
import remocra.data.enums.ErrorType
import remocra.data.enums.TypePointCarte
import remocra.db.CarteRepository
import remocra.db.UtilisateurRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.geometryFromBBox
import remocra.usecase.toGeomFromText
import java.util.UUID

class GetPointCarteUseCase : AbstractUseCase() {
    @Inject
    lateinit var utilisateurRepository: UtilisateurRepository

    @Inject
    lateinit var carteRepository: CarteRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Permet de retourner des points au format GeoJSON pour assurer les interactions sur la carte
     * @param bbox : la bbox si elle existe
     * @param srid de la carte
     * @param organismeId: identifiant de l'organisme de l'utilisateur connecté
     * @param etudeId: Id de l'étude s'il s'agit des PEI en projet
     * @param typePointCarte : Permet de spécifier le type de points (PEI, PEI en projet, PEI prescrit ...)
     */
    fun execute(
        bbox: String?,
        srid: String?,
        organismeId: UUID,
        etudeId: UUID?,
        typePointCarte: TypePointCarte,
        isSuperAdmin: Boolean,
    ): LayersRes {
        val zoneCompetence = utilisateurRepository.getZoneByOrganismeId(organismeId)

        val feature = when (typePointCarte) {
            TypePointCarte.PEI -> bbox.let {
                if (zoneCompetence == null) {
                    logger.error("L'utilisateur n'a pas de zone de compétence.")
                    throw RemocraResponseException(ErrorType.ZONE_COMPETENCE_INTROUVABLE_FORBIDDEN)
                }
                if (it.isNullOrEmpty()) {
                    carteRepository.getPeiWithinZone(zoneCompetence.zoneIntegrationId, isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, srid) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiWithinZoneAndBbox(zoneCompetence.zoneIntegrationId, geom.toGeomFromText())
                }
            }
            TypePointCarte.PEI_PROJET -> bbox.let {
                if (it.isNullOrEmpty()) {
                    carteRepository.getPeiProjetWithinEtude(etudeId!!)
                } else {
                    val geom = geometryFromBBox(bbox, srid) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiProjetWithinEtudeAndBbox(etudeId!!, geom.toGeomFromText())
                }
            }
            TypePointCarte.PEI_PRESCRIT -> TODO()
        }

        return LayersRes(
            features = feature.map {
                Feature(
                    geometry = FeatureGeom(
                        type = it.pointGeometrie.geometryType,
                        coordinates = it.pointGeometrie.coordinates.map { c -> arrayOf(c.x, c.y) }.first(),
                        srid = "EPSG:${it.pointGeometrie.srid}",
                    ),
                    id = it.pointId,
                    properties = it,
                )
            },
        )
    }

    // Data classes pour renvoyer les données au format GeoJSON
    data class LayersRes(
        val type: String = "FeatureCollection",
        val features: List<Feature>,
    )

    data class Feature(
        val type: String = "Feature",
        val geometry: FeatureGeom,
        val id: UUID,
        val properties: Any,
    )

    data class FeatureGeom(
        val type: String,
        val coordinates: Array<Double>,
        val srid: String,
    )
}
