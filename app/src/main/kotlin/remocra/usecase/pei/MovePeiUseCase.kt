package remocra.usecase.pei

import com.google.inject.Inject
import remocra.CoordonneesXYSrid
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

    fun execute(
        coordonnees: CoordonneesXYSrid,
        peiId: UUID,
    ): PeiData {
        val type = peiRepository.getTypePei(peiId)

        val communeActuelle = peiRepository.getCommune(peiId)

        // On récupère la commune correspondante
        val communeId = communeRepository.getCommunePei(
            coordonneeX = coordonnees.coordonneeX.toString(),
            coordonneeY = coordonnees.coordonneeY.toString(),
            srid = coordonnees.srid,
        ) ?: throw IllegalArgumentException("Aucune commune n'a été trouvée")

        return when (type) {
            TypePei.PIBI -> pibiRepository.getInfoPibi(peiId).copy(
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
