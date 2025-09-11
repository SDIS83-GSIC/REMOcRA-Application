package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID
import kotlin.math.absoluteValue

class GroupeFonctionnalitesRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            GROUPE_FONCTIONNALITES.ID.`as`("id"),
            GROUPE_FONCTIONNALITES.CODE.`as`("code"),
            GROUPE_FONCTIONNALITES.LIBELLE.`as`("libelle"),
        )
            .from(GROUPE_FONCTIONNALITES)
            .orderBy(GROUPE_FONCTIONNALITES.LIBELLE)
            .fetchInto()

    fun getGroupeFonctionnalitesWithProfils(): List<GroupeFonctionnalitesWithProfils> =
        dsl.select(
            GROUPE_FONCTIONNALITES.ID.`as`("id"),
            GROUPE_FONCTIONNALITES.LIBELLE.`as`("libelle"),
            L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID,
            L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID,
        )
            .from(GROUPE_FONCTIONNALITES)
            .join(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .fetchInto()

    fun getProfilUtilisateurByUtilisateurId(utilisateurId: UUID): UUID? =
        dsl.select(
            GROUPE_FONCTIONNALITES.ID,
        )
            .from(GROUPE_FONCTIONNALITES)
            .join(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .join(ORGANISME)
            .on(
                ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID)
                    .and(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID.eq(ORGANISME.PROFIL_ORGANISME_ID)),
            )
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .fetchOneInto()

    fun getGroupeFonctionnalitesByUtilisateurId(utilisateurId: UUID): GroupeFonctionnalites? =
        dsl.select(
            *GROUPE_FONCTIONNALITES.fields(),
        )
            .from(GROUPE_FONCTIONNALITES)
            .join(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .join(ORGANISME)
            .on(
                ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID)
                    .and(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID.eq(ORGANISME.PROFIL_ORGANISME_ID)),
            )
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .fetchOneInto()

    data class GroupeFonctionnalitesWithProfils(
        val id: UUID,
        val libelle: String,
        val profilUtilisateurId: UUID,
        val profilOrganismeId: UUID,
    )

    fun getAll(params: Params<Filter, Sort>): Collection<GroupeFonctionnalites> =
        dsl.select(*GROUPE_FONCTIONNALITES.fields())
            .from(GROUPE_FONCTIONNALITES)
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(GROUPE_FONCTIONNALITES.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getAllActive(): Collection<GroupeFonctionnalites> =
        dsl.selectFrom(GROUPE_FONCTIONNALITES).where(GROUPE_FONCTIONNALITES.ACTIF).fetchInto()

    fun getAllForAdmin(): Collection<GroupeFonctionnalites> =
        dsl.selectFrom(GROUPE_FONCTIONNALITES).fetchInto()

    fun getCountAll(params: Params<Filter, Sort>): Int = dsl.fetchCount(GROUPE_FONCTIONNALITES, params.filterBy?.toCondition())

    fun getById(groupeFonctionnalitesId: UUID): GroupeFonctionnalites =
        dsl.select(*GROUPE_FONCTIONNALITES.fields())
            .from(GROUPE_FONCTIONNALITES)
            .where(GROUPE_FONCTIONNALITES.ID.eq(groupeFonctionnalitesId))
            .fetchSingleInto()

    data class Filter(
        val groupeFonctionnalitesLibelle: String?,
        val groupeFonctionnalitesCode: String?,
        val groupeFonctionnalitesActif: Boolean?,
    ) {

        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    groupeFonctionnalitesLibelle?.let { DSL.and(GROUPE_FONCTIONNALITES.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    groupeFonctionnalitesCode?.let { DSL.and(GROUPE_FONCTIONNALITES.CODE.containsIgnoreCaseUnaccent(it)) },
                    groupeFonctionnalitesActif?.let { DSL.and(GROUPE_FONCTIONNALITES.ACTIF.eq(it)) },
                ),
            )
    }

    data class Sort(
        val groupeFonctionnalitesLibelle: Int?,
        val groupeFonctionnalitesCode: Int?,
        val groupeFonctionnalitesActif: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            groupeFonctionnalitesLibelle?.let { "groupeFonctionnalitesLibelle" to it },
            groupeFonctionnalitesCode?.let { "groupeFonctionnalitesCode" to it },
            groupeFonctionnalitesActif?.let { "groupeFonctionnalitesActif" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "groupeFonctionnalitesLibelle" -> GROUPE_FONCTIONNALITES.LIBELLE.getSortField(pair.second)
                "groupeFonctionnalitesCode" -> GROUPE_FONCTIONNALITES.CODE.getSortField(pair.second)
                "groupeFonctionnalitesActif" -> GROUPE_FONCTIONNALITES.ACTIF.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun insert(groupeFonctionnalites: GroupeFonctionnalites): Int =
        dsl.insertInto(GROUPE_FONCTIONNALITES)
            .set(dsl.newRecord(GROUPE_FONCTIONNALITES, groupeFonctionnalites))
            .execute()

    fun update(groupeFonctionnalites: GroupeFonctionnalites): Int =
        dsl.update(GROUPE_FONCTIONNALITES)
            .set(GROUPE_FONCTIONNALITES.LIBELLE, groupeFonctionnalites.groupeFonctionnalitesLibelle)
            .set(GROUPE_FONCTIONNALITES.CODE, groupeFonctionnalites.groupeFonctionnalitesCode)
            .set(GROUPE_FONCTIONNALITES.ACTIF, groupeFonctionnalites.groupeFonctionnalitesActif)
            .where(GROUPE_FONCTIONNALITES.ID.eq(groupeFonctionnalites.groupeFonctionnalitesId))
            .execute()

    fun updateDroits(groupeFonctionnalites: GroupeFonctionnalites): Int =
        dsl.update(GROUPE_FONCTIONNALITES)
            .set(GROUPE_FONCTIONNALITES.DROITS, groupeFonctionnalites.groupeFonctionnalitesDroits)
            .where(GROUPE_FONCTIONNALITES.ID.eq(groupeFonctionnalites.groupeFonctionnalitesId))
            .execute()
}
