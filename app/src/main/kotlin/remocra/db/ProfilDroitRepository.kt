package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class ProfilDroitRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
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

    fun getProfilUtilisateurByUtilisateurId(utilisateurId: UUID): UUID? =
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
            .fetchOneInto()

    data class ProfilDroitWithProfils(
        val id: UUID,
        val libelle: String,
        val profilUtilisateurId: UUID,
        val profilOrganismeId: UUID,
    )

    fun getAll(params: Params<Filter, Sort>): Collection<ProfilDroit> =
        dsl.select(*PROFIL_DROIT.fields())
            .from(PROFIL_DROIT)
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(PROFIL_DROIT.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getAllActive(): Collection<ProfilDroit> =
        dsl.selectFrom(PROFIL_DROIT).where(PROFIL_DROIT.ACTIF).fetchInto()

    fun getAllForAdmin(): Collection<ProfilDroit> =
        dsl.selectFrom(PROFIL_DROIT).fetchInto()

    fun getCountAll(params: Params<Filter, Sort>): Int = dsl.fetchCount(PROFIL_DROIT, params.filterBy?.toCondition())

    fun getById(profilDroitId: UUID): ProfilDroit =
        dsl.select(*PROFIL_DROIT.fields())
            .from(PROFIL_DROIT)
            .where(PROFIL_DROIT.ID.eq(profilDroitId))
            .fetchSingleInto()

    data class Filter(
        val profilDroitLibelle: String?,
        val profilDroitCode: String?,
        val profilDroitActif: Boolean?,
    ) {

        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    profilDroitLibelle?.let { DSL.and(PROFIL_DROIT.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    profilDroitCode?.let { DSL.and(PROFIL_DROIT.CODE.containsIgnoreCaseUnaccent(it)) },
                    profilDroitActif?.let { DSL.and(PROFIL_DROIT.ACTIF.eq(it)) },
                ),
            )
    }

    data class Sort(
        val profilDroitLibelle: Int?,
        val profilDroitCode: Int?,
        val profilDroitActif: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            PROFIL_DROIT.LIBELLE.getSortField(profilDroitLibelle),
            PROFIL_DROIT.CODE.getSortField(profilDroitCode),
            PROFIL_DROIT.ACTIF.getSortField(profilDroitActif),
        )
    }

    fun insert(profilDroit: ProfilDroit): Int =
        dsl.insertInto(PROFIL_DROIT)
            .set(dsl.newRecord(PROFIL_DROIT, profilDroit))
            .execute()

    fun update(profilDroit: ProfilDroit): Int =
        dsl.update(PROFIL_DROIT)
            .set(PROFIL_DROIT.LIBELLE, profilDroit.profilDroitLibelle)
            .set(PROFIL_DROIT.CODE, profilDroit.profilDroitCode)
            .set(PROFIL_DROIT.ACTIF, profilDroit.profilDroitActif)
            .where(PROFIL_DROIT.ID.eq(profilDroit.profilDroitId))
            .execute()

    fun updateDroits(profilDroit: ProfilDroit): Int =
        dsl.update(PROFIL_DROIT)
            .set(PROFIL_DROIT.DROITS, profilDroit.profilDroitDroits)
            .where(PROFIL_DROIT.ID.eq(profilDroit.profilDroitId))
            .execute()
}
