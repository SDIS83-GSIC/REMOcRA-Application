package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.jooq.remocra.tables.pojos.Voie
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.VOIE

class VoieRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(codeInsee: String?, libelle: String?, limit: Int?, offset: Int?): Collection<Voie> =
        dsl.select(*VOIE.fields())
            .from(VOIE).innerJoin(COMMUNE).on(VOIE.COMMUNE_ID.eq(COMMUNE.ID))
            .where(getConditions(codeInsee, libelle))
            .limit(limit)
            .offset(offset)
            .fetchInto()

    fun getVoieForSelect(): List<IdCodeLibelleData> =
        dsl.select(VOIE.ID.`as`("id"), VOIE.LIBELLE.`as`("code"), VOIE.LIBELLE.`as`("libelle"))
            .from(VOIE)
            .orderBy(VOIE.LIBELLE)
            .fetchInto()

    private fun getConditions(codeInsee: String?, libelleVoie: String?): Condition {
        var condition: Condition = DSL.trueCondition()
        if (codeInsee != null) {
            condition = condition.and(COMMUNE.INSEE.eq(codeInsee))
        }
        if (libelleVoie != null) {
            condition = condition.and(VOIE.LIBELLE.likeIgnoreCase("%$libelleVoie%"))
        }
        return condition
    }
}
