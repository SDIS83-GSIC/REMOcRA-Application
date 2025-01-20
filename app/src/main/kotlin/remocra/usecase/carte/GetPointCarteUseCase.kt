package remocra.usecase.carte

import com.google.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.data.enums.TypePointCarte
import remocra.db.CarteRepository
import remocra.db.UtilisateurRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.utils.geometryFromBBox
import remocra.utils.sridFromEpsgCode
import remocra.utils.toGeomFromText
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
     * @param etudeId: Id de l'étude s'il s'agit des PEI en projet
     * @param typePointCarte : Permet de spécifier le type de points (PEI, PEI en projet, PEI prescrit ...)
     */
    fun execute(
        bbox: String,
        sridSource: String,
        etudeId: UUID? = null,
        typePointCarte: TypePointCarte,
        userInfo: UserInfo,
    ): LayersRes {
        val srid = sridFromEpsgCode(sridSource)

        val feature = when (typePointCarte) {
            TypePointCarte.PEI -> bbox.let {
                if (userInfo.zoneCompetence == null && !userInfo.isSuperAdmin) {
                    logger.error("L'utilisateur n'a pas de zone de compétence.")
                    throw RemocraResponseException(ErrorType.ZONE_COMPETENCE_INTROUVABLE_FORBIDDEN)
                }
                if (it.isEmpty()) {
                    carteRepository.getPeiWithinZone(userInfo.zoneCompetence?.zoneIntegrationId, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }
            TypePointCarte.PEI_PROJET -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getPeiProjetWithinEtude(etudeId!!, srid)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiProjetWithinEtudeAndBbox(etudeId!!, geom.toGeomFromText(), srid)
                }
            }
            TypePointCarte.PEI_PRESCRIT -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getPeiPrescritWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, null, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiPrescritWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }
            TypePointCarte.DEBIT_SIMULTANE -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getDebitSimultaneWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, null, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getDebitSimultaneWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }
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
