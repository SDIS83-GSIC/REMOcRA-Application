package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import java.util.UUID

data class PeiProjetData(
    val peiProjetId: UUID,
    val peiProjetEtudeId: UUID,
    val peiProjetNatureDeciId: UUID,
    val peiProjetTypePeiProjet: TypePeiProjet,
    val peiProjetDiametreId: UUID?,
    val peiProjetDiametreCanalisation: Int?,
    val peiProjetCapacite: Int?,
    val peiProjetDebit: Int?,
    val peiProjetGeometrie: Geometry,
)
