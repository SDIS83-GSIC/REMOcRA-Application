package remocra.data

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader
import java.util.UUID

data class AdresseData(
    val description: String?,
    val listAdresseElement: Collection<AdresseElementInput>,
) {
    val adresseId: UUID = UUID.randomUUID()
}

data class AdresseElementInput(
    val geometryString: String,
    val anomalies: Collection<String>,
    val description: String? = null,
    val srid: Int,
    val sousType: UUID,
    var adresseElementAdresseId: UUID? = null,
) {
    val adresseElementId: UUID = UUID.randomUUID()
    val geometry: Geometry = geometryString.let {
        val reader = WKTReader()
        val geometry = reader.read(it)
        geometry.srid = srid
        return@let geometry
    }
}
