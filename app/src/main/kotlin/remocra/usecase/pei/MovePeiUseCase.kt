package remocra.usecase.pei

import jakarta.inject.Inject
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import remocra.CoordonneesXYSrid
import remocra.app.AppSettings
import remocra.data.PeiData
import remocra.db.CommuneRepository
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.AbstractUseCase
import remocra.utils.formatPoint
import java.util.UUID

class MovePeiUseCase : AbstractUseCase() {

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var pibiRepository: PibiRepository

    @Inject
    lateinit var penaRepository: PenaRepository

    @Inject
    lateinit var communeRepository: CommuneRepository

    @Inject
    lateinit var appSettings: AppSettings

    fun execute(
        coordonnees: CoordonneesXYSrid,
        peiId: UUID,
    ): PeiData {
        val type = peiRepository.getTypePei(peiId)
        val communeActuelle = peiRepository.getCommune(peiId)

        val point: Point = GeometryFactory(PrecisionModel()).createPoint(
            Coordinate(coordonnees.coordonneeX, coordonnees.coordonneeY),
        )
        val sourceCRS = CRS.decode("EPSG:${coordonnees.srid}")
        val targetCRS = CRS.decode(appSettings.epsg.name)
        val transform = CRS.findMathTransform(sourceCRS, targetCRS)
        val geometryProjectionTo = JTS.transform(point, transform)
            ?: throw IllegalArgumentException("Impossible de convertir la géometrie $point en ${appSettings.srid}")

        // On récupère la commune correspondante
        val communeId = communeRepository.getCommunePei(
            coordonneeX = geometryProjectionTo.coordinate.x.toString(),
            coordonneeY = geometryProjectionTo.coordinate.y.toString(),
            sridCoords = geometryProjectionTo.srid,
            sridSdis = appSettings.srid,
        ) ?: throw IllegalArgumentException("Aucune commune n'a été trouvée")

        return when (type) {
            TypePei.PIBI -> pibiRepository.getInfoPibi(peiId).copy(
                peiGeometrie = geometryProjectionTo,
                peiCommuneId = communeId,
            ).let {
                if (communeActuelle != communeId) {
                    return it.copy(
                        peiVoieId = null,
                        peiVoieTexte = null,
                    )
                }
                it
            }
            TypePei.PENA -> penaRepository.getInfoPena(peiId).copy(
                peiGeometrie = formatPoint(coordonnees),
                peiCommuneId = communeId,
            ).let {
                if (communeActuelle != communeId) {
                    return it.copy(
                        peiVoieId = null,
                        peiVoieTexte = null,
                    )
                }
                it
            }
        }
    }
}
