package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrierParametre
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER_PARAMETRE
import java.util.UUID

class ModeleCourrierRepository @Inject constructor(private val dsl: DSLContext) {

    fun getByCode(code: String): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.eq(code))
            .fetchSingleInto()

    fun getById(modeleCourrierId: UUID): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.ID.eq(modeleCourrierId))
            .fetchSingleInto()

    fun getAll(): Collection<ModeleCourrier> =
        dsl.selectFrom(MODELE_COURRIER)
            .fetchInto()

    /**
     * Retourne les paramètres groupés par courrier
     */
    fun getParametresByModele(): Map<ModeleCourrier, List<ModeleCourrierParametre>> =
        dsl.select(
            *MODELE_COURRIER.fields(),
            *MODELE_COURRIER_PARAMETRE.fields(),
        )
            .from(MODELE_COURRIER)
            .join(MODELE_COURRIER_PARAMETRE)
            .on(MODELE_COURRIER.ID.eq(MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID))
            .fetchGroups(ModeleCourrier::class.java, ModeleCourrierParametre::class.java)
}
