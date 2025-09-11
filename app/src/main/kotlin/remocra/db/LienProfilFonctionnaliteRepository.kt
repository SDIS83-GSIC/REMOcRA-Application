package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.LienProfilFonctionnaliteData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeGroupeFonctionnalites
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import java.util.UUID
import kotlin.math.absoluteValue

class LienProfilFonctionnaliteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(params: Params<Filter, Sort>): Collection<LienProfilFonctionnaliteData> =
        dsl.select(
            *L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.fields(),
            PROFIL_ORGANISME.LIBELLE,
            PROFIL_UTILISATEUR.LIBELLE,
            GROUPE_FONCTIONNALITES.LIBELLE,
        )
            .from(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .join(PROFIL_ORGANISME).on(PROFIL_ORGANISME.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID))
            .join(PROFIL_UTILISATEUR).on(PROFIL_UTILISATEUR.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .join(GROUPE_FONCTIONNALITES).on(GROUPE_FONCTIONNALITES.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID))
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(PROFIL_ORGANISME.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getCountAll(params: Params<Filter, Sort>): Int =
        dsl.selectCount().from(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .join(PROFIL_ORGANISME).on(PROFIL_ORGANISME.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID))
            .join(PROFIL_UTILISATEUR).on(PROFIL_UTILISATEUR.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .join(GROUPE_FONCTIONNALITES).on(GROUPE_FONCTIONNALITES.ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID))
            .where(params.filterBy?.toCondition())
            .fetchSingleInto()

    fun get(profilOrganismeId: UUID, profilUtilisateurId: UUID): LProfilUtilisateurOrganismeGroupeFonctionnalites? =
        dsl.selectFrom(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .where(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID.eq(profilOrganismeId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID.eq(profilUtilisateurId))
            .fetchOneInto()

    fun insert(element: LProfilUtilisateurOrganismeGroupeFonctionnalites): Int =
        dsl.insertInto(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .set(dsl.newRecord(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES, element))
            .execute()

    fun update(lProfilUtilisateurOrganismeDroit: LProfilUtilisateurOrganismeGroupeFonctionnalites, profilOrganismeId: UUID, profilUtilisateurId: UUID): Int =
        dsl.update(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .set(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID, lProfilUtilisateurOrganismeDroit.profilOrganismeId)
            .set(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID, lProfilUtilisateurOrganismeDroit.profilUtilisateurId)
            .set(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID, lProfilUtilisateurOrganismeDroit.groupeFonctionnalitesId)
            .where(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID.eq(profilOrganismeId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID.eq(profilUtilisateurId))
            .execute()

    fun delete(element: LProfilUtilisateurOrganismeGroupeFonctionnalites): Int =
        dsl.deleteFrom(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .where(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID.eq(element.profilOrganismeId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID.eq(element.profilUtilisateurId))
            .and(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(element.groupeFonctionnalitesId))
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
                    fonctionnalite?.let { DSL.and(GROUPE_FONCTIONNALITES.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                ),
            )
    }

    data class Sort(
        val organisme: Int?,
        val utilisateur: Int?,
        val fonctionnalite: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            organisme?.let { "organisme" to it },
            utilisateur?.let { "utilisateur" to it },
            fonctionnalite?.let { "fonctionnalite" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "organisme" -> PROFIL_ORGANISME.LIBELLE.getSortField(pair.second)
                "utilisateur" -> PROFIL_UTILISATEUR.LIBELLE.getSortField(pair.second)
                "fonctionnalite" -> GROUPE_FONCTIONNALITES.LIBELLE.getSortField(pair.second)
                else -> null
            }
        }
    }
}
