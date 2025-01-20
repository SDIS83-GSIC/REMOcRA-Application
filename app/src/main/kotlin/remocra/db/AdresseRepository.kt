package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.Adresse
import remocra.db.jooq.remocra.tables.pojos.AdresseTypeAnomalie
import remocra.db.jooq.remocra.tables.references.ADRESSE
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
}
