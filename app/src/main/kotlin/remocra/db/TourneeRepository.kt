package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.concat
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.db.jooq.remocra.tables.references.V_PEI_DATE_RECOP
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class TourneeRepository
@Inject constructor(
    private val dsl: DSLContext,
) {
    fun getAllTourneeComplete(filter: Filter?): List<TourneeComplete> {
        val peiCounterCteName = name("PEI_COUNTER_CTE")
        val peiCounterCte = peiCounterCteName.fields("TOURNEE_ID", "TOURNEE_NB_PEI").`as`(
            select(
                L_TOURNEE_PEI.TOURNEE_ID,
                count(L_TOURNEE_PEI.PEI_ID).`as`("TOURNEE_NB_PEI"),
            )
                .from(L_TOURNEE_PEI)
                .groupBy(L_TOURNEE_PEI.TOURNEE_ID),
        )

        val nextRecopCteName = name("NEXT_RECOP_CTE")
        val nextRecopCte = nextRecopCteName.fields("TOURNEE_ID", "TOURNEE_NEXT_RECOP_DATE").`as`(
            select(
                L_TOURNEE_PEI.TOURNEE_ID,
                DSL.min(V_PEI_DATE_RECOP.PEI_NEXT_RECOP).`as`("TOURNEE_NEXT_RECOP_DATE"),
            )
                .from(L_TOURNEE_PEI)
                .join(V_PEI_DATE_RECOP).on(L_TOURNEE_PEI.PEI_ID.eq(V_PEI_DATE_RECOP.PEI_ID))
                .groupBy(L_TOURNEE_PEI.TOURNEE_ID),
        )

        return dsl.with(peiCounterCte, nextRecopCte)
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
                nextRecopCte.field("TOURNEE_NEXT_RECOP_DATE"),
            )
            .from(TOURNEE)
            .join(ORGANISME).on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .leftJoin(UTILISATEUR).on(TOURNEE.RESERVATION_UTILISATEUR_ID.eq(UTILISATEUR.ID))
            .leftJoin(table(peiCounterCteName))
            .on(TOURNEE.ID.eq(field(name("PEI_COUNTER_CTE", "TOURNEE_ID"), SQLDataType.UUID)))
            .leftJoin(table(nextRecopCteName))
            .on(TOURNEE.ID.eq(field(name("NEXT_RECOP_CTE", "TOURNEE_ID"), SQLDataType.UUID)))
            .where(filter?.toCondition() ?: DSL.noCondition())
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
        var tourneeNextRecopDate: ZonedDateTime?,
    )

    data class Filter(
        val tourneeLibelle: String?,
        val tourneeOrganismeLibelle: String?,
        val tourneeUtilisateurReservationLibelle: String?,
        val tourneeDeltaDate: String?,
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
        val tourneeNextRecopDate: Int?,
    ) {
        fun toCondition(list: Collection<TourneeComplete>): Collection<TourneeComplete> {
            return when {
                tourneeLibelle == 1 -> {
                    list.sortedBy { it.tourneeLibelle }
                }

                tourneeLibelle == -1 -> {
                    list.sortedByDescending { it.tourneeLibelle }
                }

                tourneeNbPei == 1 -> {
                    list.sortedBy { it.tourneeNbPei }
                }

                tourneeNbPei == -1 -> {
                    list.sortedByDescending { it.tourneeNbPei }
                }

                organismeLibelle == 1 -> {
                    list.sortedBy { it.organismeLibelle }
                }

                organismeLibelle == -1 -> {
                    list.sortedByDescending { it.organismeLibelle }
                }

                tourneeEtat == 1 -> {
                    list.sortedBy { it.tourneeEtat }
                }

                tourneeEtat == -1 -> {
                    list.sortedByDescending { it.tourneeEtat }
                }

                tourneeUtilisateurReservationLibelle == 1 -> {
                    list.sortedBy { it.tourneeUtilisateurReservationLibelle }
                }

                tourneeUtilisateurReservationLibelle == -1 -> {
                    list.sortedByDescending { it.tourneeUtilisateurReservationLibelle }
                }

                tourneeActif == 1 -> {
                    list.sortedBy { it.tourneeActif }
                }

                tourneeActif == -1 -> {
                    list.sortedByDescending { it.tourneeActif }
                }

                tourneeNextRecopDate == 1 -> {
                    list.sortedBy { it.tourneeNextRecopDate }
                }

                tourneeNextRecopDate == -1 -> {
                    list.sortedByDescending { it.tourneeNextRecopDate }
                }

                else -> {
                    list
                }
            }
        }
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

    fun getPeiForDnD(tourneeId: UUID): List<PeiTourneeForDnD> =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            NATURE_DECI.CODE,
            NATURE.LIBELLE,
            PEI.NUMERO_VOIE,
            VOIE.LIBELLE,
            COMMUNE.LIBELLE,
        )
            .from(TOURNEE)
            .join(ORGANISME).on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .join(ZONE_INTEGRATION).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .join(PEI).on(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .where(TOURNEE.ID.eq(tourneeId))
            .fetchInto()

    fun getAllPeiByTourneeIdForDnD(tourneeId: UUID): List<PeiTourneeForDnD> =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            NATURE_DECI.CODE,
            NATURE.LIBELLE,
            PEI.NUMERO_VOIE,
            VOIE.LIBELLE,
            COMMUNE.LIBELLE,
        )
            .from(L_TOURNEE_PEI)
            .join(PEI).on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .orderBy(L_TOURNEE_PEI.ORDRE)
            .fetchInto()

    data class PeiTourneeForDnD(
        val peiId: UUID,
        val peiNumeroComplet: String,
        val natureDeciCode: String,
        val natureLibelle: String,
        val peiNumeroVoie: Int?,
        val voieLibelle: String?,
        val communeLibelle: String,
    )

    fun getTourneeLibelleById(tourneeId: UUID): String =
        dsl.select(TOURNEE.LIBELLE).from(TOURNEE).where(TOURNEE.ID.eq(tourneeId)).fetchSingleInto()

    fun getTourneeOrganismeLibelleById(tourneeId: UUID): String =
        dsl.select(ORGANISME.LIBELLE)
            .from(TOURNEE)
            .join(ORGANISME).on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .where(TOURNEE.ID.eq(tourneeId)).fetchSingleInto()

    fun deleteLTourneePeiByTourneeId(tourneeId: UUID) =
        dsl.deleteFrom(L_TOURNEE_PEI)
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .execute()

    fun batchInsertLTourneePei(listeTourneePei: List<LTourneePei>) =
        dsl.batch(listeTourneePei.map { DSL.insertInto(L_TOURNEE_PEI).set(dsl.newRecord(L_TOURNEE_PEI, it)) }).execute()
}
