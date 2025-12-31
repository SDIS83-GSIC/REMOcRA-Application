package remocra.data

import org.locationtech.jts.geom.Geometry
import java.util.UUID

data class SignalementData(
    val description: String?,
    val listSignalementElement: Collection<SignalementElementInput>,
    val document: DocumentsData.DocumentsEvenement? = null,
    val signalementId: UUID = UUID.randomUUID(),
)
data class SignalementElementInput(
    val geometry: Geometry,
    val anomalies: Collection<String>,
    val description: String? = null,
    val sousType: UUID,
    var signalementElementSignalementId: UUID? = null,
) {
    val signalementElementId: UUID = UUID.randomUUID()
}
