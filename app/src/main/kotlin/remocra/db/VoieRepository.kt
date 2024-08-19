package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.tables.pojos.Voie
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.utils.ST_DistanceInferieurStrict
import java.util.UUID

class VoieRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(codeInsee: String?, libelle: String?, limit: Int?, offset: Int?): Collection<Voie> =
        dsl.select(*VOIE.fields())
            .from(VOIE).innerJoin(COMMUNE).on(VOIE.COMMUNE_ID.eq(COMMUNE.ID))
            .where(getConditions(codeInsee, libelle))
            .limit(limit)
            .offset(offset)
            .fetchInto()

    fun getVoies(
        coordonneeX: String,
        coordonneeY: String,
        srid: Int,
        toleranceVoiesMetres: Int,
        listeIdCommune: List<UUID>,
    ): List<VoieWithCommune> =
        dsl.select(
            VOIE.ID.`as`("id"),
            VOIE.LIBELLE.`as`("code"),
            VOIE.LIBELLE.`as`("libelle"),
            VOIE.COMMUNE_ID.`as`("communeId"),
        )
            .from(VOIE)
            .where(VOIE.COMMUNE_ID.`in`(listeIdCommune))
            .ST_DistanceInferieurStrict(VOIE.GEOMETRIE, srid, coordonneeX.toDouble(), coordonneeY.toDouble(), toleranceVoiesMetres)
            .orderBy(VOIE.LIBELLE)
            .fetchInto()

    data class VoieWithCommune(
        val id: UUID,
        val code: String,
        val libelle: String,
        val communeId: UUID,
    )

    private fun getConditions(codeInsee: String?, libelleVoie: String?): Condition {
        var condition: Condition = DSL.trueCondition()
        if (codeInsee != null) {
            condition = condition.and(COMMUNE.CODE_INSEE.eq(codeInsee))
        }
        if (libelleVoie != null) {
            condition = condition.and(VOIE.LIBELLE.likeIgnoreCase("%$libelleVoie%"))
        }
        return condition
    }
}
