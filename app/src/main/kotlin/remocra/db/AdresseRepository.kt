package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.Adresse
import remocra.db.jooq.remocra.tables.pojos.AdresseSousTypeElement
import remocra.db.jooq.remocra.tables.pojos.AdresseTypeAnomalie
import remocra.db.jooq.remocra.tables.references.ADRESSE
import remocra.db.jooq.remocra.tables.references.ADRESSE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ADRESSE_SOUS_TYPE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ADRESSE_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.ADRESSE_TYPE_ELEMENT
import java.util.UUID

class AdresseRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun insertAdresse(adresse: Adresse) {
        dsl.insertInto(ADRESSE).set(dsl.newRecord(ADRESSE, adresse)).execute()
    }

    fun getTypeAndSousType(): Collection<TypeSousTypeForMap> =
        dsl.select(
            ADRESSE_TYPE_ELEMENT.ID,
            ADRESSE_TYPE_ELEMENT.CODE,
            ADRESSE_TYPE_ELEMENT.LIBELLE,

            multiset(
                selectDistinct(
                    ADRESSE_SOUS_TYPE_ELEMENT.ID,
                    ADRESSE_SOUS_TYPE_ELEMENT.CODE,
                    ADRESSE_SOUS_TYPE_ELEMENT.LIBELLE,
                    ADRESSE_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE,
                )
                    .from(ADRESSE_SOUS_TYPE_ELEMENT)
                    .where(ADRESSE_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.eq(ADRESSE_TYPE_ELEMENT.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    SousTypeForMap(
                        adresseSousTypeElementId = r.value1(),
                        adresseSousTypeElementCode = r.value2(),
                        adresseSousTypeElementLibelle = r.value3(),
                        adresseSousTypeElementTypeGeom = r.value4(),
                    )
                }
            }.`as`("listSousType"),
        )
            .from(
                ADRESSE_TYPE_ELEMENT,
            )
            .fetchInto()

    fun getType(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            ADRESSE_TYPE_ELEMENT.ID.`as`("id"),
            ADRESSE_TYPE_ELEMENT.CODE.`as`("code"),
            ADRESSE_TYPE_ELEMENT.LIBELLE.`as`("libelle"),
        ).from(ADRESSE_TYPE_ELEMENT).fetchInto()

    fun getTypeAnomalie(): List<AdresseTypeAnomalie> = dsl.selectFrom(ADRESSE_TYPE_ANOMALIE).fetchInto()

    data class TypeSousTypeForMap(
        val adresseTypeElementId: UUID,
        val adresseTypeElementCode: String,
        val adresseTypeElementLibelle: String,
        val listSousType: List<SousTypeForMap>,
    )
    data class SousTypeForMap(
        val adresseSousTypeElementId: UUID?,
        val adresseSousTypeElementCode: String?,
        val adresseSousTypeElementLibelle: String?,
        val adresseSousTypeElementTypeGeom: TypeGeometry?,
    )

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<AdresseSousTypeElementForAdminData> =
        dsl.select(
            ADRESSE_SOUS_TYPE_ELEMENT.ID,
            ADRESSE_SOUS_TYPE_ELEMENT.CODE,
            ADRESSE_SOUS_TYPE_ELEMENT.LIBELLE,
            ADRESSE_SOUS_TYPE_ELEMENT.ACTIF,
            ADRESSE_TYPE_ELEMENT.LIBELLE,
            ADRESSE_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE,
        )
            .from(ADRESSE_SOUS_TYPE_ELEMENT)
            .leftJoin(ADRESSE_TYPE_ELEMENT).on(ADRESSE_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.eq(ADRESSE_TYPE_ELEMENT.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(ADRESSE_SOUS_TYPE_ELEMENT.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto<AdresseSousTypeElementForAdminData>().map { elmnt ->
                elmnt.copy(isUsed = isUsed(elmnt.adresseSousTypeElementId))
            }

    data class AdresseSousTypeElementForAdminData(
        val adresseSousTypeElementId: UUID,
        val adresseSousTypeElementCode: String,
        val adresseSousTypeElementLibelle: String?,
        val adresseSousTypeElementActif: Boolean,
        val adresseTypeElementLibelle: String?,
        val adresseSousTypeElementTypeGeometrie: TypeGeometry,
        val isUsed: Boolean = false,
    )

    fun countAllForAdmin(params: Params<Filter, Sort>): Int =
        dsl.selectCount().from(ADRESSE_SOUS_TYPE_ELEMENT).where(params.filterBy?.toCondition() ?: DSL.noCondition()).fetchSingleInto()

    data class Filter(
        val adresseSousTypeElementCode: String?,
        val adresseSousTypeElementLibelle: String?,
        val adresseSousTypeElementActif: Boolean?,
        val adresseTypeElementId: UUID?,
        val adresseSousTypeElementTypeGeometrie: TypeGeometry?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    adresseSousTypeElementCode?.let { DSL.and(ADRESSE_SOUS_TYPE_ELEMENT.CODE.containsIgnoreCaseUnaccent(it)) },
                    adresseSousTypeElementLibelle?.let { DSL.and(ADRESSE_SOUS_TYPE_ELEMENT.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    adresseSousTypeElementActif?.let { DSL.and(ADRESSE_SOUS_TYPE_ELEMENT.ACTIF.eq(it)) },
                    adresseTypeElementId?.let { DSL.and(ADRESSE_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.eq(it)) },
                    adresseSousTypeElementTypeGeometrie?.let { DSL.and(ADRESSE_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE.eq(it)) },
                ),
            )
    }

    data class Sort(
        val adresseSousTypeElementCode: Int?,
        val adresseSousTypeElementLibelle: Int?,
        val adresseSousTypeElementActif: Int?,
        val adresseTypeElementId: Int?,
        val adresseSousTypeElementTypeGeometrie: Int?,
    ) {
        fun toCondition(): List<SortField<*>> =
            listOfNotNull(
                ADRESSE_SOUS_TYPE_ELEMENT.CODE.getSortField(adresseSousTypeElementCode),
                ADRESSE_SOUS_TYPE_ELEMENT.LIBELLE.getSortField(adresseSousTypeElementLibelle),
                ADRESSE_SOUS_TYPE_ELEMENT.ACTIF.getSortField(adresseSousTypeElementActif),
                ADRESSE_SOUS_TYPE_ELEMENT.TYPE_ELEMENT.getSortField(adresseTypeElementId),
                ADRESSE_SOUS_TYPE_ELEMENT.TYPE_GEOMETRIE.getSortField(adresseSousTypeElementTypeGeometrie),
            )
    }

    fun insert(adresseSousTypeElement: AdresseSousTypeElement) =
        dsl.insertInto(ADRESSE_SOUS_TYPE_ELEMENT).set(dsl.newRecord(ADRESSE_SOUS_TYPE_ELEMENT, adresseSousTypeElement)).execute()

    fun update(adresseSousTypeElement: AdresseSousTypeElement) =
        dsl.update(ADRESSE_SOUS_TYPE_ELEMENT).set(dsl.newRecord(ADRESSE_SOUS_TYPE_ELEMENT, adresseSousTypeElement))
            .where(ADRESSE_SOUS_TYPE_ELEMENT.ID.eq(adresseSousTypeElement.adresseSousTypeElementId)).execute()

    fun checkCodeExists(code: String, id: UUID?) = dsl.fetchExists(
        dsl.select(ADRESSE_SOUS_TYPE_ELEMENT.CODE)
            .from(ADRESSE_SOUS_TYPE_ELEMENT)
            .where(ADRESSE_SOUS_TYPE_ELEMENT.CODE.equalIgnoreCase(code))
            .and(id?.let { DSL.and(ADRESSE_SOUS_TYPE_ELEMENT.ID.notEqual(id)) }),
    )

    fun getById(adresseSousTypeElementId: UUID): AdresseSousTypeElement =
        dsl.selectFrom(ADRESSE_SOUS_TYPE_ELEMENT).where(ADRESSE_SOUS_TYPE_ELEMENT.ID.eq(adresseSousTypeElementId)).fetchSingleInto()

    fun isUsed(adresseSousTypeElementId: UUID): Boolean = dsl.fetchExists(
        dsl.select(ADRESSE_ELEMENT.ID).from(ADRESSE_ELEMENT).where(ADRESSE_ELEMENT.SOUS_TYPE.eq(adresseSousTypeElementId)),
    )

    fun deleteById(adresseSousTypeElementId: UUID) =
        dsl.deleteFrom(ADRESSE_SOUS_TYPE_ELEMENT).where(ADRESSE_SOUS_TYPE_ELEMENT.ID.eq(adresseSousTypeElementId)).execute()
}
