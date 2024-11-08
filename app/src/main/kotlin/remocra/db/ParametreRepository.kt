package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.references.PARAMETRE

class ParametreRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getMapParametres() = dsl.selectFrom(PARAMETRE).fetchInto<Parametre>().associateBy { it.parametreCode }

    /**
     * Met à jour un paramètre, et retourne TRUE si une et une seule ligne a été mise à jour, false sinon
     */
    fun updateParametre(parametreCode: String, parametreValeur: String?): Boolean {
        return dsl.update(PARAMETRE)
            .set(PARAMETRE.VALEUR, parametreValeur)
            .where(PARAMETRE.CODE.eq(parametreCode)).execute() == 1
    }
}
