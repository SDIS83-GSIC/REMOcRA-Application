package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.SortOrder
import org.jooq.impl.DSL
import remocra.db.AbstractRepository
import remocra.db.fetchOneInto
import remocra.db.fetchSingleInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.VoieLaterale
import remocra.db.jooq.couverturehydraulique.tables.references.VOIE_LATERALE
import java.util.UUID

/**
 * Repository pour les données de voie latérale
 */
class VoieLateraleRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Vide la table des voies latérales
     */
    fun emptyTable() {
        dsl.truncate(VOIE_LATERALE).execute()
    }

    /**
     * Insère une nouvelle voie latérale
     */
    fun insert(
        voieVoisine: UUID,
        angle: Double,
        traversable: Boolean,
        accessible: Boolean = true,
    ) {
        dsl.insertInto(VOIE_LATERALE)
            .set(VOIE_LATERALE.ID, UUID.randomUUID())
            .set(VOIE_LATERALE.VOIE_VOISINE, voieVoisine)
            .set(VOIE_LATERALE.ANGLE, angle)
            .set(VOIE_LATERALE.GAUCHE, false)
            .set(VOIE_LATERALE.DROITE, false)
            .set(VOIE_LATERALE.TRAVERSABLE, traversable)
            .set(VOIE_LATERALE.ACCESSIBLE, accessible)
            .execute()
    }

    /**
     * Marque la voie de gauche (angle minimum)
     */
    fun tagVoieGauche() {
        val angleMin = dsl.select(DSL.min(VOIE_LATERALE.ANGLE))
            .from(VOIE_LATERALE)
            .fetchOne(0, Double::class.java)

        if (angleMin != null) {
            dsl.update(VOIE_LATERALE)
                .set(VOIE_LATERALE.GAUCHE, true)
                .where(VOIE_LATERALE.ANGLE.eq(angleMin))
                .execute()
        }
    }

    /**
     * Marque la voie de droite (angle maximum)
     */
    fun tagVoieDroite() {
        val angleMax = dsl.select(DSL.max(VOIE_LATERALE.ANGLE))
            .from(VOIE_LATERALE)
            .fetchOne(0, Double::class.java)

        if (angleMax != null) {
            dsl.update(VOIE_LATERALE)
                .set(VOIE_LATERALE.DROITE, true)
                .where(VOIE_LATERALE.ANGLE.eq(angleMax))
                .execute()
        }
    }

    /**
     * Marque les voies non accessibles entre deux angles
     */
    fun tagVoiesNonAccessibles(angleMin: Double, angleMax: Double) {
        dsl.update(VOIE_LATERALE)
            .set(VOIE_LATERALE.ACCESSIBLE, false)
            .where(VOIE_LATERALE.ANGLE.gt(angleMin))
            .and(VOIE_LATERALE.ANGLE.lt(angleMax))
            .execute()
    }

    /**
     * Obtient la voie de gauche
     */
    fun getVoieGauche(): VoieLaterale? {
        return dsl.selectFrom(VOIE_LATERALE)
            .where(VOIE_LATERALE.GAUCHE.isTrue)
            .limit(1)
            .fetchOneInto()
    }

    /**
     * Obtient la voie de droite
     */
    fun getVoieDroite(): VoieLaterale? {
        return dsl.selectFrom(VOIE_LATERALE)
            .where(VOIE_LATERALE.DROITE.isTrue)
            .limit(1)
            .fetchOneInto()
    }

    /**
     * Obtient une voie latérale par ID de voie voisine
     */
    fun getByVoieVoisine(voieId: UUID): VoieLaterale? {
        return dsl.selectFrom(VOIE_LATERALE)
            .where(VOIE_LATERALE.VOIE_VOISINE.eq(voieId))
            .limit(1)
            .fetchOneInto()
    }

    /**
     * Obtient la première voie non traversable par ordre d'angle
     */
    fun getFirstVoieNonTraversable(ordreAngle: String): VoieLaterale? {
        val orderField = if (ordreAngle == SortOrder.DESC.toString()) {
            VOIE_LATERALE.ANGLE.desc()
        } else {
            VOIE_LATERALE.ANGLE.asc()
        }

        return dsl.selectFrom(VOIE_LATERALE)
            .where(VOIE_LATERALE.TRAVERSABLE.eq(false))
            .orderBy(orderField)
            .limit(1)
            .fetchOneInto()
    }

    /**
     * Compte le nombre total de voies latérales
     */
    fun countVoies(): Int {
        return dsl.selectCount()
            .from(VOIE_LATERALE)
            .fetchSingleInto()
    }

    /**
     * Vérifie si une voie est dans la liste des voies latérales
     */
    fun isVoieLaterale(voieId: UUID): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(VOIE_LATERALE)
                .where(VOIE_LATERALE.VOIE_VOISINE.eq(voieId)),
        )
    }
}
