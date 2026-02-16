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
        geometry: Geometry,
        peiId: UUID,
        voieId: UUID? = null,
        voieLibelle: String? = null,
    ): PeiData {
        val input = normalizeGeometry(geometry)

        val finalVoie = voieLibelle.takeIf { !it.isNullOrEmpty() }

        val type = peiRepository.getTypePei(peiId)
        val communeActuelle = peiRepository.getCommune(peiId)
        val communeId = resolveCommuneId(input)

        return when (type) {
            TypePei.PIBI -> pibiRepository.getInfoPibi(peiId).copy(
                peiGeometrie = input,
                peiCommuneId = communeId,
            ).let {
                if (communeActuelle != communeId) {
                    return it.copy(
                        peiVoieId = voieId,
                        peiVoieTexte = finalVoie,
                    )
                }
                it
            }

            TypePei.PENA -> penaRepository.getInfoPena(peiId).copy(
                peiGeometrie = input,
                peiCommuneId = communeId,
            ).let {
                if (communeActuelle != communeId) {
                    return it.copy(
                        peiVoieId = voieId,
                        peiVoieTexte = finalVoie,
                    )
                }
                it
            }
        }
    }

    fun needTobeMoved(
        geometry: Geometry,
        peiId: UUID,
    ): Boolean {
        return peiRepository.getCommune(peiId) != resolveCommuneId(normalizeGeometry(geometry))
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
        communeRepository.getCommunePei(
            geometry.toGeomFromText(),
        ) ?: throw RemocraResponseException(ErrorType.COMMUNE_NOT_FOUND)
}
