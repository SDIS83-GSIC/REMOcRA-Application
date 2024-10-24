package remocra.data

import java.util.UUID

data class AnomalieData(
    val anomalieId: UUID = UUID.randomUUID(),
    val anomalieCode: String,
    val anomalieLibelle: String,
    val anomalieCommentaire: String?,
    val anomalieAnomalieCategorieId: UUID,
    val anomalieActif: Boolean,
    val anomalieProtected: Boolean = false,
    val anomalieRendNonConforme: Boolean,
    val poidsAnomalieList: Collection<PoidsAnomalieData>? = listOf(),
)
