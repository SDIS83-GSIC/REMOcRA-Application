package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrierParametre
import remocra.db.jooq.remocra.tables.references.L_MODELE_COURRIER_PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER_PARAMETRE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class ModeleCourrierRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getByCode(code: String): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.eq(code))
            .fetchSingleInto()

    fun getById(modeleCourrierId: UUID): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.ID.eq(modeleCourrierId))
            .fetchSingleInto()

    fun getAll(utilisateurId: UUID): Collection<ModeleCourrier> =
        dsl.select(*MODELE_COURRIER.fields())
            .from(MODELE_COURRIER)
            .join(L_MODELE_COURRIER_PROFIL_DROIT)
            .on(MODELE_COURRIER.ID.eq(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID))
            .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(L_MODELE_COURRIER_PROFIL_DROIT.PROFIL_DROIT_ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .where(UTILISATEUR.ID.eq(utilisateurId))
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
