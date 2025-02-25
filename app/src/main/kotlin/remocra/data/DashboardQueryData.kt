package remocra.data

import org.jooq.JSONB
import remocra.db.jooq.remocra.enums.TypeDashboardComponents
import java.util.UUID

data class DashboardQueryData(
    val queryId: UUID = UUID.randomUUID(),
    val queryTitle: String,
    val queryQuery: String,
    val queryComponents: Collection<DashboardComponentData>,
)

data class DashboardData(
    val dashboardId: UUID?,
    val dashboardTitle: String?,
    val dashboardComponents: Collection<DashboardComponentData>?,
)

data class DashboardComponentData(
    val componentId: UUID = UUID.randomUUID(),
    val componentQueryId: UUID = UUID.randomUUID(),
    val componentKey: TypeDashboardComponents,
    val componentTitle: String,
    val componentConfig: JSONB,
    val componentConfigPosition: JSONB?,
)

data class DashboardQueryRequestData(
    val queryId: UUID?,
    val query: String,
    val queryTitle: String,
    val zoneCompetenceId: UUID?,
    val utilisateurId: UUID?,
    val organismeId: UUID?,
)

data class DashboardQueryInfoData(
    val queryId: UUID,
    val queryTitle: String,
    val queryComponents: Collection<DashboardComponentInfoData>,
)

data class DashboardComponentInfoData(
    val componentId: UUID,
    val componentKey: TypeDashboardComponents,
    val componentTitle: String,
)

data class DashboardConfigData(
    val dashboardId: UUID = UUID.randomUUID(),
    val dashboardTitle: String,
    val dashboardComponents: Collection<ComponentConfigData>,
    val dashboardProfilsId: Collection<UUID>?,
)

data class ComponentConfigData(
    val componentId: UUID,
    val componentConfig: JSONB,
)

data class QueryIds(
    val dashboardQueryIds: Collection<UUID>,
)
