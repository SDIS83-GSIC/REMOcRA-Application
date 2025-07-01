package remocra.usecase.carte

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.apimobile.usecase.PeiCaracteristiquesUseCase
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeElementCarte
import remocra.db.CarteRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.EvenementStatutMode
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

    @Inject
    lateinit var peiCaracteristiquesUseCase: PeiCaracteristiquesUseCase

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Permet de retourner des points au format GeoJSON pour assurer les interactions sur la carte
     * @param bbox : la bbox si elle existe
     * @param srid de la carte
     * @param etudeId: Id de l'étude s'il s'agit des PEI en projet
     * @param criseId: Id de la crise s'il s'agit des crises
     * @param criseState: Permet de spécifier les évènements de crises à retourner (Opérationnel / Anticipation)
     * @param typeElementCarte : Permet de spécifier le type de points (PEI, PEI en projet, PEI prescrit ...)
     */
    fun execute(
        bbox: String,
        sridSource: String,
        etudeId: UUID? = null,
        typeElementCarte: TypeElementCarte,
        userInfo: WrappedUserInfo,
        criseId: UUID? = null,
        criseState: EvenementStatutMode? = null,
        listePeiId: Set<UUID>? = null,
    ): LayersRes {
        val srid = sridFromEpsgCode(sridSource)

        val feature = when (typeElementCarte) {
            TypeElementCarte.PEI -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getPeiWithinZoneAndBbox(
                        userInfo.zoneCompetence?.zoneIntegrationId,
                        null,
                        srid,
                        userInfo.isSuperAdmin,
                        listePeiId,
                    )
                } else {
                    val geom =
                        geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiWithinZoneAndBbox(
                        userInfo.zoneCompetence?.zoneIntegrationId,
                        geom.toGeomFromText(),
                        srid,
                        userInfo.isSuperAdmin,
                        listePeiId,
                    )
                }
            }
            TypeElementCarte.PEI_PROJET -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getPeiProjetWithinEtude(etudeId!!, srid)
                } else {
                    val geom =
                        geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiProjetWithinEtudeAndBbox(etudeId!!, geom.toGeomFromText(), srid)
                }
            }
            TypeElementCarte.PEI_PRESCRIT -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getPeiPrescritWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, null, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPeiPrescritWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }
            TypeElementCarte.PERMIS -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getPermisWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, null, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getPermisWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }
            TypeElementCarte.DEBIT_SIMULTANE -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getDebitSimultaneWithinZoneAndBbox(
                        userInfo.zoneCompetence?.zoneIntegrationId,
                        null,
                        srid,
                        userInfo.isSuperAdmin,
                    )
                } else {
                    val geom =
                        geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getDebitSimultaneWithinZoneAndBbox(
                        userInfo.zoneCompetence?.zoneIntegrationId,
                        geom.toGeomFromText(),
                        srid,
                        userInfo.isSuperAdmin,
                    )
                }
            }

            TypeElementCarte.ADRESSE -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getAdresse(null, srid, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin)
                } else {
                    val geom =
                        geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getAdresse(geom.toGeomFromText(), srid, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin)
                }
            }
            TypeElementCarte.OLDEB -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getOldebWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, null, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getOldebWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }
            TypeElementCarte.RCCI -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getRcciWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, null, srid, userInfo.isSuperAdmin)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getRcciWithinZoneAndBbox(userInfo.zoneCompetence?.zoneIntegrationId, geom.toGeomFromText(), srid, userInfo.isSuperAdmin)
                }
            }

            TypeElementCarte.CRISE -> bbox.let {
                if (it.isEmpty()) {
                    carteRepository.getEvenementProjetFromCrise(criseId!!, srid, criseState)
                } else {
                    val geom = geometryFromBBox(bbox, sridSource) ?: throw RemocraResponseException(ErrorType.BBOX_GEOMETRIE)
                    carteRepository.getEvenementProjetFromCriseAndBbox(criseId!!, geom.toGeomFromText(), srid, criseState)
                }
            }
        }

        if (typeElementCarte == TypeElementCarte.PEI) {
            val peiCaracteristiques = peiCaracteristiquesUseCase.getPeiCaracteristiquesWeb()
            feature.map {
                it.propertiesToDisplay = peiCaracteristiques[it.elementId]
            }
        }

        return LayersRes(
            features = feature.map {
                Feature(
                    geometry = if (it.elementGeometrie.geometryType.equals("Point")) {
                        FeatureGeom(
                            type = it.elementGeometrie.geometryType,
                            coordinates =
                            it.elementGeometrie.coordinates.map { c -> arrayOf(c.x, c.y) }.first(),
                            srid = "EPSG:${it.elementGeometrie.srid}",
                        )
                    } else {
                        FeatureGeom(
                            type = it.elementGeometrie.geometryType,
                            coordinates = arrayOf(it.elementGeometrie.coordinates.map { c -> arrayOf(c.x, c.y) }.toTypedArray()),
                            srid = "EPSG:${it.elementGeometrie.srid}",
                        )
                    },
                    id = it.elementId,
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
        val coordinates: Array<out Any>,
        val srid: String,
    )
}
