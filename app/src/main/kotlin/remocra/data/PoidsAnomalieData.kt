package remocra.data

import remocra.db.jooq.remocra.enums.TypeVisite
import java.util.UUID

data class PoidsAnomalieData(
    val poidsAnomalieId: UUID = UUID.randomUUID(),
    val poidsAnomalieNatureId: UUID,
    val poidsAnomalieTypeVisite: Array<TypeVisite?>?,
    val poidsAnomalieValIndispoHbe: Int?,
    val poidsAnomalieValIndispoTerrestre: Int?,
) {
    val isEmpty: Boolean
        get() =
            poidsAnomalieValIndispoHbe == null &&
                poidsAnomalieValIndispoTerrestre == null &&
                poidsAnomalieTypeVisite.isNullOrEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PoidsAnomalieData

        if (poidsAnomalieValIndispoHbe != other.poidsAnomalieValIndispoHbe) return false
        if (poidsAnomalieValIndispoTerrestre != other.poidsAnomalieValIndispoTerrestre) return false
        if (poidsAnomalieId != other.poidsAnomalieId) return false
        if (poidsAnomalieNatureId != other.poidsAnomalieNatureId) return false
        if (!poidsAnomalieTypeVisite.contentEquals(other.poidsAnomalieTypeVisite)) return false
        if (isEmpty != other.isEmpty) return false

        return true
    }

    override fun hashCode(): Int {
        var result = poidsAnomalieValIndispoHbe ?: 0
        result = 31 * result + (poidsAnomalieValIndispoTerrestre ?: 0)
        result = 31 * result + poidsAnomalieId.hashCode()
        result = 31 * result + poidsAnomalieNatureId.hashCode()
        result = 31 * result + (poidsAnomalieTypeVisite?.contentHashCode() ?: 0)
        result = 31 * result + isEmpty.hashCode()
        return result
    }
}
