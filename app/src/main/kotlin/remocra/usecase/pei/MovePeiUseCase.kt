package remocra.usecase.pei

import jakarta.inject.Inject
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.CommuneRepository
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText
import java.util.UUID

class MovePeiUseCase
@Inject
constructor(
    private val peiRepository: PeiRepository,
    private val pibiRepository: PibiRepository,
    private val penaRepository: PenaRepository,
    private val communeRepository: CommuneRepository,
    private val appSettings: AppSettings,
) :
    AbstractUseCase() {

    fun execute(
        geometry: Geometry,
        peiId: UUID,
        voieId: UUID? = null,
        voieLibelle: String? = null,
    ): PeiData {
        val input = normalizeGeometry(geometry)

        val finalVoie = voieLibelle.takeIf { !it.isNullOrEmpty() }

        val type = peiRepository.getTypePei(peiId)
        val communeId = resolveCommuneId(input)

        return when (type) {
            TypePei.PIBI -> pibiRepository.getInfoPibi(peiId).copy(
                peiGeometrie = input,
                peiCommuneId = communeId,
            ).copy(
                peiVoieId = voieId,
                peiVoieTexte = finalVoie,
            )

            TypePei.PENA -> penaRepository.getInfoPena(peiId).copy(
                peiGeometrie = input,
                peiCommuneId = communeId,
            ).copy(
                peiVoieId = voieId,
                peiVoieTexte = finalVoie,
            )
        }
    }

    private fun normalizeGeometry(geometry: Geometry): Geometry {
        var input = geometry

        if (input.srid != appSettings.srid) {
            val targetCRS = CRS.decode(appSettings.epsg.name)

            input = JTS.transform(input, CRS.findMathTransform(CRS.decode("EPSG:${input.srid}"), targetCRS))
                ?: throw RemocraResponseException(ErrorType.GEOMETRIE_CONVERSION)

            input.srid = appSettings.srid
        }

        return input
    }

    private fun resolveCommuneId(geometry: Geometry): UUID =
        communeRepository.getCommuneIdByGeometrie(
            geometry.toGeomFromText(),
        ) ?: throw RemocraResponseException(ErrorType.COMMUNE_NOT_FOUND)
}
