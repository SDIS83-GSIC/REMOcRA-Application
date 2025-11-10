package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import remocra.data.CoucheData
import remocra.data.CoucheStyle
import remocra.data.CoucheStyleInput
import remocra.data.GroupeFonctionnaliteList
import remocra.data.Params
import remocra.data.ResponseCouche
import remocra.db.jooq.remocra.enums.SourceCarto
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.COUCHE_METADATA
import remocra.db.jooq.remocra.tables.references.GROUPE_COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_COUCHE_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_COUCHE_MODULE
import remocra.db.jooq.remocra.tables.references.L_GROUPE_FONCTIONNALITES_COUCHE_METADATA
import java.util.UUID

class CoucheRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    // XXX: Retourner un type plus spécifique, avec uniquement les champs utiles
    fun getCouche(code: String, module: TypeModule, groupeFonctionnalites: GroupeFonctionnalites?, isSuperAdmin: Boolean? = false): Couche? =
        dsl.select(*COUCHE.fields())
            .from(COUCHE)
            .join(L_COUCHE_MODULE).on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID))
            .where(COUCHE.CODE.eq(code))
            .and(L_COUCHE_MODULE.MODULE_TYPE.eq(module))
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    COUCHE.PUBLIC.isTrue.or(
                        groupeFonctionnalites?.let {
                            DSL.exists(
                                DSL.select(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID)
                                    .from(L_COUCHE_GROUPE_FONCTIONNALITES)
                                    .where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(COUCHE.ID))
                                    .and(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(it.groupeFonctionnalitesId)),
                            )
                        },
                    ),
                    isSuperAdmin == true,
                ),
            )
            .fetchOneInto<Couche>()

    data class FilterLayerStyle(
        val groupeCoucheLibelle: String?,
        val coucheLibelle: String?,
        val coucheMetadataActif: Boolean?,
        val coucheMetadataPublic: Boolean?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    groupeCoucheLibelle?.let { DSL.and(GROUPE_COUCHE.LIBELLE.contains(it)) },
                    coucheLibelle?.let { DSL.and(COUCHE.LIBELLE.contains(it)) },
                    coucheMetadataActif?.let { DSL.and(COUCHE_METADATA.ACTIF.eq(it)) },
                    coucheMetadataPublic?.let { DSL.and(COUCHE_METADATA.PUBLIC.eq(it)) },
                ),
            )
    }

    data class SortLayer(
        val groupeCoucheLibelle: Int?,
        val coucheLibelle: Int?,
        val coucheMetadataActif: Int?,
        val coucheMetadataPublic: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            GROUPE_COUCHE.LIBELLE.getSortField(groupeCoucheLibelle),
            COUCHE.LIBELLE.getSortField(coucheLibelle),
            COUCHE_METADATA.ACTIF.getSortField(coucheMetadataActif),
            COUCHE_METADATA.PUBLIC.getSortField(coucheMetadataPublic),
        )
    }

    fun getAllPublicStyles(): List<CoucheStyle> =
        dsl.select(
            COUCHE_METADATA.ACTIF.`as`("layerStyleFlag"),
            COUCHE_METADATA.STYLE.`as`("layerStyle"),
            COUCHE_METADATA.COUCHE_ID.`as`("layerId"),
            COUCHE_METADATA.PUBLIC.`as`("layerStylePublicAccess"),
            COUCHE.GROUPE_COUCHE_ID.`as`("groupLayerId"),
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE.ID.eq(COUCHE_METADATA.COUCHE_ID))
            .where(COUCHE_METADATA.PUBLIC.isTrue)
            .fetchInto<CoucheStyle>()

    fun getAllStylesByUserId(groupeFonctionnaliteId: UUID): List<CoucheStyle> =
        dsl.select(
            COUCHE_METADATA.ACTIF.`as`("layerStyleFlag"),
            COUCHE_METADATA.STYLE.`as`("layerStyle"),
            COUCHE_METADATA.COUCHE_ID.`as`("layerId"),
            COUCHE_METADATA.PUBLIC.`as`("layerStylePublicAccess"),
            COUCHE.GROUPE_COUCHE_ID.`as`("groupLayerId"),
            multiset(
                DSL.select(
                    GROUPE_FONCTIONNALITES.ID.`as`("profilId"),
                    GROUPE_FONCTIONNALITES.LIBELLE.`as`("profilLibelle"),
                )
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .join(GROUPE_FONCTIONNALITES)
                    .on(GROUPE_FONCTIONNALITES.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID))
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(COUCHE_METADATA.ID))
                    .and(GROUPE_FONCTIONNALITES.ID.eq(groupeFonctionnaliteId)),
            ).convertFrom { records ->
                records.map { (id) -> id as UUID }
            }.`as`("layerProfilId"),
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE.ID.eq(COUCHE_METADATA.COUCHE_ID))
            .fetchInto<CoucheStyle>()

    fun getCountStyles(filterBy: FilterLayerStyle?): Int =
        dsl.selectCount()
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE_METADATA.COUCHE_ID.eq(COUCHE.ID))
            .join(GROUPE_COUCHE).on(COUCHE.GROUPE_COUCHE_ID.eq(GROUPE_COUCHE.ID))
            .whereExists(
                DSL.selectOne()
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(COUCHE_METADATA.ID)),
            )
            .and(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto()

    fun getStyleById(styleId: UUID): CoucheStyle? =
        dsl.select(
            COUCHE_METADATA.ACTIF.`as`("layerStyleFlag"),
            COUCHE_METADATA.STYLE.`as`("layerStyle"),
            COUCHE_METADATA.PUBLIC.`as`("layerStylePublicAccess"),
            COUCHE_METADATA.COUCHE_ID.`as`("layerId"),
            COUCHE.GROUPE_COUCHE_ID.`as`("groupLayerId"),
            multiset(
                DSL.select(
                    GROUPE_FONCTIONNALITES.ID.`as`("profilId"),
                    GROUPE_FONCTIONNALITES.LIBELLE.`as`("profilLibelle"),
                )
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .join(GROUPE_FONCTIONNALITES).on(GROUPE_FONCTIONNALITES.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID))
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(styleId)),
            ).convertFrom { records ->
                records.map { (id) -> id as UUID }
            }.`as`("layerProfilId"),
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE.ID.eq(COUCHE_METADATA.COUCHE_ID))
            .where(COUCHE_METADATA.ID.eq(styleId))
            .fetchOneInto<CoucheStyle>()

    fun getCouchesParams(params: Params<FilterLayerStyle, SortLayer>): List<ResponseCouche> =
        dsl.select(
            GROUPE_COUCHE.ID.`as`("groupeCoucheId"),
            GROUPE_COUCHE.LIBELLE.`as`("groupeCoucheLibelle"),
            COUCHE.ID.`as`("coucheId"),
            COUCHE.LIBELLE.`as`("coucheLibelle"),
            COUCHE_METADATA.ACTIF.`as`("coucheMetadataActif"),
            COUCHE_METADATA.PUBLIC.`as`("coucheMetadataPublic"),
            COUCHE_METADATA.ID.`as`("styleId"),
            multiset(
                DSL.select(
                    GROUPE_FONCTIONNALITES.ID.`as`("profilId"),
                    GROUPE_FONCTIONNALITES.LIBELLE.`as`("profilLibelle"),
                )
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .join(GROUPE_FONCTIONNALITES).on(GROUPE_FONCTIONNALITES.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID))
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(COUCHE_METADATA.ID)),
            ).convertFrom { records ->
                records.map { (id, libelle) ->
                    GroupeFonctionnaliteList(
                        profilId = id as UUID,
                        profilLibelle = libelle,
                    )
                }
            }.`as`("groupeFonctionnaliteList"),
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE_METADATA.COUCHE_ID.eq(COUCHE.ID))
            .join(GROUPE_COUCHE).on(COUCHE.GROUPE_COUCHE_ID.eq(GROUPE_COUCHE.ID))
            .whereExists(
                DSL.selectOne()
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(COUCHE_METADATA.ID)),
            )
            .and(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition())
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto(ResponseCouche::class.java)

    fun upsertCoucheStyle(couche: CoucheStyleInput) {
        dsl.insertInto(COUCHE_METADATA)
            .set(COUCHE_METADATA.ID, couche.layerStyleId)
            .set(COUCHE_METADATA.COUCHE_ID, couche.layerId)
            .set(COUCHE_METADATA.STYLE, couche.layerStyle)
            .set(COUCHE_METADATA.ACTIF, couche.layerStyleFlag)
            .set(COUCHE_METADATA.PUBLIC, couche.layerStylePublicAccess)
            .onConflict(COUCHE_METADATA.ID)
            .doUpdate()
            .set(COUCHE_METADATA.COUCHE_ID, couche.layerId)
            .set(COUCHE_METADATA.STYLE, couche.layerStyle)
            .set(COUCHE_METADATA.ACTIF, couche.layerStyleFlag)
            .set(COUCHE_METADATA.PUBLIC, couche.layerStylePublicAccess)
            .execute()
    }

    fun deleteCoucheStyleByStyleId(styleId: UUID) =
        dsl.deleteFrom(COUCHE_METADATA).where(COUCHE_METADATA.ID.eq(styleId)).execute()

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
                coucheProtected = couche.coucheProtected ?: false,
            )
        }.associateBy { it.coucheId }

    fun checkIfLayerHasStyle(coucheId: UUID): Boolean {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(COUCHE_METADATA)
                .where(COUCHE_METADATA.COUCHE_ID.eq(coucheId))
                .and(COUCHE_METADATA.ACTIF.eq(true)),
        )
    }

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

    fun getAvailableGroupeFonctionnaliteList(coucheId: UUID, excludeExisting: Boolean = false): List<GroupeFonctionnalites> {
        var query = dsl.select(*GROUPE_FONCTIONNALITES.fields())
            .from(GROUPE_FONCTIONNALITES)
            .join(L_COUCHE_GROUPE_FONCTIONNALITES).on(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .leftJoin(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA).on(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .where(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(coucheId))

        if (excludeExisting) {
            query = query.and(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID.isNull)
        }

        return query.fetchInto<GroupeFonctionnalites>()
    }

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
