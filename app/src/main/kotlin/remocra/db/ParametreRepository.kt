package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.references.PARAMETRE

class ParametreRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getMapParametres() = dsl.selectFrom(PARAMETRE).fetchInto<Parametre>().associateBy { it.parametreCode }

    /**
     * Met à jour un paramètre existant.
     *
     * @param parametreCode Le code du paramètre à mettre à jour
     * @param parametreValeur La nouvelle valeur du paramètre (peut être null)
     */
    fun updateParametre(parametreCode: String, parametreValeur: String?) {
        dsl.update(PARAMETRE)
            .set(PARAMETRE.VALEUR, parametreValeur)
            .where(PARAMETRE.CODE.eq(parametreCode))
            .execute()
    }

    fun getAll(): List<Parametre> = dsl.selectFrom(PARAMETRE).fetchInto()
}
