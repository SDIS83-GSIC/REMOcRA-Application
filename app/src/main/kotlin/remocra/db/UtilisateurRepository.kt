package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.GlobalConstants
import remocra.data.GlobalData
import remocra.data.Params
import remocra.data.UtilisateurData
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.tasks.Destinataire
import remocra.utils.ST_Within
import java.util.UUID

class UtilisateurRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getUtilisateurById(idUtilisateur: UUID): Utilisateur? =
        dsl.selectFrom(UTILISATEUR)
            .where(UTILISATEUR.ID.eq(idUtilisateur))
            .fetchOneInto()

    fun setActif(actif: Boolean, idUtilisateur: UUID) {
        dsl.update(UTILISATEUR)
            .set(UTILISATEUR.ACTIF, actif)
            .where(UTILISATEUR.ID.eq(idUtilisateur))
            .and(UTILISATEUR.USERNAME.ne(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .execute()
    }

    fun updateUtilisateur(idUtilisateur: UUID, nom: String, prenom: String, email: String, actif: Boolean) {
        dsl.update(UTILISATEUR)
            .set(UTILISATEUR.ACTIF, actif)
            .set(UTILISATEUR.NOM, nom)
            .set(UTILISATEUR.PRENOM, prenom)
            .set(UTILISATEUR.EMAIL, email)
            .where(UTILISATEUR.ID.eq(idUtilisateur))
            .and(UTILISATEUR.USERNAME.ne(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .execute()
    }

    /**
     * Dans le cadre de la synchro des utilisateurs, désactive tous les utilisateurs sauf SYSTEME qui est hors scope
     */
    fun desactiveAllUsers() {
        dsl.update(UTILISATEUR)
            .set(UTILISATEUR.ACTIF, false)
            .where(UTILISATEUR.USERNAME.ne(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .execute()
    }

    /**
     * Dans le cadre de la synchro des utilisateurs, supprime tous les utilisateurs restés inactifs sauf SYSTEME qui est hors scope
     */
    fun deleteUtilisateurInactif(): Int =
        dsl.deleteFrom(UTILISATEUR)
            .where(UTILISATEUR.ACTIF.isFalse)
            .and(UTILISATEUR.USERNAME.ne(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .execute()

    fun insertUtilisateur(
        id: UUID,
        nom: String,
        prenom: String,
        email: String,
        username: String,
        actif: Boolean = true,
    ): Utilisateur =
        dsl.insertInto(UTILISATEUR)
            .set(UTILISATEUR.ID, id)
            .set(UTILISATEUR.ACTIF, actif)
            .set(UTILISATEUR.NOM, nom)
            .set(UTILISATEUR.PRENOM, prenom)
            .set(UTILISATEUR.EMAIL, email)
            .set(UTILISATEUR.USERNAME, username)
            .onConflict(UTILISATEUR.USERNAME)
            .doNothing()
            .returning()
            .fetchSingleInto()

    fun syncUtilisateur(
        id: UUID,
        nom: String,
        prenom: String,
        email: String,
        username: String,
        actif: Boolean,
    ): Utilisateur =
        dsl.insertInto(UTILISATEUR)
            .set(UTILISATEUR.ID, id)
            .set(UTILISATEUR.ACTIF, actif)
            .set(UTILISATEUR.NOM, nom)
            .set(UTILISATEUR.PRENOM, prenom)
            .set(UTILISATEUR.EMAIL, email)
            .set(UTILISATEUR.USERNAME, username)
            .onConflict(UTILISATEUR.ID)
            .doUpdate()
            .set(UTILISATEUR.ACTIF, actif)
            .set(UTILISATEUR.NOM, nom)
            .set(UTILISATEUR.PRENOM, prenom)
            .set(UTILISATEUR.EMAIL, email)
            // USERNAME exclu explicitement
            .returning()
            .fetchSingleInto()

    fun getAll(): Collection<Utilisateur> =
        dsl.selectFrom(UTILISATEUR).fetchInto()

    fun getZoneByOrganismeId(organismeId: UUID): ZoneIntegration? {
        return dsl.select(*ZONE_INTEGRATION.fields())
            .from(ZONE_INTEGRATION)
            .join(ORGANISME).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .where(ORGANISME.ID.eq(organismeId))
            .fetchOneInto()
    }

    fun getDestinataireUtilisateurOrganisme(
        listePeiId: List<UUID>,
        typeOrganisme: List<UUID>,
    ): Map<Destinataire, List<UUID?>> =
        dsl.select(
            PEI.ID,
            UTILISATEUR.ID,
            UTILISATEUR.NOM,
            UTILISATEUR.PRENOM,
            UTILISATEUR.EMAIL,
        )
            .from(UTILISATEUR)
            .join(ORGANISME).on(UTILISATEUR.ORGANISME_ID.eq(ORGANISME.ID))
            .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .join(ZONE_INTEGRATION).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .join(PEI).on(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .where(PEI.ID.`in`(listePeiId))
            .and(TYPE_ORGANISME.ID.`in`(typeOrganisme))
            .and(UTILISATEUR.CAN_BE_NOTIFIED)
            .and(UTILISATEUR.EMAIL.isNotNull)
            .fetchGroups(
                { record ->
                    Destinataire(
                        destinataireId = record.get(UTILISATEUR.ID),
                        destinataireCivilite = null,
                        destinataireFonction = null,
                        destinataireNom = record.get(UTILISATEUR.NOM),
                        destinatairePrenom = record.get(UTILISATEUR.PRENOM),
                        destinataireEmail = record.get(UTILISATEUR.EMAIL)!!,
                    )
                },
                { record ->
                    record.get(PEI.ID)
                },
            )

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<UtilisateurComplet> =
        dsl.select(
            *UTILISATEUR.fields(),
            ORGANISME.LIBELLE,
            PROFIL_UTILISATEUR.LIBELLE,
            PROFIL_DROIT.LIBELLE,
        )
            .from(UTILISATEUR)
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID))
            .leftJoin(PROFIL_UTILISATEUR)
            .on(PROFIL_UTILISATEUR.ID.eq(UTILISATEUR.PROFIL_UTILISATEUR_ID))
            .leftJoin(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(
                L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID)
                    .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID.eq(ORGANISME.PROFIL_ORGANISME_ID)),
            )
            .leftJoin(PROFIL_DROIT)
            .on(PROFIL_DROIT.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .and(UTILISATEUR.USERNAME.ne(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(UTILISATEUR.USERNAME))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    data class UtilisateurComplet(
        val utilisateurId: UUID,
        val utilisateurActif: Boolean,
        val utilisateurEmail: String,
        val utilisateurNom: String,
        val utilisateurPrenom: String,
        val utilisateurUsername: String,
        val utilisateurTelephone: String?,
        val utilisateurCanBeNotified: Boolean?,
        val utilisateurProfilUtilisateurId: UUID?,
        val utilisateurOrganismeId: UUID?,
        val organismeLibelle: String?,
        val profilUtilisateurLibelle: String?,
        val profilDroitLibelle: String?,
    )

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.select(UTILISATEUR.ID)
            .from(UTILISATEUR)
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID))
            .leftJoin(PROFIL_UTILISATEUR)
            .on(PROFIL_UTILISATEUR.ID.eq(UTILISATEUR.PROFIL_UTILISATEUR_ID))
            .leftJoin(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(
                L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID)
                    .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID.eq(ORGANISME.PROFIL_ORGANISME_ID)),
            )
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .and(UTILISATEUR.USERNAME.ne(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .count()

    data class Filter(
        val utilisateurActif: Boolean?,
        val utilisateurEmail: String?,
        val utilisateurNom: String?,
        val utilisateurPrenom: String?,
        val utilisateurUsername: String?,
        val utilisateurTelephone: String?,
        val utilisateurCanBeNotified: Boolean?,
        val utilisateurProfilUtilisateurId: UUID?,
        val utilisateurOrganismeId: UUID?,
        val profilDroitId: UUID?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    utilisateurActif?.let { DSL.and(UTILISATEUR.ACTIF.eq(it)) },
                    utilisateurEmail?.let { DSL.and(UTILISATEUR.EMAIL.containsIgnoreCase(it)) },
                    utilisateurNom?.let { DSL.and(UTILISATEUR.NOM.containsIgnoreCase(it)) },
                    utilisateurPrenom?.let { DSL.and(UTILISATEUR.PRENOM.containsIgnoreCase(it)) },
                    utilisateurUsername?.let { DSL.and(UTILISATEUR.USERNAME.containsIgnoreCase(it)) },
                    utilisateurTelephone?.let { DSL.and(UTILISATEUR.TELEPHONE.containsIgnoreCase(it)) },
                    utilisateurCanBeNotified?.let { DSL.and(UTILISATEUR.CAN_BE_NOTIFIED.eq(it)) },
                    utilisateurProfilUtilisateurId?.let { DSL.and(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(it)) },
                    utilisateurOrganismeId?.let { DSL.and(UTILISATEUR.ORGANISME_ID.eq(it)) },
                    profilDroitId?.let { DSL.and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(it)) },
                ),
            )
    }

    data class Sort(
        val utilisateurActif: Int?,
        val utilisateurEmail: Int?,
        val utilisateurNom: Int?,
        val utilisateurPrenom: Int?,
        val utilisateurUsername: Int?,
        val utilisateurTelephone: Int?,
        val utilisateurCanBeNotified: Int?,
        val profilUtilisateurLibelle: Int?,
        val organismeLibelle: Int?,
        val profilDroitLibelle: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            UTILISATEUR.ACTIF.getSortField(utilisateurActif),
            UTILISATEUR.EMAIL.getSortField(utilisateurEmail),
            UTILISATEUR.NOM.getSortField(utilisateurNom),
            UTILISATEUR.PRENOM.getSortField(utilisateurPrenom),
            UTILISATEUR.USERNAME.getSortField(utilisateurUsername),
            UTILISATEUR.TELEPHONE.getSortField(utilisateurTelephone),
            UTILISATEUR.CAN_BE_NOTIFIED.getSortField(utilisateurCanBeNotified),
            PROFIL_UTILISATEUR.LIBELLE.getSortField(profilUtilisateurLibelle),
            ORGANISME.LIBELLE.getSortField(organismeLibelle),
            PROFIL_DROIT.LIBELLE.getSortField(profilDroitLibelle),
        )
    }

    fun checkExistsUsername(username: String) =
        dsl.fetchExists(
            dsl.select(UTILISATEUR.ID)
                .from(UTILISATEUR)
                .where(UTILISATEUR.USERNAME.eq(username)),
        )
    fun checkExistsUsername(username: String, id: UUID) =
        dsl.fetchExists(
            dsl.select(UTILISATEUR.ID)
                .from(UTILISATEUR)
                .where(UTILISATEUR.USERNAME.eq(username))
                .and(
                    UTILISATEUR.ID.ne(id),
                ),
        )
    fun checkExistsEmail(email: String) =
        dsl.fetchExists(
            dsl.select(UTILISATEUR.ID)
                .from(UTILISATEUR)
                .where(UTILISATEUR.EMAIL.eq(email)),
        )

    fun insertUtilisateur(utilisateur: Utilisateur) =
        dsl.insertInto(UTILISATEUR)
            .set(dsl.newRecord(UTILISATEUR, utilisateur))
            .execute()

    fun checkExistsInTournee(utilisateurId: UUID) =
        dsl.fetchExists(
            dsl.select(TOURNEE.RESERVATION_UTILISATEUR_ID)
                .from(TOURNEE)
                .where(TOURNEE.RESERVATION_UTILISATEUR_ID.eq(utilisateurId)),
        )

    fun deleteUtilisateur(utilisateurId: UUID): Int =
        dsl.deleteFrom(UTILISATEUR)
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .execute()

    fun updateUtilisateur(utilisateur: Utilisateur) =
        dsl.update(UTILISATEUR)
            .set(dsl.newRecord(UTILISATEUR, utilisateur))
            .where(UTILISATEUR.ID.eq(utilisateur.utilisateurId))
            .execute()

    fun getById(utilisateurId: UUID): UtilisateurData =
        dsl.select(*UTILISATEUR.fields())
            .from(UTILISATEUR)
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .fetchSingleInto()

    fun getUtilisateurSysteme(): Utilisateur {
        return dsl.selectFrom(UTILISATEUR)
            .where(UTILISATEUR.USERNAME.eq(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .fetchSingleInto()
    }

    fun getIdMailForFilter(): Collection<IdMailData> =
        dsl.select(UTILISATEUR.ID, UTILISATEUR.EMAIL)
            .from(UTILISATEUR)
            .where(UTILISATEUR.USERNAME.notEqualIgnoreCase(GlobalConstants.UTILISATEUR_SYSTEME_USERNAME))
            .fetchInto()

    data class IdMailData(
        val utilisateurId: UUID,
        val utilisateurEmail: String,
    )

    private fun getUtilisteurListByTypeOrganisme(vararg typeOrganismeList: String): List<GlobalData.IdCodeLibelleData> =
        dsl
            .select(UTILISATEUR.ID.`as`("id"))
            .select(DSL.concat(UTILISATEUR.NOM, DSL.value(" "), UTILISATEUR.PRENOM).`as`("code"))
            .select(DSL.concat(UTILISATEUR.NOM, DSL.value(" "), UTILISATEUR.PRENOM).`as`("libelle"))
            .from(UTILISATEUR)
            .join(PROFIL_UTILISATEUR).on(PROFIL_UTILISATEUR.ID.eq(UTILISATEUR.PROFIL_UTILISATEUR_ID))
            .join(TYPE_ORGANISME).on(TYPE_ORGANISME.ID.eq(PROFIL_UTILISATEUR.TYPE_ORGANISME_ID).and(TYPE_ORGANISME.CODE.`in`(*typeOrganismeList)))
            .fetchInto()

    //  Direction départementale des Territoires et de la Mer + Office National des Forêts
    fun getUtilisateurDdtmonf() = getUtilisteurListByTypeOrganisme("DDTM", "ONF")

    // SDIS, CIS et affiliés
    fun getUtilisateurSdis() = getUtilisteurListByTypeOrganisme("SDIS", "CIS", "CIS-ETAPE-1", "CIS-ETAPE-2")

    // Gendarmerie
    fun getUtilisateurGendarmerie() = getUtilisteurListByTypeOrganisme("GENDARMERIE")

    // Police
    fun getUtilisateurPolice() = getUtilisteurListByTypeOrganisme("POLICE")

    fun getForList(): Collection<GlobalData.IdCodeLibelleData> = dsl.select(
        UTILISATEUR.ID.`as`("id"),
        UTILISATEUR.USERNAME.`as`("code"),
        UTILISATEUR.USERNAME.`as`("libelle"),
    )
        .from(UTILISATEUR)
        .where(UTILISATEUR.ACTIF.isTrue)
        .fetchInto()
}
