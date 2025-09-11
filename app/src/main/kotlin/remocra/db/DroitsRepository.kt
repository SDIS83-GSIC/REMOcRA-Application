package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class DroitsRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getDroitsFromUser(userId: UUID): Set<Droit> {
        return dsl.select(GROUPE_FONCTIONNALITES.DROITS)
            .from(UTILISATEUR)
            .innerJoin(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID))
            .innerJoin(ORGANISME).on(UTILISATEUR.ORGANISME_ID.eq(ORGANISME.ID))
            .innerJoin(PROFIL_ORGANISME).on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
            .innerJoin(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .and(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID))
            .innerJoin(GROUPE_FONCTIONNALITES).on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .where(UTILISATEUR.ID.eq(userId))
            .fetch().map { record ->
                record.component1()!!
            }.first()
            .filterNotNull()
            .toSet()
    }

    fun getGroupeFonctionnalitesFromUser(userId: UUID): GroupeFonctionnalites? {
        return dsl.select(*GROUPE_FONCTIONNALITES.fields())
            .from(UTILISATEUR)
            .innerJoin(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID))
            .innerJoin(ORGANISME).on(UTILISATEUR.ORGANISME_ID.eq(ORGANISME.ID))
            .innerJoin(PROFIL_ORGANISME).on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
            .innerJoin(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .and(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID))
            .innerJoin(GROUPE_FONCTIONNALITES).on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .where(UTILISATEUR.ID.eq(userId))
            .fetchOneInto()
    }
}
