package remocra.data

import remocra.db.jooq.remocra.enums.TypeVisite

data class ApiAnomalieWithNature(
    val anomalieCode: String,
    val anomalieLibelle: String,
    val poidsAnomalieValIndispoTerrestre: Int?,
    val listTypeVisite: List<TypeVisite>,
)
