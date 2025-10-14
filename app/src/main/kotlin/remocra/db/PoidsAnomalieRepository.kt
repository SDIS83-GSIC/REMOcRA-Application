package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.GlobalConstants
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import java.util.UUID

class PoidsAnomalieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getPoidsAnomalies(anomaliesIds: Collection<UUID>, natureId: UUID, typeVisite: TypeVisite?): Collection<PoidsAnomalie> =
        dsl.select(POIDS_ANOMALIE.fields().toList())
            .from(POIDS_ANOMALIE)
            .join(ANOMALIE)
            .on(ANOMALIE.ID.eq(POIDS_ANOMALIE.ANOMALIE_ID))
            .join(ANOMALIE_CATEGORIE)
            .on(ANOMALIE.ANOMALIE_CATEGORIE_ID.eq(ANOMALIE_CATEGORIE.ID))
            .where(POIDS_ANOMALIE.ANOMALIE_ID.`in`(anomaliesIds))
            .and(POIDS_ANOMALIE.NATURE_ID.eq(natureId))
            .and(
                // Si anomalie système, elles n'ont pas de type de visites
                typeVisite?.let { POIDS_ANOMALIE.TYPE_VISITE.contains(typeVisite) }?.or(
                    ANOMALIE_CATEGORIE.CODE.eq(
                        GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME,
                    ),
                ) ?: DSL.noCondition(),
            )
            .fetchInto()
}
