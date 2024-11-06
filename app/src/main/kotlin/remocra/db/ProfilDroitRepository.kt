package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class ProfilDroitRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            PROFIL_DROIT.ID.`as`("id"),
            PROFIL_DROIT.CODE.`as`("code"),
            PROFIL_DROIT.LIBELLE.`as`("libelle"),
        )
            .from(PROFIL_DROIT)
            .orderBy(PROFIL_DROIT.LIBELLE)
            .fetchInto()

    fun getProfilsDroitWithProfils(): List<ProfilDroitWithProfils> =
        dsl.select(
            PROFIL_DROIT.ID.`as`("id"),
            PROFIL_DROIT.LIBELLE.`as`("libelle"),
            L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID,
            L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID,
        )
            .from(PROFIL_DROIT)
            .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
            .fetchInto()

    fun getProfilUtilisateurByUtilisateurId(utilisateurId: UUID): UUID =
        dsl.select(
            PROFIL_DROIT.ID,
        )
            .from(PROFIL_DROIT)
            .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .join(ORGANISME)
            .on(
                ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID)
                    .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID.eq(ORGANISME.PROFIL_ORGANISME_ID)),
            )
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .fetchSingleInto()

    data class ProfilDroitWithProfils(
        val id: UUID,
        val libelle: String,
        val profilUtilisateurId: UUID,
        val profilOrganismeId: UUID,
    )
}
