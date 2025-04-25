package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import remocra.auth.WrappedUserInfo
import remocra.data.DashboardComponentData
import remocra.data.DashboardComponentInfoData
import remocra.data.DashboardQueryInfoData
import remocra.data.DashboardQueryRequestData
import remocra.db.jooq.remocra.tables.pojos.Dashboard
import remocra.db.jooq.remocra.tables.pojos.DashboardComponent
import remocra.db.jooq.remocra.tables.pojos.DashboardConfig
import remocra.db.jooq.remocra.tables.pojos.DashboardQuery
import remocra.db.jooq.remocra.tables.pojos.LDashboardProfil
import remocra.db.jooq.remocra.tables.pojos.ProfilUtilisateur
import remocra.db.jooq.remocra.tables.references.DASHBOARD
import remocra.db.jooq.remocra.tables.references.DASHBOARD_COMPONENT
import remocra.db.jooq.remocra.tables.references.DASHBOARD_CONFIG
import remocra.db.jooq.remocra.tables.references.DASHBOARD_QUERY
import remocra.db.jooq.remocra.tables.references.L_DASHBOARD_PROFIL
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import java.util.UUID

class DashboardRepository
@Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getQuery(query: String) = dsl.fetch(query)

    fun getRequest(queryId: UUID): DashboardQueryRequestData? =
        dsl.select(DASHBOARD_QUERY.ID.`as`("queryId"), DASHBOARD_QUERY.QUERY.`as`("query"), DASHBOARD_QUERY.TITLE.`as`("queryTitle")).from(DASHBOARD_QUERY).where(DASHBOARD_QUERY.ID.eq(queryId)).fetchOneInto()

    fun getQueryList(): Collection<DashboardQuery> =
        dsl.select(*DASHBOARD_QUERY.fields()).from(DASHBOARD_QUERY).fetchInto()

    fun getDashboardList(): Collection<Dashboard> =
        dsl.select(*DASHBOARD.fields()).from(DASHBOARD).fetchInto()

    fun getDashboardUser(userInfo: WrappedUserInfo): Dashboard? =
        dsl.select(*DASHBOARD.fields())
            .from(DASHBOARD)
            .join(L_DASHBOARD_PROFIL)
            .on(DASHBOARD.ID.eq(L_DASHBOARD_PROFIL.DASHBOARD_ID))
            .where(L_DASHBOARD_PROFIL.PROFIL_UTILISATEUR_ID.eq(userInfo?.utilisateur?.utilisateurProfilUtilisateurId))
            .fetchOneInto()

    fun getQueryListAllComponents(): Collection<DashboardQueryInfoData> =
        dsl.select(
            DASHBOARD_QUERY.ID.`as`("queryId"),
            DASHBOARD_QUERY.TITLE.`as`("queryTitle"),
            multiset(
                select(
                    DASHBOARD_COMPONENT.ID,
                    DASHBOARD_COMPONENT.TITLE,
                    DASHBOARD_COMPONENT.KEY,
                )
                    .from(DASHBOARD_COMPONENT)
                    .where(DASHBOARD_COMPONENT.DAHSBOARD_QUERY_ID.eq(DASHBOARD_QUERY.ID)),
            ).convertFrom { result ->
                result.map { record ->
                    DashboardComponentInfoData(
                        componentId = record[DASHBOARD_COMPONENT.ID]!!,
                        componentKey = record[DASHBOARD_COMPONENT.KEY]!!,
                        componentTitle = record[DASHBOARD_COMPONENT.TITLE]!!,
                    )
                }
            }.`as`("queryComponents"),
        )
            .from(DASHBOARD_QUERY)
            .fetchInto()

    fun getComponentsIdByQuery(queryId: UUID): Collection<UUID> =
        dsl.select(DASHBOARD_COMPONENT.ID).from(DASHBOARD_COMPONENT).where(DASHBOARD_COMPONENT.DAHSBOARD_QUERY_ID.eq(queryId)).fetchInto()

    fun getComponentsByQuery(queryId: UUID): Collection<DashboardComponent> =
        dsl.select(*DASHBOARD_COMPONENT.fields()).from(DASHBOARD_COMPONENT).where(DASHBOARD_COMPONENT.DAHSBOARD_QUERY_ID.eq(queryId)).fetchInto()

    fun getComponentsConfig(componentId: UUID): DashboardComponent? =
        dsl.select(*DASHBOARD_COMPONENT.fields()).from(DASHBOARD_COMPONENT).where(DASHBOARD_COMPONENT.ID.eq(componentId)).fetchOneInto()

    fun getDashboardConfig(dashboardId: UUID): Collection<DashboardComponentData> =
        dsl.select(
            DASHBOARD_CONFIG.DASHBOARD_COMPONENT_POSITION_CONFIG.`as`("componentConfigPosition"),
            DASHBOARD_COMPONENT.ID.`as`("componentId"),
            DASHBOARD_COMPONENT.KEY.`as`("componentKey"),
            DASHBOARD_COMPONENT.DAHSBOARD_QUERY_ID.`as`("componentQueryId"),
            DASHBOARD_COMPONENT.TITLE.`as`("componentTitle"),
            DASHBOARD_COMPONENT.CONFIG.`as`("componentConfig"),
        )
            .from(DASHBOARD_CONFIG)
            .join(DASHBOARD_COMPONENT)
            .on(DASHBOARD_COMPONENT.ID.eq(DASHBOARD_CONFIG.DASHBOARD_COMPONENT_ID))
            .where(DASHBOARD_CONFIG.DASHBOARD_ID.eq(dashboardId)).fetchInto(DashboardComponentData::class.java)

    fun getAvailableProfil(): Collection<ProfilUtilisateur> =
        dsl.selectFrom(PROFIL_UTILISATEUR)
            .where(
                PROFIL_UTILISATEUR.ID.notIn(
                    dsl.select(L_DASHBOARD_PROFIL.PROFIL_UTILISATEUR_ID)
                        .from(L_DASHBOARD_PROFIL),
                ),
            )
            .orderBy(PROFIL_UTILISATEUR.LIBELLE)
            .fetchInto(ProfilUtilisateur::class.java)

    fun getDashboardProfil(dashboardId: UUID): Collection<ProfilUtilisateur> =
        dsl.select(PROFIL_UTILISATEUR.fields().toList())
            .from(PROFIL_UTILISATEUR)
            .join(L_DASHBOARD_PROFIL)
            .on(PROFIL_UTILISATEUR.ID.eq(L_DASHBOARD_PROFIL.PROFIL_UTILISATEUR_ID))
            .where(L_DASHBOARD_PROFIL.DASHBOARD_ID.eq(dashboardId))
            .fetchInto(ProfilUtilisateur::class.java)

    fun insertQuery(dashboardQuery: DashboardQuery) =
        dsl.insertInto(DASHBOARD_QUERY)
            .set(dsl.newRecord(DASHBOARD_QUERY, dashboardQuery))
            .execute()

    fun insertComponent(dashboardComponent: DashboardComponent) =
        dsl.insertInto(DASHBOARD_COMPONENT)
            .set(dsl.newRecord(DASHBOARD_COMPONENT, dashboardComponent))
            .execute()

    fun insertDashboard(dashboard: Dashboard) =
        dsl.insertInto(DASHBOARD)
            .set(dsl.newRecord(DASHBOARD, dashboard))
            .execute()

    fun insertProfil(dashboardProfil: LDashboardProfil) =
        dsl.insertInto(L_DASHBOARD_PROFIL)
            .set(dsl.newRecord(L_DASHBOARD_PROFIL, dashboardProfil))
            .execute()

    fun insertConfig(dashboardConfig: DashboardConfig) =
        dsl.insertInto(DASHBOARD_CONFIG)
            .set(dsl.newRecord(DASHBOARD_CONFIG, dashboardConfig))
            .execute()

    fun updateQuery(dashboardQuery: DashboardQuery) =
        dsl.update(DASHBOARD_QUERY)
            .set(dsl.newRecord(DASHBOARD_QUERY, dashboardQuery))
            .where(DASHBOARD_QUERY.ID.eq(dashboardQuery.dashboardQueryId))
            .execute()

    fun updateComponent(dashboardComponent: DashboardComponent) =
        dsl.update(DASHBOARD_COMPONENT)
            .set(dsl.newRecord(DASHBOARD_COMPONENT, dashboardComponent))
            .where(DASHBOARD_COMPONENT.ID.eq(dashboardComponent.dashboardComponentId))
            .execute()

    fun updateDashboard(dashboard: Dashboard) =
        dsl.update(DASHBOARD)
            .set(dsl.newRecord(DASHBOARD, dashboard))
            .where(DASHBOARD.ID.eq(dashboard.dashboardId))

    fun deleteComponentsInDashboard(componentIds: Collection<UUID>) =
        dsl.deleteFrom(DASHBOARD_CONFIG)
            .where(DASHBOARD_CONFIG.DASHBOARD_COMPONENT_ID.`in`(componentIds))
            .execute()

    fun deleteComponents(componentIds: Collection<UUID>) =
        dsl.deleteFrom(DASHBOARD_COMPONENT)
            .where(DASHBOARD_COMPONENT.ID.`in`(componentIds))
            .execute()

    fun deleteComponentsByQueryIds(componentsQueryId: UUID) =
        dsl.deleteFrom(DASHBOARD_COMPONENT)
            .where(DASHBOARD_COMPONENT.DAHSBOARD_QUERY_ID.eq(componentsQueryId))
            .execute()

    fun deleteQueryById(componentsQueryId: UUID) =
        dsl.deleteFrom(DASHBOARD_QUERY)
            .where(DASHBOARD_QUERY.ID.eq(componentsQueryId))
            .execute()

    fun deleteProfil(dashboardId: UUID) =
        dsl.deleteFrom(L_DASHBOARD_PROFIL)
            .where(L_DASHBOARD_PROFIL.DASHBOARD_ID.eq(dashboardId))
            .execute()

    fun deleteConfig(dashboardId: UUID) =
        dsl.deleteFrom(DASHBOARD_CONFIG)
            .where(DASHBOARD_CONFIG.DASHBOARD_ID.eq(dashboardId))
            .execute()

    fun deleteDashboard(dashboardId: UUID) =
        dsl.deleteFrom(DASHBOARD)
            .where(DASHBOARD.ID.eq(dashboardId))
            .execute()
}
