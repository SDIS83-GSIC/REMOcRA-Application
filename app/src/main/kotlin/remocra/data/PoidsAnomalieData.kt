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
}
