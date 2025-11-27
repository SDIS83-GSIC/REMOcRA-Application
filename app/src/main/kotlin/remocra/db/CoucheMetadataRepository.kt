package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import remocra.data.CoucheMetadata
import remocra.data.GroupeFonctionnalite
import remocra.data.Params
import remocra.data.ResponseCouche
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.COUCHE_METADATA
import remocra.db.jooq.remocra.tables.references.GROUPE_COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_COUCHE_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_GROUPE_FONCTIONNALITES_COUCHE_METADATA
import java.util.UUID

class CoucheMetadataRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    data class FilterCoucheMetadata(
        val groupeCoucheLibelle: String?,
        val coucheLibelle: String?,
        val coucheMetadataActif: Boolean?,
        val coucheMetadataPublic: Boolean?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                groupeCoucheLibelle?.let { DSL.and(GROUPE_COUCHE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                coucheLibelle?.let { DSL.and(COUCHE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                coucheMetadataActif?.let { DSL.and(COUCHE_METADATA.ACTIF.eq(it)) },
                coucheMetadataPublic?.let { DSL.and(COUCHE_METADATA.PUBLIC.eq(it)) },
            ),
        )
    }

    fun upsertCoucheMetadata(couche: CoucheMetadata) {
        dsl.insertInto(COUCHE_METADATA)
            .set(COUCHE_METADATA.ID, couche.coucheMetadataId)
            .set(COUCHE_METADATA.COUCHE_ID, couche.coucheId)
            .set(COUCHE_METADATA.STYLE, couche.coucheMetadataStyle)
            .set(COUCHE_METADATA.ACTIF, couche.coucheMetadataActif)
            .set(COUCHE_METADATA.PUBLIC, couche.coucheMetadataPublic)
            .onConflict(COUCHE_METADATA.ID)
            .doUpdate()
            .set(COUCHE_METADATA.COUCHE_ID, couche.coucheId)
            .set(COUCHE_METADATA.STYLE, couche.coucheMetadataStyle)
            .set(COUCHE_METADATA.ACTIF, couche.coucheMetadataActif)
            .set(COUCHE_METADATA.PUBLIC, couche.coucheMetadataPublic)
            .execute()
    }

    /**
     * Retourne VRAI si des metadata existent pour la couche
     */
    fun checkCoucheMetadata(coucheId: UUID): Boolean {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(COUCHE_METADATA)
                .where(COUCHE_METADATA.COUCHE_ID.eq(coucheId))
                .and(COUCHE_METADATA.ACTIF.eq(true)),
        )
    }

    fun getAvailableGroupeFonctionnaliteList(coucheId: UUID, coucheMetadataId: UUID?): List<GroupeFonctionnalites> {
        val subquery = dsl.select(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID)
            .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
            .join(COUCHE_METADATA)
            .on(COUCHE_METADATA.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID))
            .where(COUCHE_METADATA.COUCHE_ID.eq(coucheId))
            .let { sq ->
                if (coucheMetadataId != null) {
                    sq.and(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.ne(coucheMetadataId))
                } else {
                    sq
                }
            }

        return dsl.select(*GROUPE_FONCTIONNALITES.fields())
            .from(GROUPE_FONCTIONNALITES)
            .join(L_COUCHE_GROUPE_FONCTIONNALITES)
            .on(L_COUCHE_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .and(L_COUCHE_GROUPE_FONCTIONNALITES.COUCHE_ID.eq(coucheId))
            .where(GROUPE_FONCTIONNALITES.ACTIF.isTrue)
            .and(
                coucheMetadataId?.let {
                    GROUPE_FONCTIONNALITES.ID.notIn(subquery)
                        .or(
                            GROUPE_FONCTIONNALITES.ID.`in`(
                                DSL.select(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID)
                                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(it)),
                            ),
                        )
                } ?: GROUPE_FONCTIONNALITES.ID.notIn(subquery),
            )
            .fetchInto()
    }

    fun getCountCoucheMetadata(filterBy: FilterCoucheMetadata?): Int =
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

    fun getCoucheMetadataById(coucheMetadataId: UUID): CoucheMetadata =
        dsl.select(
            COUCHE_METADATA.PUBLIC,
            COUCHE_METADATA.ID,
            COUCHE_METADATA.ACTIF,
            COUCHE_METADATA.STYLE,
            COUCHE_METADATA.COUCHE_ID.`as`("coucheId"),
            COUCHE.GROUPE_COUCHE_ID.`as`("groupeCoucheId"),
            multiset(
                DSL.select(
                    GROUPE_FONCTIONNALITES.ID,
                )
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .join(GROUPE_FONCTIONNALITES).on(GROUPE_FONCTIONNALITES.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID))
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(coucheMetadataId)),
            ).convertFrom { records ->
                records.map { (id) -> id as UUID }
            }.`as`("groupeFonctionnaliteIds"),
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE.ID.eq(COUCHE_METADATA.COUCHE_ID))
            .where(COUCHE_METADATA.ID.eq(coucheMetadataId))
            .fetchSingleInto<CoucheMetadata>()

    fun deleteCouchesMetadataByMetadataId(metadataId: UUID) =
        dsl.deleteFrom(COUCHE_METADATA).where(COUCHE_METADATA.ID.eq(metadataId)).execute()

    fun getCouchesMetadataForTableau(params: Params<FilterCoucheMetadata, SortCouche>): List<ResponseCouche> =
        dsl.select(
            GROUPE_COUCHE.ID,
            GROUPE_COUCHE.LIBELLE,
            COUCHE.ID,
            COUCHE.LIBELLE,
            COUCHE_METADATA.ID,
            COUCHE_METADATA.ACTIF,
            COUCHE_METADATA.PUBLIC,
            multiset(
                DSL.select(
                    GROUPE_FONCTIONNALITES.ID,
                    GROUPE_FONCTIONNALITES.CODE,
                    GROUPE_FONCTIONNALITES.LIBELLE,
                )
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .join(GROUPE_FONCTIONNALITES).on(GROUPE_FONCTIONNALITES.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID))
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(COUCHE_METADATA.ID)),
            ).convertFrom { records ->
                records.map { (id, code, libelle) ->
                    GroupeFonctionnalite(
                        groupeFonctionnaliteId = id as UUID,
                        groupeFonctionnaliteCode = code!!,
                        groupeFonctionnaliteLibelle = libelle!!,
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

    /**
     * Retourne les metadata des couches publiques (pour les utilisateurs non connectés)
     */
    fun getPublicCoucheMetadata(): List<CoucheMetadata> =
        dsl.select(
            COUCHE_METADATA.ACTIF,
            COUCHE_METADATA.STYLE,
            COUCHE_METADATA.COUCHE_ID,
            COUCHE.GROUPE_COUCHE_ID,
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE.ID.eq(COUCHE_METADATA.COUCHE_ID))
            .where(COUCHE_METADATA.PUBLIC.isTrue)
            .fetchInto<CoucheMetadata>()

    fun getAllCoucheMetadataByUserId(groupeFonctionnaliteId: UUID): List<CoucheMetadata> =
        dsl.select(
            COUCHE_METADATA.ACTIF,
            COUCHE_METADATA.STYLE,
            COUCHE_METADATA.COUCHE_ID.`as`("coucheId"),
            COUCHE.GROUPE_COUCHE_ID.`as`("groupeCoucheId"),
            multiset(
                DSL.select(
                    GROUPE_FONCTIONNALITES.ID.`as`("groupeFonctionnaliteId"),
                )
                    .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
                    .join(GROUPE_FONCTIONNALITES)
                    .on(GROUPE_FONCTIONNALITES.ID.eq(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID))
                    .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(COUCHE_METADATA.ID))
                    .and(GROUPE_FONCTIONNALITES.ID.eq(groupeFonctionnaliteId)),
            ).convertFrom { records ->
                records.map { (id) -> id as UUID }
            }.`as`("groupeFonctionnaliteIds"),
        )
            .from(COUCHE_METADATA)
            .join(COUCHE).on(COUCHE.ID.eq(COUCHE_METADATA.COUCHE_ID))
            .fetchInto<CoucheMetadata>()

    data class SortCouche(
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

    fun deleteLienGroupeFonctionnalites(coucheMetadataId: UUID) =
        dsl.deleteFrom(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
            .where(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(coucheMetadataId))
            .execute()

    fun addLienGroupeFonctionnalites(layerId: UUID, profilId: UUID): Int =
        dsl.insertInto(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
            .set(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID, profilId)
            .set(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID, layerId)
            .execute()

    fun checkLienGroupeFonctionnalites(layerId: UUID, profilId: UUID) = dsl.fetchExists(
        dsl.select(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID)
            .from(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA)
            .where(
                L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.GROUPE_FONCTIONNALITES_ID.eq(profilId)
                    .and(L_GROUPE_FONCTIONNALITES_COUCHE_METADATA.COUCHE_METADATA_ID.eq(layerId)),
            ),
    )
}
