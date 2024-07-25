package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.Commune
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.utils.ST_DWithin
import java.util.UUID

class CommuneRepository @Inject constructor(private val dsl: DSLContext) {
    fun getMapById(): Map<UUID, Commune> = dsl.selectFrom(COMMUNE).fetchInto<Commune>().associateBy { it.communeId }

    fun getAll(codeInsee: String?, libelle: String?, limit: Int?, offset: Int?): Collection<Commune> =
        dsl.selectFrom(COMMUNE)
            .where(getConditions(codeInsee, libelle))
            .orderBy(COMMUNE.INSEE)
            .limit(limit)
            .offset(offset)
            .fetchInto()

    private fun getConditions(codeInsee: String?, libelle: String?): Condition {
        var condition: Condition = DSL.trueCondition()
        if (codeInsee != null) {
            condition = condition.and(COMMUNE.INSEE.eq(codeInsee))
        }
        if (libelle != null) {
            condition = condition.and(COMMUNE.LIBELLE.likeIgnoreCase("%$libelle%"))
        }
        return condition
    }

    fun getCommuneForSelect(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.INSEE.`as`("code"), COMMUNE.LIBELLE.`as`("libelle"))
            .from(COMMUNE)
            .orderBy(COMMUNE.LIBELLE)
            .fetchInto()

    /**
     * Retourne les communes qui sont à moins de PEI_TOLERANCE_COMMUNE_METRES mètres de la géométrie passée en paramètre
     */
    fun getCommunesPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int): List<GlobalData.IdCodeLibelleData> =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.INSEE.`as`("code"), COMMUNE.LIBELLE.`as`("libelle"))
            .from(COMMUNE)
            .ST_DWithin(COMMUNE.GEOMETRIE, srid, coordonneeX.toDouble(), coordonneeY.toDouble(), toleranceCommuneMetres)
            .orderBy(COMMUNE.LIBELLE)
            .fetchInto()

    fun getById(id: UUID): Commune = dsl.selectFrom(COMMUNE).where(COMMUNE.ID.eq(id)).fetchSingleInto()
}
