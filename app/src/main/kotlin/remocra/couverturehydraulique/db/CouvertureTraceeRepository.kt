package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import remocra.db.AbstractRepository
import remocra.db.jooq.couverturehydraulique.tables.references.COUVERTURE_TRACEE
import java.util.UUID

/**
 * Repository pour les données de couverture tracée
 */
class CouvertureTraceeRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Supprime les couvertures tracées par label et étude
     */
    fun deleteByLabelAndEtude(label: String, idEtude: UUID) {
        dsl.deleteFrom(COUVERTURE_TRACEE)
            .where(COUVERTURE_TRACEE.LABEL.eq(label))
            .and(COUVERTURE_TRACEE.ETUDE_ID.eq(idEtude))
            .execute()
    }

    /**
     * Insère une nouvelle couverture tracée
     */
    fun insert(label: String, idEtude: UUID, geometrie: Geometry?) {
        if (geometrie != null && !geometrie.isEmpty) {
            dsl.insertInto(COUVERTURE_TRACEE)
                .set(COUVERTURE_TRACEE.LABEL, label)
                .set(COUVERTURE_TRACEE.ETUDE_ID, idEtude)
                .set(COUVERTURE_TRACEE.GEOMETRIE, geometrie)
                .execute()
        }
    }

    /**
     * Récupère la géométrie d'une couverture tracée
     */
    fun getGeometrieByLabelAndEtude(label: String, idEtude: UUID): Geometry? {
        return dsl.select(COUVERTURE_TRACEE.GEOMETRIE)
            .from(COUVERTURE_TRACEE)
            .where(COUVERTURE_TRACEE.LABEL.eq(label))
            .and(COUVERTURE_TRACEE.ETUDE_ID.eq(idEtude))
            .fetchOne()
            ?.get(COUVERTURE_TRACEE.GEOMETRIE)
    }
}
