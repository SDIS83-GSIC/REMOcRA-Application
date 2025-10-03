package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.CoucheData
import remocra.db.jooq.remocra.enums.SourceCarto
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_COUCHE_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_COUCHE_MODULE
import java.util.UUID

class CoucheRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    /**
     * Retourne une Map<coucheId, CoucheData> pour stockage dans le dataCache
     */
    fun getMapById(): Map<UUID, CoucheData> =
        dsl.selectFrom(COUCHE).fetchInto<Couche>().map {
                couche ->
            CoucheData(
                coucheId = couche.coucheId,
                coucheCode = couche.coucheCode,
                coucheLibelle = couche.coucheLibelle,
                coucheOrdre = couche.coucheOrdre,
                coucheSource = couche.coucheSource,
                coucheProjection = couche.coucheProjection,
                coucheUrl = couche.coucheUrl,
                coucheNom = couche.coucheNom,
                coucheFormat = couche.coucheFormat,
                couchePublic = couche.couchePublic,
                coucheActive = couche.coucheActive,
                coucheProxy = couche.coucheProxy ?: true,
                coucheCrossOrigin = couche.coucheCrossOrigin,
                coucheIconeUrl = null,
                coucheLegendeUrl = null,
                groupeFonctionnalitesList = getGroupeFonctionnalitesList(couche.coucheId).map { groupeFonctionnalites -> groupeFonctionnalites.groupeFonctionnalitesId },
                moduleList = getModuleList(couche.coucheId),
                coucheProtected = couche.coucheProtected,
            )
        }.associateBy { it.coucheId }

    fun getCoucheMap(module: TypeModule, groupeFonctionnalites: GroupeFonctionnalites?, isSuperAdmin: Boolean): Map<UUID, List<Couche>> =
        dsl.selectDistinct(*COUCHE.fields())
            .from(COUCHE)
            .leftJoin(L_COUCHE_GROUPE_FONCTIONNALITES).on(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(COUCHE.ID))
            .join(L_COUCHE_MODULE).on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID).and(L_COUCHE_MODULE.MODULE_TYPE.eq(module)))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    COUCHE.PUBLIC.isTrue.or(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(groupeFonctionnalites?.groupeFonctionnalitesId)),
                    isSuperAdmin,
                ),
            )
            .orderBy(COUCHE.ORDRE.desc())
            .fetchInto<Couche>().groupBy { it.coucheGroupeCoucheId }

    fun getGroupeFonctionnalitesList(coucheId: UUID): List<GroupeFonctionnalites> =
        dsl.select(*GROUPE_FONCTIONNALITES.fields())
            .from(GROUPE_FONCTIONNALITES)
            .join(L_COUCHE_GROUPE_FONCTIONNALITES).on(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(coucheId))
            .fetchInto<GroupeFonctionnalites>()

    fun getModuleList(coucheId: UUID): List<TypeModule> =
        dsl.select(L_COUCHE_MODULE.MODULE_TYPE)
            .from(L_COUCHE_MODULE)
            .where(L_COUCHE_MODULE.COUCHE_ID.eq(coucheId))
            .fetchInto<TypeModule>()

    fun getCoucheList(groupeCoucheId: UUID): List<Couche> = dsl.selectFrom(COUCHE).where(COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId)).fetchInto<Couche>()

    fun getCoucheById(coucheId: UUID): Couche = dsl.selectFrom(COUCHE).where(COUCHE.ID.eq(coucheId)).fetchSingleInto<Couche>()

    fun getGroupeCoucheList(): List<GroupeCouche> = dsl.selectFrom(GROUPE_COUCHE).orderBy(GROUPE_COUCHE.ORDRE.desc()).fetchInto<GroupeCouche>()

    fun getIcone(idCouche: UUID): ByteArray? =
        dsl.select(COUCHE.ICONE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.ICONE)

    fun getLegende(idCouche: UUID): ByteArray? =
        dsl.select(COUCHE.LEGENDE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.LEGENDE)

    fun upsertGroupeCouche(groupeCouche: GroupeCouche): Int = with(dsl.newRecord(GROUPE_COUCHE, groupeCouche)) {
        return dsl
            .insertInto(GROUPE_COUCHE)
            .set(this)
            .onConflict()
            .doUpdate()
            .set(GROUPE_COUCHE.ORDRE, groupeCouche.groupeCoucheOrdre)
            .set(GROUPE_COUCHE.LIBELLE, groupeCouche.groupeCoucheLibelle)
            .execute()
    }

    fun upsertCouche(couche: Couche): Int = with(dsl.newRecord(COUCHE, couche)) {
        dsl
            .insertInto(COUCHE)
            .set(this)
            .onConflict()
            .doUpdate()
            .set(COUCHE.GROUPE_COUCHE_ID, couche.coucheGroupeCoucheId)
            .set(COUCHE.LIBELLE, couche.coucheLibelle)
            .set(COUCHE.CODE, couche.coucheCode)
            .set(COUCHE.NOM, couche.coucheNom)
            .set(COUCHE.ORDRE, couche.coucheOrdre)
            .set(COUCHE.SOURCE, couche.coucheSource)
            .set(COUCHE.PROJECTION, couche.coucheProjection)
            .set(COUCHE.CROSS_ORIGIN, couche.coucheCrossOrigin)
            .set(COUCHE.URL, couche.coucheUrl)
            .set(COUCHE.FORMAT, couche.coucheFormat)
            .set(COUCHE.PUBLIC, couche.couchePublic)
            .set(COUCHE.ACTIVE, couche.coucheActive)
            .set(COUCHE.PROXY, couche.coucheProxy)
            .execute()
    }

    fun updateIcone(coucheId: UUID, icone: ByteArray?) =
        dsl.update(COUCHE)
            .set(COUCHE.ICONE, icone)
            .where(COUCHE.ID.eq(coucheId))
            .execute()

    fun updateLegende(coucheId: UUID, legende: ByteArray?) =
        dsl.update(COUCHE)
            .set(COUCHE.LEGENDE, legende)
            .where(COUCHE.ID.eq(coucheId))
            .execute()

    fun removeOldCouche(toKeep: Collection<UUID>): Int = dsl.deleteFrom(COUCHE).where(COUCHE.ID.notIn(toKeep)).execute()

    fun removeOldGroupeCouche(toKeep: Collection<UUID>): Int =
        dsl.deleteFrom(GROUPE_COUCHE).where(GROUPE_COUCHE.ID.notIn(toKeep)).execute()

    fun clearGroupeFonctionnalites(): Int = dsl.deleteFrom(L_COUCHE_GROUPE_FONCTIONNALITES).execute()

    fun clearModule(): Int = dsl.deleteFrom(L_COUCHE_MODULE).execute()

    fun insertGroupeFonctionnalites(coucheId: UUID, groupeFonctionnalitesId: UUID): Int =
        dsl.insertInto(L_COUCHE_GROUPE_FONCTIONNALITES)
            .set(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID, coucheId)
            .set(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID, groupeFonctionnalitesId)
            .execute()

    fun insertModule(coucheId: UUID, moduleType: TypeModule): Int =
        dsl.insertInto(L_COUCHE_MODULE)
            .set(L_COUCHE_MODULE.COUCHE_ID, coucheId)
            .set(L_COUCHE_MODULE.MODULE_TYPE, moduleType)
            .execute()

    fun getAvailableLayers(groupeCoucheId: UUID): List<Couche> =
        dsl.selectFrom(COUCHE)
            .where(COUCHE.SOURCE.notIn(listOf(SourceCarto.GEOJSON, SourceCarto.OSM)))
            .and(COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId))
            .fetchInto<Couche>()
}
