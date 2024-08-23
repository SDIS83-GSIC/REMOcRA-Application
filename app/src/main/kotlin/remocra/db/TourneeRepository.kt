package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SortField
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.concat
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.time.ZonedDateTime
import java.util.UUID

class TourneeRepository
@Inject constructor(
    private val dsl: DSLContext,
) {
    fun getAllTourneeComplete(params: Params<Filter, Sort>): List<TourneeComplete> {
        val peiCounterCteName = name("PEI_COUNTER_CTE")
        val peiCounterCte = peiCounterCteName.fields("TOURNEE_ID", "TOURNEE_NB_PEI").`as`(
            select(
                L_TOURNEE_PEI.TOURNEE_ID,
                count(L_TOURNEE_PEI.PEI_ID).`as`("TOURNEE_NB_PEI"),
            )
                .from(L_TOURNEE_PEI)
                .groupBy(L_TOURNEE_PEI.TOURNEE_ID),
        )

        return dsl.with(peiCounterCte)
            .select(
                TOURNEE.ID,
                TOURNEE.LIBELLE,
                TOURNEE.ORGANISME_ID,
                ORGANISME.LIBELLE,
                TOURNEE.ETAT,
                TOURNEE.RESERVATION_UTILISATEUR_ID,
                concat(
                    UTILISATEUR.PRENOM,
                    DSL.`val`(" "),
                    UTILISATEUR.NOM,
                    DSL.`val`(" ("),
                    UTILISATEUR.USERNAME,
                    DSL.`val`(")"),
                ).`as`("tourneeUtilisateurReservationLibelle"),
                TOURNEE.DATE_SYNCHRONISATION,
                TOURNEE.ACTIF,
                peiCounterCte.field("TOURNEE_NB_PEI"),
            )
            .from(TOURNEE)
            .join(ORGANISME).on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .leftJoin(UTILISATEUR).on(TOURNEE.RESERVATION_UTILISATEUR_ID.eq(UTILISATEUR.ID))
            .leftJoin(table(peiCounterCteName))
            .on(TOURNEE.ID.eq(field(name("PEI_COUNTER_CTE", "TOURNEE_ID"), SQLDataType.UUID)))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(params.sortBy?.toCondition() ?: listOf(TOURNEE.LIBELLE.asc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()
    }

    fun getTourneeInfoById(tourneeId: UUID): Tournee =
        dsl.select(TOURNEE.fields().asList())
            .from(TOURNEE)
            .where(TOURNEE.ID.eq(tourneeId))
            .fetchSingleInto()

    data class TourneeComplete(
        val tourneeId: UUID,
        val tourneeLibelle: String,
        val tourneeOrganismeId: UUID,
        val organismeLibelle: String,
        val tourneeEtat: Int?,
        val tourneeReservationUtilisateurId: UUID?,
        val tourneeUtilisateurReservationLibelle: String?,
        val tourneeDateSynchronisation: ZonedDateTime?,
        val tourneeActif: Boolean,
        val tourneeNbPei: Int,
    )

    fun countAllTournee(params: Params<Filter, Sort>): Int =
        getAllTourneeComplete(Params(filterBy = params.filterBy, sortBy = null, limit = null, offset = null)).size

    data class Filter(
        val tourneeLibelle: String?,
        val tourneeOrganismeLibelle: String?,
        val tourneeUtilisateurReservationLibelle: String?,
    ) {
        /** Retourne une chaine regroupant toutes les possibilités d'enchainements de trois champs : ABCABACBAC
         *  @param f1: TableField<Record, String?>
         *  @param f2: TableField<Record, String?>
         *  @param f3: TableField<Record, String?>
         *  @return Field<String>
         */
        private fun concatFieldTriple(f1: TableField<Record, String?>, f2: TableField<Record, String?>, f3: TableField<Record, String?>): Field<String> =
            concat(f1, DSL.`val`(" "), f2, DSL.`val`(" "), f3, DSL.`val`(" "), f1, DSL.`val`(" "), f2, DSL.`val`(" "), f1, DSL.`val`(" "), f3, DSL.`val`(" "), f2, DSL.`val`(" "), f1, DSL.`val`(" "), f3)

        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    tourneeLibelle?.let { DSL.and(TOURNEE.LIBELLE.containsIgnoreCase(it)) },
                    tourneeOrganismeLibelle?.let { DSL.and(ORGANISME.LIBELLE.containsIgnoreCase(it)) },
                    tourneeUtilisateurReservationLibelle?.let { DSL.and(concatFieldTriple(UTILISATEUR.PRENOM, UTILISATEUR.NOM, UTILISATEUR.USERNAME).containsIgnoreCase(it)) },
                ),
            )
    }

    data class Sort(
        val tourneeLibelle: Int?,
        val tourneeNbPei: Int?,
        val organismeLibelle: Int?,
        val tourneeEtat: Int?,
        val tourneeUtilisateurReservationLibelle: Int?,
        val tourneeActif: Int?,
    ) {
        fun toCondition(): List<SortField<*>> =
            listOfNotNull(
                TOURNEE.LIBELLE.getSortField(tourneeLibelle),
                ORGANISME.LIBELLE.getSortField(organismeLibelle),
                TOURNEE.ETAT.getSortField(tourneeEtat),
                UTILISATEUR.NOM.getSortField(tourneeUtilisateurReservationLibelle),
                UTILISATEUR.PRENOM.getSortField(tourneeUtilisateurReservationLibelle),
                TOURNEE.ACTIF.getSortField(tourneeActif),
            )
    }

    fun insertTournee(tournee: Tournee) =
        dsl.insertInto(TOURNEE)
            .set(dsl.newRecord(TOURNEE, tournee))
            .execute()

    fun tourneeAlreadyExists(tourneeLibelle: String, tourneeOrganismeId: UUID) =
        dsl.fetchExists(dsl.selectFrom(TOURNEE).where(TOURNEE.LIBELLE.eq(tourneeLibelle).and(TOURNEE.ORGANISME_ID.eq(tourneeOrganismeId))))

    fun updateTourneeLibelle(tourneeId: UUID, tourneeLibelle: String) =
        dsl.update(TOURNEE)
            .set(TOURNEE.LIBELLE, tourneeLibelle)
            .where(TOURNEE.ID.eq(tourneeId))
            .execute()

    fun getAllPeiByTourneeIdForDnD(tourneeId: UUID): List<PeiTourneeForDnD> =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            L_TOURNEE_PEI.ORDRE,
        )
            .from(L_TOURNEE_PEI)
            .join(PEI).on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .orderBy(L_TOURNEE_PEI.ORDRE)
            .fetchInto()

    data class PeiTourneeForDnD(
        val peiId: UUID,
        val peiNumeroComplet: String,
        val lTourneePeiOrdre: Int,
    )

    fun deleteLTourneePeiByTourneeId(tourneeId: UUID) =
        dsl.deleteFrom(L_TOURNEE_PEI)
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .execute()

    fun batchInsertLTourneePei(listeTourneePei: List<LTourneePei>) =
        dsl.batch(listeTourneePei.map { DSL.insertInto(L_TOURNEE_PEI).set(dsl.newRecord(L_TOURNEE_PEI, it)) }).execute()
}
