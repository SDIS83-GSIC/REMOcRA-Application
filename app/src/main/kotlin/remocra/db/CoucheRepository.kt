package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.CoucheData
import remocra.data.CoucheFormData
import remocra.data.GlobalData
import remocra.data.Params
import remocra.data.couche.GroupeFonctionnalitesWithFlagLimiteZc
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
import kotlin.math.absoluteValue

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
                coucheTuilage = couche.coucheTuilage,
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

    fun getCouche(coucheId: UUID): CoucheFormData =
        dsl.select(
            COUCHE.ID,
            COUCHE.GROUPE_COUCHE_ID.`as`("groupeCoucheId"),
            COUCHE.CODE,
            COUCHE.LIBELLE,
            COUCHE.SOURCE,
            COUCHE.PROJECTION,
            COUCHE.URL,
            COUCHE.NOM,
            COUCHE.FORMAT,
            COUCHE.CROSS_ORIGIN,
            COUCHE.PUBLIC,
            COUCHE.ACTIVE,
            COUCHE.PROXY,
            COUCHE.PROTECTED,
            COUCHE.TUILAGE,
            COUCHE.ICONE,
            COUCHE.LEGENDE,
            // Multiset pour les groupes ZC
            DSL.multiset(
                dsl.select(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID)
                    .from(L_COUCHE_GROUPE_FONCTIONNALITES)
                    .where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(COUCHE.ID))
                    .and(L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC.isTrue),
            ).convertFrom { r -> r.map { it.value1() } }.`as`("groupeFonctionnalitesZcList"),
            // Multiset pour les groupes hors ZC
            DSL.multiset(
                dsl.select(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID)
                    .from(L_COUCHE_GROUPE_FONCTIONNALITES)
                    .where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(COUCHE.ID))
                    .and(L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC.isFalse),
            ).convertFrom { r -> r.map { it.value1() } }.`as`("groupeFonctionnalitesHorsZcList"),
            // Multiset pour les modules
            DSL.multiset(
                dsl.select(L_COUCHE_MODULE.MODULE_TYPE)
                    .from(L_COUCHE_MODULE)
                    .where(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID)),
            ).convertFrom { r -> r.map { it.value1() } }.`as`("moduleList"),
        )
            .from(COUCHE)
            .where(COUCHE.ID.eq(coucheId))
            .fetchSingleInto()

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
            .set(COUCHE.TUILAGE, couche.coucheTuilage)
            .set(COUCHE.ICONE, couche.coucheIcone)
            .set(COUCHE.LEGENDE, couche.coucheLegende)
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

    fun clearGroupeFonctionnalites(coucheId: UUID): Int =
        dsl.deleteFrom(L_COUCHE_GROUPE_FONCTIONNALITES).where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(coucheId)).execute()

    fun clearModule(coucheId: UUID): Int = dsl.deleteFrom(L_COUCHE_MODULE)
        .where(L_COUCHE_MODULE.COUCHE_ID.eq(coucheId)).execute()

    fun insertGroupeFonctionnalites(coucheId: UUID, groupeFonctionnalitesId: UUID, limiteZc: Boolean): Int =
        dsl.insertInto(L_COUCHE_GROUPE_FONCTIONNALITES)
            .set(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID, coucheId)
            .set(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID, groupeFonctionnalitesId)
            .set(L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC, limiteZc)
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

    fun countAllCoucheForAdmin(
        groupeCoucheId: UUID,
        filter: FilterCouche?,
    ): Int =
        dsl.select(DSL.countDistinct(COUCHE.ID))
            .from(COUCHE)
            .leftJoin(L_COUCHE_GROUPE_FONCTIONNALITES)
            .on(COUCHE.ID.eq(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID))
            .leftJoin(L_COUCHE_MODULE)
            .on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID))
            .where(
                COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId),
            ).and(filter?.toCondition())
            .fetchSingleInto<Int>()

    fun getAllCoucheForAdmin(
        groupeCoucheId: UUID,
        params: Params<FilterCouche, Sort>,
    ): List<remocra.data.couche.CoucheData> =
        dsl.selectDistinct(
            COUCHE.ID,
            COUCHE.CODE,
            COUCHE.LIBELLE,
            COUCHE.SOURCE,
            COUCHE.PROJECTION,
            COUCHE.URL,
            COUCHE.NOM,
            COUCHE.FORMAT,
            COUCHE.PUBLIC,
            COUCHE.ACTIVE,
            COUCHE.PROXY,
            COUCHE.PROTECTED,
            COUCHE.TUILAGE,
            COUCHE.ORDRE,
            DSL.multiset(
                dsl.select(L_COUCHE_MODULE.MODULE_TYPE)
                    .from(L_COUCHE_MODULE)
                    .where(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID)),
            ).convertFrom { r ->
                r.map { it.value1().toString() }.joinToString()
            }.`as`("moduleList"),
            DSL.multiset(
                dsl.select(GROUPE_FONCTIONNALITES.LIBELLE, L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC)
                    .from(GROUPE_FONCTIONNALITES)
                    .join(L_COUCHE_GROUPE_FONCTIONNALITES)
                    .on(GROUPE_FONCTIONNALITES.ID.eq(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID))
                    .where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(COUCHE.ID)),
            ).`as`("groupeFonctionnalitesWithFlagLimiteZc").convertFrom { r ->
                r.map {
                    GroupeFonctionnalitesWithFlagLimiteZc(
                        groupeFonctionnaliteId = it.getValue<String>(GROUPE_FONCTIONNALITES.LIBELLE),
                        limiteZc = it.getValue<Boolean>(L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC),
                    )
                }
            },

        )
            .from(COUCHE)
            .leftJoin(L_COUCHE_GROUPE_FONCTIONNALITES)
            .on(COUCHE.ID.eq(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID))
            .leftJoin(L_COUCHE_MODULE)
            .on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID))
            .where(
                COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId),
            ).and(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(COUCHE.ORDRE.desc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    data class FilterCouche(
        val coucheCode: String?,
        val coucheLibelle: String?,
        val coucheSource: String?,
        val coucheProjection: String?,
        val coucheUrl: String?,
        val coucheNom: String?,
        val coucheFormat: String?,
        val couchePublic: Boolean?,
        val coucheActive: Boolean?,
        val coucheProxy: Boolean?,
        val coucheProtected: Boolean?,
        val groupeFonctionnalitesHorsZc: List<UUID>?,
        val groupeFonctionnalitesZc: List<UUID>?,
        val moduleList: List<TypeModule>?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                coucheCode?.let { DSL.and(COUCHE.CODE.containsIgnoreCaseUnaccent(it)) },
                coucheLibelle?.let { DSL.and(COUCHE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                coucheProjection?.let { DSL.and(COUCHE.PROJECTION.containsIgnoreCaseUnaccent(it)) },
                coucheUrl?.let { DSL.and(COUCHE.URL.containsIgnoreCaseUnaccent(it)) },
                coucheNom?.let { DSL.and(COUCHE.NOM.containsIgnoreCaseUnaccent(it)) },
                coucheFormat?.let { DSL.and(COUCHE.FORMAT.containsIgnoreCaseUnaccent(it)) },
                couchePublic?.let { DSL.and(COUCHE.PUBLIC.eq(it)) },
                coucheActive?.let { DSL.and(COUCHE.ACTIVE.eq(it)) },
                coucheProxy?.let { DSL.and(COUCHE.PROXY.eq(it)) },
                coucheProtected?.let { DSL.and(COUCHE.PROTECTED.eq(it)) },
                groupeFonctionnalitesZc?.let { L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.`in`(it).and(L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC.isTrue) },
                groupeFonctionnalitesHorsZc?.let { L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.`in`(it).and(L_COUCHE_GROUPE_FONCTIONNALITES.LIMITE_ZC.isFalse) },
                moduleList?.let { L_COUCHE_MODULE.MODULE_TYPE.`in`(it) },
            ),
        )
    }

    data class Sort(
        val coucheCode: Int?,
        val coucheLibelle: Int?,
        val coucheSource: Int?,
        val coucheProjection: Int?,
        val coucheUrl: Int?,
        val coucheNom: Int?,
        val coucheFormat: Int?,
        val couchePublic: Int?,
        val coucheActive: Int?,
        val coucheProxy: Int?,
        val coucheCrossOrigin: Int?,
        val coucheProtected: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            coucheCode?.let { "coucheCode" to it },
            coucheLibelle?.let { "coucheLibelle" to it },
            coucheSource?.let { "coucheSource" to it },
            coucheProjection?.let { "coucheProjection" to it },
            coucheUrl?.let { "coucheUrl" to it },
            coucheNom?.let { "coucheNom" to it },
            coucheFormat?.let { "coucheFormat" to it },
            couchePublic?.let { "couchePublic" to it },
            coucheActive?.let { "coucheActive" to it },
            coucheProxy?.let { "coucheProxy" to it },
            coucheProtected?.let { "coucheProtected" to it },
        )
        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "coucheCode" -> COUCHE.CODE.getSortField(pair.second)
                "coucheLibelle" -> COUCHE.LIBELLE.getSortField(pair.second)
                "coucheOrdre" -> COUCHE.ORDRE.getSortField(pair.second)
                "coucheSource" -> COUCHE.SOURCE.getSortField(pair.second)
                "coucheProjection" -> COUCHE.PROJECTION.getSortField(pair.second)
                "coucheUrl" -> COUCHE.URL.getSortField(pair.second)
                "coucheNom" -> COUCHE.NOM.getSortField(pair.second)
                "coucheFormat" -> COUCHE.FORMAT.getSortField(pair.second)
                "couchePublic" -> COUCHE.PUBLIC.getSortField(pair.second)
                "coucheActive" -> COUCHE.ACTIVE.getSortField(pair.second)
                "coucheProxy" -> COUCHE.PROXY.getSortField(pair.second)
                "coucheProtected" -> COUCHE.PROTECTED.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun checkCodeExists(coucheCode: String, coucheId: UUID?) = dsl.fetchExists(
        dsl.select(COUCHE.CODE)
            .from(COUCHE)
            .where(COUCHE.CODE.equalIgnoreCase(coucheCode))
            .and(COUCHE.ID.notEqual(coucheId)),
    )

    fun getLastOrdre(groupeCoucheId: UUID): Int? =
        dsl.select(DSL.max(COUCHE.ORDRE)).from(COUCHE).where(COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId)).fetchOneInto()

    fun deleteCouche(coucheId: UUID) {
        dsl.deleteFrom(COUCHE)
            .where(COUCHE.ID.eq(coucheId))
            .execute()
    }

    fun updateCoucheOrdre(listeObjet: List<UUID>) {
        listeObjet.forEachIndexed { index, id ->
            dsl.update(COUCHE)
                .set(COUCHE.ORDRE, listeObjet.size - index + 1)
                .where(COUCHE.ID.eq(id))
                .execute()
        }
    }

    fun getOrdreCouche(groupeCoucheId: UUID): List<GlobalData.IdCodeLibelleData> =
        dsl.select(COUCHE.ID.`as`("id"), COUCHE.CODE.`as`("code"), COUCHE.LIBELLE.`as`("libelle"))
            .from(COUCHE)
            .where(COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId))
            .orderBy(COUCHE.ORDRE.desc())
            .fetchInto()
}
