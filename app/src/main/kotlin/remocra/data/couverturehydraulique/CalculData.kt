package remocra.data.couverturehydraulique

import java.util.UUID

data class CalculData(
    val listPeiId: Set<UUID>,
    val listPeiProjetId: Set<UUID>,
    val useReseauImporte: Boolean,
    val useReseauImporteWithReseauCourant: Boolean,
    val etudeId: UUID,
)
