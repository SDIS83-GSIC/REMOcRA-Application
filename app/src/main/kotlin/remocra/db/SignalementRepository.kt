package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.Signalement
import remocra.db.jooq.remocra.tables.pojos.SignalementSousTypeElement
import remocra.db.jooq.remocra.tables.pojos.SignalementTypeAnomalie
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT_ELEMENT
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT_SOUS_TYPE_ELEMENT
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT_TYPE_ELEMENT
import java.util.UUID

class SignalementRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun insertSignalement(signalement: Signalement) {
        dsl.insertInto(SIGNALEMENT).set(dsl.newRecord(SIGNALEMENT, signalement)).execute()
    }

    fun getTypeAndSousType(): Collection<TypeSousTypeForMap> =
        dsl.select(
            SIGNALEMENT_TYPE_ELEMENT.ID,
            SIGNALEMENT_TYPE_ELEMENT.CODE,
            SIGNALEMENT_TYPE_ELEMENT.LIBELLE,

            multiset(
                selectDistinct(
                    SIGNALEMENT_SOUS_TYPE_ELEMENT.ID,
                    SIGNALEMENT_SOUS_TYPE_ELEMENT.CODE,
                    SIGNALEMENT_SOUS_TYPE_ELEMENT.LIBELLE,
                    SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE,
                )
                    .from(SIGNALEMENT_SOUS_TYPE_ELEMENT)
                    .where(SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.eq(SIGNALEMENT_TYPE_ELEMENT.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    SousTypeForMap(
                        signalementSousTypeElementId = r.value1(),
                        signalementSousTypeElementCode = r.value2(),
                        signalementSousTypeElementLibelle = r.value3(),
                        signalementSousTypeElementTypeGeom = r.value4(),
                    )
                }
            }.`as`("listSousType"),
        )
            .from(
                SIGNALEMENT_TYPE_ELEMENT,
            )
            .fetchInto()

    fun getType(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            SIGNALEMENT_TYPE_ELEMENT.ID.`as`("id"),
            SIGNALEMENT_TYPE_ELEMENT.CODE.`as`("code"),
            SIGNALEMENT_TYPE_ELEMENT.LIBELLE.`as`("libelle"),
        ).from(SIGNALEMENT_TYPE_ELEMENT).fetchInto()

    fun getTypeAnomalie(): List<SignalementTypeAnomalie> = dsl.selectFrom(SIGNALEMENT_TYPE_ANOMALIE).fetchInto()

    data class TypeSousTypeForMap(
        val signalementTypeElementId: UUID,
        val signalementTypeElementCode: String,
        val signalementTypeElementLibelle: String,
        val listSousType: List<SousTypeForMap>,
    )
    data class SousTypeForMap(
        val signalementSousTypeElementId: UUID?,
        val signalementSousTypeElementCode: String?,
        val signalementSousTypeElementLibelle: String?,
        val signalementSousTypeElementTypeGeom: TypeGeometry?,
    )

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<SignalementSousTypeElementForAdminData> =
        dsl.select(
            SIGNALEMENT_SOUS_TYPE_ELEMENT.ID,
            SIGNALEMENT_SOUS_TYPE_ELEMENT.CODE,
            SIGNALEMENT_SOUS_TYPE_ELEMENT.LIBELLE,
            SIGNALEMENT_SOUS_TYPE_ELEMENT.ACTIF,
            SIGNALEMENT_SOUS_TYPE_ELEMENT.LIBELLE,
            SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE,
        )
            .from(SIGNALEMENT_SOUS_TYPE_ELEMENT)
            .leftJoin(SIGNALEMENT_TYPE_ELEMENT).on(SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.eq(SIGNALEMENT_TYPE_ELEMENT.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(SIGNALEMENT_SOUS_TYPE_ELEMENT.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto<SignalementSousTypeElementForAdminData>().map { elmnt ->
                elmnt.copy(isUsed = isUsed(elmnt.signalementSousTypeElementId))
            }

    data class SignalementSousTypeElementForAdminData(
        val signalementSousTypeElementId: UUID,
        val signalementSousTypeElementCode: String,
        val signalementSousTypeElementLibelle: String?,
        val signalementSousTypeElementActif: Boolean,
        val signalementTypeElementLibelle: String?,
        val signalementSousTypeElementTypeGeometrie: TypeGeometry,
        val isUsed: Boolean = false,
    )

    fun countAllForAdmin(params: Params<Filter, Sort>): Int =
        dsl.selectCount().from(SIGNALEMENT_SOUS_TYPE_ELEMENT).where(params.filterBy?.toCondition() ?: DSL.noCondition()).fetchSingleInto()

    data class Filter(
        val signalementSousTypeElementCode: String?,
        val signalementSousTypeElementLibelle: String?,
        val signalementSousTypeElementActif: Boolean?,
        val signalementTypeElementId: UUID?,
        val signalementSousTypeElementTypeGeometrie: TypeGeometry?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    signalementSousTypeElementCode?.let { DSL.and(SIGNALEMENT_SOUS_TYPE_ELEMENT.CODE.containsIgnoreCaseUnaccent(it)) },
                    signalementSousTypeElementLibelle?.let { DSL.and(SIGNALEMENT_SOUS_TYPE_ELEMENT.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    signalementSousTypeElementActif?.let { DSL.and(SIGNALEMENT_SOUS_TYPE_ELEMENT.ACTIF.eq(it)) },
                    signalementTypeElementId?.let { DSL.and(SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.eq(it)) },
                    signalementSousTypeElementTypeGeometrie?.let { DSL.and(SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE.eq(it)) },
                ),
            )
    }

    data class Sort(
        val signalementSousTypeElementCode: Int?,
        val signalementSousTypeElementLibelle: Int?,
        val signalementSousTypeElementActif: Int?,
        val signalementTypeElementId: Int?,
        val signalementSousTypeElementTypeGeometrie: Int?,
    ) {
        fun toCondition(): List<SortField<*>> =
            listOfNotNull(
                SIGNALEMENT_SOUS_TYPE_ELEMENT.CODE.getSortField(signalementSousTypeElementCode),
                SIGNALEMENT_SOUS_TYPE_ELEMENT.LIBELLE.getSortField(signalementSousTypeElementLibelle),
                SIGNALEMENT_SOUS_TYPE_ELEMENT.ACTIF.getSortField(signalementSousTypeElementActif),
                SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.getSortField(signalementTypeElementId),
                SIGNALEMENT_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE.getSortField(signalementSousTypeElementTypeGeometrie),
            )
    }

    fun insert(signalementSousTypeElement: SignalementSousTypeElement) =
        dsl.insertInto(SIGNALEMENT_SOUS_TYPE_ELEMENT).set(dsl.newRecord(SIGNALEMENT_SOUS_TYPE_ELEMENT, signalementSousTypeElement)).execute()

    fun update(signalementSousTypeElement: SignalementSousTypeElement) =
        dsl.update(SIGNALEMENT_SOUS_TYPE_ELEMENT).set(dsl.newRecord(SIGNALEMENT_SOUS_TYPE_ELEMENT, signalementSousTypeElement))
            .where(SIGNALEMENT_SOUS_TYPE_ELEMENT.ID.eq(signalementSousTypeElement.signalementSousTypeElementId)).execute()

    fun checkCodeExists(code: String, id: UUID?) = dsl.fetchExists(
        dsl.select(SIGNALEMENT_SOUS_TYPE_ELEMENT.CODE)
            .from(SIGNALEMENT_SOUS_TYPE_ELEMENT)
            .where(SIGNALEMENT_SOUS_TYPE_ELEMENT.CODE.equalIgnoreCase(code))
            .and(id?.let { DSL.and(SIGNALEMENT_SOUS_TYPE_ELEMENT.ID.notEqual(id)) }),
    )

    fun getById(signalementSousTypeElementId: UUID): SignalementSousTypeElement =
        dsl.selectFrom(SIGNALEMENT_SOUS_TYPE_ELEMENT).where(SIGNALEMENT_SOUS_TYPE_ELEMENT.ID.eq(signalementSousTypeElementId)).fetchSingleInto()

    fun isUsed(signalementSousTypeElementId: UUID): Boolean = dsl.fetchExists(
        dsl.select(SIGNALEMENT_ELEMENT.ID).from(SIGNALEMENT_ELEMENT).where(SIGNALEMENT_ELEMENT.SOUS_TYPE.eq(signalementSousTypeElementId)),
    )

    fun deleteById(signalementSousTypeElementId: UUID) =
        dsl.deleteFrom(SIGNALEMENT_SOUS_TYPE_ELEMENT).where(SIGNALEMENT_SOUS_TYPE_ELEMENT.ID.eq(signalementSousTypeElementId)).execute()
}
