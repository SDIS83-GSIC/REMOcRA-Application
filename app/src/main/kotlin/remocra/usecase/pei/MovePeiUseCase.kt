package remocra.usecase.pei

import jakarta.inject.Inject
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.PeiData
import remocra.db.CommuneRepository
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.jooq.remocra.enums.TypePei
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
    ): PeiData {
        var input = geometry
        if (input.srid != appSettings.srid) {
            val targetCRS = CRS.decode(appSettings.epsg.name)
            val sourceCRS = CRS.decode("EPSG:${input.srid}")
            input = JTS.transform(input, CRS.findMathTransform(sourceCRS, targetCRS))
                ?: throw IllegalArgumentException("Impossible de convertir la géometrie $input en ${targetCRS.name}")
            input.srid = appSettings.srid
        }

        val type = peiRepository.getTypePei(peiId)
        val communeActuelle = peiRepository.getCommune(peiId)

        // On récupère la commune correspondante
        val communeId = communeRepository.getCommunePei(
            input.toGeomFromText(),
        ) ?: throw IllegalArgumentException("Aucune commune n'a été trouvée")

        return when (type) {
            TypePei.PIBI -> pibiRepository.getInfoPibi(peiId).copy(
                peiGeometrie = input,
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
                peiGeometrie = input,
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
