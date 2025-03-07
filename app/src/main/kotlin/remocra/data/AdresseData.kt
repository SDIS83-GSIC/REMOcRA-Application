package remocra.data

import org.locationtech.jts.geom.Geometry
import java.util.UUID

data class AdresseData(
    val description: String?,
    val listAdresseElement: Collection<AdresseElementInput>,
) {
    val adresseId: UUID = UUID.randomUUID()
}

data class AdresseElementInput(
    val geometry: Geometry,
    val anomalies: Collection<String>,
    val description: String? = null,
    val sousType: UUID,
    var adresseElementAdresseId: UUID? = null,
) {
    val adresseElementId: UUID = UUID.randomUUID()
}
