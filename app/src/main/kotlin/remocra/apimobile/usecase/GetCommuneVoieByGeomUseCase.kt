package remocra.apimobile.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.db.CommuneRepository
import remocra.db.VoieRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText
import java.util.UUID

class GetCommuneVoieByGeomUseCase @Inject constructor(
    private val communeRepository: CommuneRepository,
    private val voieRepository: VoieRepository,
) : AbstractUseCase() {

    fun execute(geometriePei: Geometry, toleranceVoie: Int): CommuneVoieData {
        // On vérifie si le PEI change de commune
        val communeApresDeplacement = communeRepository.getCommuneIdByGeometrie(geometriePei.toGeomFromText())

        var voieId: UUID? = null

        if (communeApresDeplacement != null) {
            // Sinon, on cherche si on trouve au moins une voie (comme le web)
            val voies = voieRepository.getVoies(
                geometry = geometriePei.toGeomFromText(),
                toleranceVoiesMetres = toleranceVoie,
                listeIdCommune = listOf(communeApresDeplacement),
            )

            // on prend la voie la plus proche
            voieId = voies.firstOrNull()?.id
        }

        return CommuneVoieData(
            communeIdApresDeplacement = communeApresDeplacement,
            voieId = voieId,
        )
    }

    data class CommuneVoieData(
        val communeIdApresDeplacement: UUID?,
        val voieId: UUID?,
    )
}
