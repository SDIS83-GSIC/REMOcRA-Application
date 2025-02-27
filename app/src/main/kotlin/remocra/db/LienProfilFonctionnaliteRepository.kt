package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.LienProfilFonctionnaliteData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeDroit
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import java.util.UUID

class LienProfilFonctionnaliteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(params: Params<Filter, Sort>): Collection<LienProfilFonctionnaliteData> =
        dsl.select(
            *L_PROFIL_UTILISATEUR_ORGANISME_DROIT.fields(),
            PROFIL_ORGANISME.LIBELLE,
            PROFIL_UTILISATEUR.LIBELLE,
            PROFIL_DROIT.LIBELLE,
        )
            .from(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .join(PROFIL_ORGANISME).on(PROFIL_ORGANISME.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID))
            .join(PROFIL_UTILISATEUR).on(PROFIL_UTILISATEUR.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .join(PROFIL_DROIT).on(PROFIL_DROIT.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID))
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(PROFIL_ORGANISME.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getCountAll(params: Params<Filter, Sort>): Int =
        dsl.selectCount().from(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .join(PROFIL_ORGANISME).on(PROFIL_ORGANISME.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID))
            .join(PROFIL_UTILISATEUR).on(PROFIL_UTILISATEUR.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .join(PROFIL_DROIT).on(PROFIL_DROIT.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID))
            .where(params.filterBy?.toCondition())
            .fetchSingleInto()

    fun get(profilOrganismeId: UUID, profilUtilisateurId: UUID): LProfilUtilisateurOrganismeDroit? =
        dsl.selectFrom(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .where(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID.eq(profilOrganismeId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID.eq(profilUtilisateurId))
            .fetchOneInto()

    fun insert(element: LProfilUtilisateurOrganismeDroit): Int =
        dsl.insertInto(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .set(dsl.newRecord(L_PROFIL_UTILISATEUR_ORGANISME_DROIT, element))
            .execute()

    fun update(lProfilUtilisateurOrganismeDroit: LProfilUtilisateurOrganismeDroit, profilOrganismeId: UUID, profilUtilisateurId: UUID): Int =
        dsl.update(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .set(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID, lProfilUtilisateurOrganismeDroit.profilOrganismeId)
            .set(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID, lProfilUtilisateurOrganismeDroit.profilUtilisateurId)
            .set(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID, lProfilUtilisateurOrganismeDroit.profilDroitId)
            .where(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID.eq(profilOrganismeId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID.eq(profilUtilisateurId))
            .execute()

    fun delete(element: LProfilUtilisateurOrganismeDroit): Int =
        dsl.deleteFrom(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .where(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID.eq(element.profilOrganismeId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID.eq(element.profilUtilisateurId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(element.profilDroitId))
            .execute()

    data class Filter(
        val organisme: String?,
        val utilisateur: String?,
        val fonctionnalite: String?,
    ) {

        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    organisme?.let { DSL.and(PROFIL_ORGANISME.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    utilisateur?.let { DSL.and(PROFIL_UTILISATEUR.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    fonctionnalite?.let { DSL.and(PROFIL_DROIT.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                ),
            )
    }

    data class Sort(
        val organisme: Int?,
        val utilistaeur: Int?,
        val fonctionnalite: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            PROFIL_ORGANISME.LIBELLE.getSortField(organisme),
            PROFIL_UTILISATEUR.LIBELLE.getSortField(utilistaeur),
            PROFIL_DROIT.LIBELLE.getSortField(fonctionnalite),
        )
    }
}
