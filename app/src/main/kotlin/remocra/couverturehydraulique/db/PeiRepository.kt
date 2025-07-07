package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Point
import remocra.db.AbstractRepository
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.remocra.tables.references.PEI
import java.util.UUID

/**
 * Repository pour les données PEI
 */
class PeiRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    data class PeiCouvertureHydraulique(
        val peiId: UUID,
        val peiGeometrie: Point,
    )

    /**
     * Obtient un PEI par ID
     */
    fun getById(id: UUID): PeiCouvertureHydraulique? {
        return dsl.select(PEI.ID.`as`("peiId"), PEI.GEOMETRIE.`as`("peiGeometrie")).from(PEI)
            .where(PEI.ID.eq(id))
            .fetchOptionalInto(PeiCouvertureHydraulique::class.java)
            .orElseGet {
                dsl.select(PEI_PROJET.ID.`as`("peiId"), PEI_PROJET.GEOMETRIE.`as`("peiGeometrie")).from(PEI_PROJET)
                    .where(PEI_PROJET.ID.eq(id))
                    .fetchOneInto(PeiCouvertureHydraulique::class.java)
            }
    }
}
