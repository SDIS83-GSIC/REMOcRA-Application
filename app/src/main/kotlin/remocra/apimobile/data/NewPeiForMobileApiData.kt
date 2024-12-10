package remocra.apimobile.data

import remocra.db.jooq.remocra.enums.TypePei
import java.util.UUID

data class NewPeiForMobileApiData(
    val peiId: UUID,
    val gestionnaireId: UUID?,
    val natureId: UUID,
    val natureDeciId: UUID,
    val lon: Double,
    val lat: Double,
    val peiTypePei: TypePei,
    val peiObservation: String?,
)
