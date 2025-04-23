package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class DroitsRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getDroitsFromUser(userId: UUID): Set<Droit> {
        return dsl.select(PROFIL_DROIT.DROITS)
            .from(UTILISATEUR)
            .innerJoin(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID))
            .innerJoin(ORGANISME).on(UTILISATEUR.ORGANISME_ID.eq(ORGANISME.ID))
            .innerJoin(PROFIL_ORGANISME).on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
            .innerJoin(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .and(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID))
            .innerJoin(PROFIL_DROIT).on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
            .where(UTILISATEUR.ID.eq(userId))
            .fetch().map { record ->
                record.component1()!!
            }.first()
            .filterNotNull()
            .toSet()
    }

    fun getProfilDroitFromUser(userId: UUID): ProfilDroit? {
        return dsl.select(*PROFIL_DROIT.fields())
            .from(UTILISATEUR)
            .innerJoin(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID))
            .innerJoin(ORGANISME).on(UTILISATEUR.ORGANISME_ID.eq(ORGANISME.ID))
            .innerJoin(PROFIL_ORGANISME).on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
            .innerJoin(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .and(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID))
            .innerJoin(PROFIL_DROIT).on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
            .where(UTILISATEUR.ID.eq(userId))
            .fetchOneInto()
    }
}
