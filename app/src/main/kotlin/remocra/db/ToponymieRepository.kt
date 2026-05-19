package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import remocra.data.GlobalData
import remocra.db.CriseRepository.ToponymieResult
import remocra.db.jooq.remocra.tables.pojos.TypeToponymie
import remocra.db.jooq.remocra.tables.references.CADASTRE_PARCELLE
import remocra.db.jooq.remocra.tables.references.CADASTRE_SECTION
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.LIEU_DIT
import remocra.db.jooq.remocra.tables.references.L_TOPONYMIE_CRISE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.TOPONYMIE
import remocra.db.jooq.remocra.tables.references.TYPE_TOPONYMIE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.utils.ST_Within
import java.util.UUID
import kotlin.collections.get

class ToponymieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getToponymieForSelect(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(TYPE_TOPONYMIE.ID.`as`("id"), TYPE_TOPONYMIE.CODE.`as`("code"), TYPE_TOPONYMIE.LIBELLE.`as`("libelle"))
            .from(TYPE_TOPONYMIE)
            .orderBy(TYPE_TOPONYMIE.LIBELLE)
            .fetchInto()

    fun insertLToponymieCrise(listeToponymieId: Collection<UUID>?, criseId: UUID) {
        dsl.batch(
            listeToponymieId?.map {
                DSL.insertInto(L_TOPONYMIE_CRISE)
                    .set(L_TOPONYMIE_CRISE.TYPE_TOPONYMIE_ID, it)
                    .set(L_TOPONYMIE_CRISE.CRISE_ID, criseId)
            },
        ).execute()
    }

    enum class ToponymieProtectedType(val toponymieLibelle: String) {
        COMMUNE("COMMUNE"),
        LIEU_DIT("LIEU_DIT"),
        PEI("PEI"),
        CADASTRE("CADASTRE"),
        ROUTES("ROUTES"),
    }

    fun getToponymiesProtegesQuery(
        proteges: Collection<TypeToponymie?>,
        globalGeometry: Field<Geometry?>?,
        libelleName: String,
    ): Collection<ToponymieResult> {
        val typeToTableMapping = mapOf(
            ToponymieProtectedType.COMMUNE.toponymieLibelle to listOf(Triple(COMMUNE.ID, COMMUNE.LIBELLE, COMMUNE.GEOMETRIE)),
            ToponymieProtectedType.LIEU_DIT.toponymieLibelle to listOf(Triple(LIEU_DIT.ID, LIEU_DIT.LIBELLE, LIEU_DIT.GEOMETRIE)),
            ToponymieProtectedType.PEI.toponymieLibelle to listOf(Triple(PEI.ID, PEI.NUMERO_COMPLET, PEI.GEOMETRIE)),
            ToponymieProtectedType.CADASTRE.toponymieLibelle to listOf(
                Triple(CADASTRE_SECTION.ID, DSL.concat(CADASTRE_SECTION.NUMERO, DSL.`val`(" ("), COMMUNE.LIBELLE, DSL.`val`(")")), CADASTRE_SECTION.GEOMETRIE),
                Triple(CADASTRE_PARCELLE.ID, DSL.concat(CADASTRE_SECTION.NUMERO, DSL.`val`(" "), CADASTRE_PARCELLE.NUMERO, DSL.`val`(" ("), COMMUNE.LIBELLE, DSL.`val`(")")), CADASTRE_PARCELLE.GEOMETRIE),
            ),
            ToponymieProtectedType.ROUTES.toponymieLibelle to listOf(Triple(VOIE.ID, VOIE.LIBELLE, VOIE.GEOMETRIE)),
        )

        return proteges
            .filter { it?.typeToponymieActif == true }
            .sortedBy { ToponymieProtectedType.entries.indexOfFirst { e -> e.toponymieLibelle == it?.typeToponymieCode } }
            .mapNotNull { typeToTableMapping[it?.typeToponymieCode] }
            .flatten()
            .flatMap { (id, libelle, geometrie) ->
                var joinQuery = dsl.select(id.`as`("toponymieId"), libelle.`as`("toponymieLibelle"), geometrie.`as`("toponymieGeometrie"))
                    .from(id.table)

                if (id.table == CADASTRE_PARCELLE) {
                    joinQuery = joinQuery.join(CADASTRE_SECTION).on(CADASTRE_PARCELLE.CADASTRE_SECTION_ID.eq(CADASTRE_SECTION.ID))
                        .join(COMMUNE).on(CADASTRE_SECTION.COMMUNE_ID.eq(COMMUNE.ID))
                } else if (id.table == CADASTRE_SECTION) {
                    joinQuery = joinQuery.join(COMMUNE).on(CADASTRE_SECTION.COMMUNE_ID.eq(COMMUNE.ID))
                }

                val baseQuery = joinQuery.where(
                    DSL.and(libelle.containsIgnoreCaseUnaccent(libelleName)),
                )

                val finalQuery = if (globalGeometry != null) {
                    baseQuery.and(ST_Within(geometrie, globalGeometry))
                } else {
                    baseQuery
                }
                finalQuery.orderBy(libelle.asc()).fetchInto()
            }
    }

    fun getOtherToponymiesQuery(
        globalGeometry: Field<Geometry?>?,
        libelleName: String,
        typeIds: Collection<UUID>,
    ): Collection<ToponymieResult> {
        val query = dsl.select(
            TOPONYMIE.ID,
            TOPONYMIE.LIBELLE,
            TOPONYMIE.GEOMETRIE,
        )
            .from(TOPONYMIE)
            .where(
                TOPONYMIE.TYPE_TOPONYMIE_ID.`in`(typeIds)
                    .and(TOPONYMIE.LIBELLE.containsIgnoreCaseUnaccent(libelleName)),
            )
        val filteredQuery = if (globalGeometry != null) {
            query.and(ST_Within(TOPONYMIE.GEOMETRIE, globalGeometry))
        } else {
            query
        }
        return filteredQuery.orderBy(TOPONYMIE.LIBELLE.asc())
            .fetchInto(ToponymieResult::class.java)
    }

    fun getByCode(typeToponymie: String): TypeToponymie? =
        dsl.selectFrom(TYPE_TOPONYMIE)
            .where(TYPE_TOPONYMIE.CODE.eq(typeToponymie))
            .fetchOneInto()
}
