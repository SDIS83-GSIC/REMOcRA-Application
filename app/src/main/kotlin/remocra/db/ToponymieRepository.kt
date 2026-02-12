package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.db.CriseRepository.ToponymieResult
import remocra.db.jooq.remocra.tables.pojos.TypeToponymie
import remocra.db.jooq.remocra.tables.references.CADASTRE_SECTION
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.LIEU_DIT
import remocra.db.jooq.remocra.tables.references.L_TOPONYMIE_CRISE
import remocra.db.jooq.remocra.tables.references.PEI
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
        globalGeometry: Field<org.locationtech.jts.geom.Geometry?>?,
        libelleName: String,
    ): Collection<ToponymieResult> {
        val typeToTableMapping = mapOf(
            ToponymieProtectedType.COMMUNE.toponymieLibelle to Triple(COMMUNE.ID, COMMUNE.LIBELLE, COMMUNE.GEOMETRIE),
            ToponymieProtectedType.LIEU_DIT.toponymieLibelle to Triple(LIEU_DIT.ID, LIEU_DIT.LIBELLE, LIEU_DIT.GEOMETRIE),
            ToponymieProtectedType.PEI.toponymieLibelle to Triple(PEI.ID, PEI.NUMERO_COMPLET, PEI.GEOMETRIE),
            ToponymieProtectedType.CADASTRE.toponymieLibelle to Triple(CADASTRE_SECTION.ID, CADASTRE_SECTION.NUMERO, CADASTRE_SECTION.GEOMETRIE),
            ToponymieProtectedType.ROUTES.toponymieLibelle to Triple(VOIE.ID, VOIE.LIBELLE, VOIE.GEOMETRIE),
        )

        return proteges
            .filter { it?.typeToponymieActif == true }
            .mapNotNull { typeToTableMapping[it?.typeToponymieCode] }
            .flatMap { (id, libelle, geometrie) ->
                val baseQuery = dsl.select(id.`as`("toponymieId"), libelle.`as`("toponymieLibelle"), geometrie.`as`("toponymieGeometrie"))
                    .from(id.table)
                    .where(libelle.containsIgnoreCaseUnaccent(libelleName))

                val finalQuery = if (globalGeometry != null) {
                    baseQuery.and(ST_Within(geometrie, globalGeometry))
                } else {
                    baseQuery
                }
                finalQuery.fetchInto()
            }
    }

    fun getByCode(typeToponymie: String): TypeToponymie? =
        dsl.selectFrom(TYPE_TOPONYMIE)
            .where(TYPE_TOPONYMIE.CODE.eq(typeToponymie))
            .fetchOneInto()
}
