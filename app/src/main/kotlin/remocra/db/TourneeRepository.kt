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
import org.jooq.impl.DSL.listAgg
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import org.locationtech.jts.geom.Point
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DOMAINE
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import remocra.db.jooq.remocra.tables.references.VISITE
import remocra.db.jooq.remocra.tables.references.VISITE_CTRL_DEBIT_PRESSION
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.db.jooq.remocra.tables.references.V_PEI_VISITE_DATE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.AdresseDecorator
import remocra.utils.AdresseForDecorator
import remocra.utils.ST_Within
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

class TourneeRepository
@Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {
    fun getAllTourneeComplete(filter: Filter?, isSuperAdmin: Boolean, affiliatedOrganismeIds: Set<UUID>): List<TourneeComplete> {
        val peiCounterCteName = name("PEI_COUNTER_CTE")
        val peiCounterCte = peiCounterCteName.fields("TOURNEE_ID", "TOURNEE_NB_PEI").`as`(
            select(
                L_TOURNEE_PEI.TOURNEE_ID,
                count(L_TOURNEE_PEI.PEI_ID).`as`("TOURNEE_NB_PEI"),
            )
                .from(L_TOURNEE_PEI)
                .join(PEI)
                .on(PEI.ID.eq(L_TOURNEE_PEI.PEI_ID))
                .groupBy(L_TOURNEE_PEI.TOURNEE_ID),
        )

        val nextRopCteName = name("NEXT_ROP_CTE")
        val nextRopCte = nextRopCteName.fields("TOURNEE_ID", "TOURNEE_NEXT_ROP_DATE").`as`(
            select(
                L_TOURNEE_PEI.TOURNEE_ID,
                DSL.min(V_PEI_VISITE_DATE.PEI_NEXT_ROP).`as`("TOURNEE_NEXT_ROP_DATE"),
            )
                .from(L_TOURNEE_PEI)
                .join(V_PEI_VISITE_DATE).on(L_TOURNEE_PEI.PEI_ID.eq(V_PEI_VISITE_DATE.PEI_ID))
                .groupBy(L_TOURNEE_PEI.TOURNEE_ID),
        )

        return dsl.with(peiCounterCte, nextRopCte)
            .selectDistinct(
                TOURNEE.ID,
                TOURNEE.LIBELLE,
                TOURNEE.ORGANISME_ID,
                ORGANISME.LIBELLE,
                TOURNEE.POURCENTAGE_AVANCEMENT,
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
                nextRopCte.field("TOURNEE_NEXT_ROP_DATE"),
            )
            .from(TOURNEE)
            .join(ORGANISME).on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .leftJoin(UTILISATEUR).on(TOURNEE.RESERVATION_UTILISATEUR_ID.eq(UTILISATEUR.ID))
            .leftJoin(table(peiCounterCteName))
            .on(TOURNEE.ID.eq(field(name("PEI_COUNTER_CTE", "TOURNEE_ID"), SQLDataType.UUID)))
            .leftJoin(table(nextRopCteName))
            .on(TOURNEE.ID.eq(field(name("NEXT_ROP_CTE", "TOURNEE_ID"), SQLDataType.UUID)))
            .leftJoin(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.TOURNEE_ID.eq(TOURNEE.ID))
            .where(ORGANISME.ID.`in`(affiliatedOrganismeIds))
            .and(filter?.toCondition() ?: DSL.noCondition())
            .fetchInto()
    }

    fun getTourneeHorsZc(isSuperAdmin: Boolean, zoneCompetenceId: UUID?, listeTourneeId: List<UUID>): List<UUID> =
        if (isSuperAdmin) {
            listOf()
        } else {
            dsl.select(
                L_TOURNEE_PEI.TOURNEE_ID,
            )
                .from(L_TOURNEE_PEI)
                .join(PEI)
                .on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
                .leftJoin(ZONE_INTEGRATION)
                .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
                .where(
                    if (isSuperAdmin) {
                        DSL.falseCondition()
                    } else {
                        ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isFalse
                    },
                )
                .and(L_TOURNEE_PEI.TOURNEE_ID.`in`(listeTourneeId))
                .fetchInto()
        }

    private fun getTourneeByIdOrPei() =
        dsl.selectDistinct(TOURNEE.fields().asList())
            .from(TOURNEE)

    fun getTourneeById(tourneeId: UUID): Tournee =
        getTourneeByIdOrPei()
            .where(TOURNEE.ID.eq(tourneeId))
            .fetchSingleInto()

    /**
     * Retourne les tournées dont l'ID fait partie de la liste passée en paramètre
     *
     * @param idsTournees liste des idTournee
     * @return List<Tournee>
     */
    fun getTourneesByIds(idsTournees: List<UUID>): List<Tournee> {
        return dsl
            .selectFrom(TOURNEE)
            .where(TOURNEE.ID.`in`(idsTournees))
            .fetchInto()
    }

    /**
     * Permet de réserver les tournées dont l'ID est passé en paramètre
     *
     * @param idsTournees: List<UUID>
     * @param idUtilisateur id de l'utilisateur connecté
     */
    fun reserveTournees(idsTournees: List<UUID>, idUtilisateur: UUID): Int = dsl.update(TOURNEE)
        .set(TOURNEE.RESERVATION_UTILISATEUR_ID, idUtilisateur)
        .where(TOURNEE.ID.`in`(idsTournees))
        .execute()

    fun getTourneesActives(isSuperAdmin: Boolean, listeOrganisme: Set<UUID>, isPrive: Boolean?, onlyAvailable: Boolean?, onlyNonTerminees: Boolean?): List<Tournee> =
        getTourneeByIdOrPei()
            .leftJoin(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.TOURNEE_ID.eq(TOURNEE.ID))
            .leftJoin(PEI)
            .on(PEI.ID.eq(L_TOURNEE_PEI.PEI_ID))
            .leftJoin(NATURE_DECI)
            .on(NATURE_DECI.ID.eq(PEI.NATURE_DECI_ID))
            .where(TOURNEE.ACTIF.isTrue)
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    isSuperAdmin = isSuperAdmin,
                    condition = TOURNEE.ORGANISME_ID.`in`(listeOrganisme),
                ),
            )
            .let {
                if (isPrive != null) {
                    if (isPrive) {
                        it.and(NATURE_DECI.CODE.eq(GlobalConstants.NATURE_DECI_PRIVE)).or(NATURE_DECI.CODE.isNull)
                    } else {
                        it.and(NATURE_DECI.CODE.ne(GlobalConstants.NATURE_DECI_PRIVE)).or(NATURE_DECI.CODE.isNull)
                    }
                } else {
                    it
                }
            }
            .let {
                if (onlyAvailable == true) {
                    it.and(TOURNEE.RESERVATION_UTILISATEUR_ID.isNull)
                } else {
                    it
                }
            }
            .let {
                if (onlyNonTerminees == true) {
                    it.and(TOURNEE.POURCENTAGE_AVANCEMENT.lt(100))
                } else {
                    it
                }
            }
            .fetchInto()

    fun getTourneeByPei(peiId: UUID): List<Tournee> =
        getTourneeByIdOrPei()
            .join(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.PEI_ID.eq(peiId))
            .fetchInto()

    data class TourneeComplete(
        val tourneeId: UUID,
        val tourneeLibelle: String,
        val tourneeOrganismeId: UUID,
        val organismeLibelle: String,
        val tourneePourcentageAvancement: Int?,
        val tourneeReservationUtilisateurId: UUID?,
        val tourneeUtilisateurReservationLibelle: String?,
        val tourneeDateSynchronisation: ZonedDateTime?,
        val tourneeActif: Boolean,
        val tourneeNbPei: Int,
        var tourneeNextRopDate: ZonedDateTime?,
        var isModifiable: Boolean = true,
    )

    data class Filter(
        val tourneeLibelle: String?,
        val tourneeOrganismeLibelle: String?,
        val tourneeUtilisateurReservationLibelle: String?,
        val tourneeDeltaDate: String?,
        val peiId: UUID?,
        val tourneeActif: Boolean?,
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
                    tourneeLibelle?.let { DSL.and(TOURNEE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    tourneeOrganismeLibelle?.let { DSL.and(ORGANISME.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    tourneeUtilisateurReservationLibelle?.let { DSL.and(concatFieldTriple(UTILISATEUR.PRENOM, UTILISATEUR.NOM, UTILISATEUR.USERNAME).containsIgnoreCase(it)) },
                    peiId?.let { DSL.and(L_TOURNEE_PEI.PEI_ID.eq(it)) },
                    tourneeActif?.let { DSL.and(TOURNEE.ACTIF.eq(it)) },
                ),
            )
    }

    data class Sort(
        val tourneeLibelle: Int?,
        val tourneeNbPei: Int?,
        val organismeLibelle: Int?,
        val tourneePourcentageAvancement: Int?,
        val tourneeUtilisateurReservationLibelle: Int?,
        val tourneeActif: Int?,
        val tourneeNextRopDate: Int?,
    ) {
        fun toCondition(list: Collection<TourneeComplete>): Collection<TourneeComplete> {
            return when {
                tourneeLibelle == 1 -> {
                    list.sortedBy { it.tourneeLibelle.uppercase() }
                }

                tourneeLibelle == -1 -> {
                    list.sortedByDescending { it.tourneeLibelle.uppercase() }
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

                tourneePourcentageAvancement == 1 -> {
                    list.sortedBy { it.tourneePourcentageAvancement }
                }

                tourneePourcentageAvancement == -1 -> {
                    list.sortedByDescending { it.tourneePourcentageAvancement }
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

                tourneeNextRopDate == 1 -> {
                    list.sortedBy { it.tourneeNextRopDate }
                }

                tourneeNextRopDate == -1 -> {
                    list.sortedByDescending { it.tourneeNextRopDate }
                }

                else -> {
                    list.sortedBy { it.tourneeLibelle }
                }
            }
        }
    }

    fun insertTournee(tournee: Tournee) =
        dsl.insertInto(TOURNEE)
            .set(dsl.newRecord(TOURNEE, tournee))
            .execute()

    fun tourneeAlreadyExists(tourneeId: UUID, tourneeLibelle: String, tourneeOrganismeId: UUID) =
        dsl.fetchExists(
            dsl.selectFrom(TOURNEE)
                .where(
                    TOURNEE.LIBELLE.eq(tourneeLibelle)
                        .and(
                            TOURNEE.ORGANISME_ID.eq(tourneeOrganismeId)
                                .and(TOURNEE.ID.notEqual(tourneeId)),
                        ),
                ),
        )

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
            // On projette tous les champs composant l'adresse
            PEI.EN_FACE, PEI.NUMERO_VOIE, PEI.SUFFIXE_VOIE, PEI.VOIE_TEXTE, VOIE.LIBELLE, PEI.COMPLEMENT_ADRESSE,
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
            .orderBy(
                COMMUNE.LIBELLE,
                DSL.length(PEI.NUMERO_COMPLET).asc(),
                PEI.NUMERO_COMPLET.asc(),
            )
            .fetch().map { record ->
                PeiTourneeForDnD(
                    peiId = record.component1()!!,
                    peiNumeroComplet = record.component2()!!,
                    natureDeciCode = record.component3()!!,
                    natureLibelle = record.component4()!!,
                    adresse = AdresseDecorator().decorateAdresse(
                        AdresseForDecorator(
                            enFace = record.component5(),
                            numeroVoie = record.component6(),
                            suffixeVoie = record.component7(),
                            voie = null,
                            //  "hack", pour afficher le libellé de la voie sans remonter l'objet Voie (qui contient une géométrie)
                            voieTexte = if (record.component8() != null) record.component8() else record.component9(),
                            complementAdresse = record.component10(),
                        ),
                    ),
                    communeLibelle = record.component11()!!,
                    lTourneePeiOrdre = null,
                )
            }

    fun getAllPeiByTourneeIdForDnD(tourneeId: UUID, listePeiId: Set<UUID>?): List<PeiTourneeForDnD> =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            NATURE_DECI.CODE,
            NATURE.LIBELLE,
            // On projette tous les champs composant l'adresse
            PEI.EN_FACE, PEI.NUMERO_VOIE, PEI.SUFFIXE_VOIE, PEI.VOIE_TEXTE, VOIE.LIBELLE, PEI.COMPLEMENT_ADRESSE,
            COMMUNE.LIBELLE,
            L_TOURNEE_PEI.ORDRE,
        ).distinctOn(PEI.ID)
            .from(L_TOURNEE_PEI)
            .leftJoin(PEI).on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID).or(PEI.ID.`in`(listePeiId)))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .let {
                if (!listePeiId.isNullOrEmpty()) {
                    it.or(PEI.ID.`in`(listePeiId))
                } else {
                    it
                }
            }
            .fetch().map { record ->
                PeiTourneeForDnD(
                    peiId = record.component1()!!,
                    peiNumeroComplet = record.component2()!!,
                    natureDeciCode = record.component3()!!,
                    natureLibelle = record.component4()!!,
                    adresse = AdresseDecorator().decorateAdresse(
                        AdresseForDecorator(
                            enFace = record.component5(),
                            numeroVoie = record.component6(),
                            suffixeVoie = record.component7(),
                            voie = null,
                            //  "hack", pour afficher le libellé de la voie sans remonter l'objet Voie (qui contient une géométrie)
                            voieTexte = if (record.component8() != null) record.component8() else record.component9(),
                            complementAdresse = record.component10(),
                        ),
                    ),
                    communeLibelle = record.component11()!!,
                    lTourneePeiOrdre = record.component12(),
                )
            }

    data class PeiTourneeForDnD(
        val peiId: UUID,
        val peiNumeroComplet: String,
        val natureDeciCode: String,
        val natureLibelle: String,
        val adresse: String?,
        val communeLibelle: String,
        val lTourneePeiOrdre: Int?,
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

    fun deleteLTourneePeiByTourneeAndPeiId(tourneeId: UUID, peiId: UUID) =
        dsl.deleteFrom(L_TOURNEE_PEI)
            .where(L_TOURNEE_PEI.PEI_ID.eq(peiId))
            .and(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .execute()

    fun batchInsertLTourneePei(listeTourneePei: List<LTourneePei>) =
        dsl.batch(listeTourneePei.map { DSL.insertInto(L_TOURNEE_PEI).set(dsl.newRecord(L_TOURNEE_PEI, it)) }).execute()

    fun deleteTournee(tourneeId: UUID) =
        dsl.deleteFrom(TOURNEE)
            .where(TOURNEE.ID.eq(tourneeId))
            .execute()

    // Saisie visite en masse
    fun getTourneeLibelle(tourneeId: UUID): String =
        dsl.select(TOURNEE.LIBELLE)
            .from(TOURNEE)
            .where(TOURNEE.ID.eq(tourneeId))
            .fetchSingleInto()

    fun getPeiVisiteTourneeInformation(tourneeId: UUID): List<PeiVisiteTourneeInformation> {
        val concatAnomaliesCteName = name("CONCAT_ANOMALIES_CTE")
        val concatAnomaliesCte = concatAnomaliesCteName.fields("PEI_ID", "LISTE_ANOMALIES").`as`(
            select(
                L_PEI_ANOMALIE.PEI_ID,
                listAgg(ANOMALIE.LIBELLE, ", ").withinGroupOrderBy(ANOMALIE_CATEGORIE.LIBELLE, ANOMALIE.LIBELLE).`as`("LISTE_ANOMALIES"),
            )
                .from(L_PEI_ANOMALIE)
                .join(ANOMALIE).on(L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                .join(ANOMALIE_CATEGORIE).on(ANOMALIE.ANOMALIE_CATEGORIE_ID.eq(ANOMALIE_CATEGORIE.ID))
                .where(ANOMALIE_CATEGORIE.CODE.ne(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME)) // notEqual
                .groupBy(L_PEI_ANOMALIE.PEI_ID),
        )

        val nextVisiteCteName = name("NEXT_VISITE_CTE")
        val nextVisiteCte = nextVisiteCteName.fields("PEI_ID", "PEI_NEXT_ROP", "PEI_NEXT_CTP").`as`(
            select(
                V_PEI_VISITE_DATE.PEI_ID,
                V_PEI_VISITE_DATE.PEI_NEXT_ROP,
                V_PEI_VISITE_DATE.PEI_NEXT_CTP,
            )
                .from(V_PEI_VISITE_DATE),
        )

        return dsl.with(concatAnomaliesCte, nextVisiteCte)
            .select(
                PEI.ID,
                PEI.NUMERO_COMPLET,
                NATURE_DECI.CODE,
                NATURE_DECI.LIBELLE,
                DOMAINE.LIBELLE,
                NATURE.LIBELLE,
                PEI.TYPE_PEI,
                COMMUNE.LIBELLE,
                COMMUNE.CODE_INSEE,
                COMMUNE.CODE_POSTAL,
                PEI.DISPONIBILITE_TERRESTRE,
                GESTIONNAIRE.LIBELLE,
                concatAnomaliesCte.field("LISTE_ANOMALIES"),
                nextVisiteCte.field("PEI_NEXT_ROP"),
                nextVisiteCte.field("PEI_NEXT_CTP"),
                // On projette tous les champs composant l'adresse
                PEI.EN_FACE, PEI.NUMERO_VOIE, PEI.SUFFIXE_VOIE, PEI.VOIE_TEXTE, VOIE.LIBELLE, PEI.COMPLEMENT_ADRESSE,
            )
            .from(PEI)
            .join(L_TOURNEE_PEI).on(PEI.ID.eq(L_TOURNEE_PEI.PEI_ID))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .leftJoin(table(concatAnomaliesCteName))
            .on(PEI.ID.eq(field(name("CONCAT_ANOMALIES_CTE", "PEI_ID"), SQLDataType.UUID)))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .join(DOMAINE).on(PEI.DOMAINE_ID.eq(DOMAINE.ID))
            .leftJoin(GESTIONNAIRE).on(PEI.GESTIONNAIRE_ID.eq(GESTIONNAIRE.ID))
            .join(table(nextVisiteCteName))
            .on(PEI.ID.eq(field(name("NEXT_VISITE_CTE", "PEI_ID"), SQLDataType.UUID)))
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .fetch().map { record ->
                PeiVisiteTourneeInformation(
                    peiId = record.component1()!!, peiNumeroComplet = record.component2()!!, natureDeciCode = record.component3()!!, natureDeciLibelle = record.component4()!!, domaineLibelle = record.component5()!!, natureLibelle = record.component6()!!, peiTypePei = record.component7()!!, communeLibelle = record.component8()!!, communeCodeInsee = record.component9()!!, communeCodePostal = record.component10()!!, peiDisponibiliteTerrestre = record.component11()!!, gestionnaireLibelle = record.component12(), listeAnomalies = record.component13() as String?, peiNextRop = record.component14() as ZonedDateTime?, peiNextCtp = record.component15() as ZonedDateTime?,
                    adresse = AdresseDecorator().decorateAdresse(
                        AdresseForDecorator(
                            enFace = record.component16(),
                            numeroVoie = record.component17(),
                            suffixeVoie = record.component18(),
                            voie = null,
                            //  "hack", pour afficher le libellé de la voie sans remonter l'objet Voie (qui contient une géométrie)
                            voieTexte = if (record.component19() != null) record.component19() else record.component20(),
                            complementAdresse = record.component21(),
                        ),
                    ),

                )
            }
    }

    data class PeiVisiteTourneeInformation(
        val peiId: UUID,
        val peiNumeroComplet: String,
        val natureDeciCode: String,
        val natureDeciLibelle: String,
        val domaineLibelle: String,
        val natureLibelle: String,
        val peiTypePei: TypePei,
        val communeLibelle: String,
        val communeCodeInsee: String,
        val communeCodePostal: String,
        val peiDisponibiliteTerrestre: Disponibilite,
        val gestionnaireLibelle: String?,
        val listeAnomalies: String?,
        val peiNextRop: ZonedDateTime?,
        val peiNextCtp: ZonedDateTime?,
        val adresse: String?,
    )

    fun getListLastPeiCDPByTournee(tourneeId: UUID): List<CDPByPeiId> {
        val lastCDPCteName = name("LAST_CDP_CTE")
        val lastCDPCte = lastCDPCteName.fields("PEI_ID", "MAX_DATE").`as`(
            select(
                VISITE.PEI_ID,
                DSL.max(VISITE.DATE).`as`("MAX_DATE"),
            )
                .from(VISITE_CTRL_DEBIT_PRESSION)
                .join(VISITE).on(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(VISITE.ID))
                .groupBy(VISITE.PEI_ID),
        )
        return dsl.with(lastCDPCte)
            .select(
                PEI.ID,
                VISITE_CTRL_DEBIT_PRESSION.VISITE_ID,
                VISITE_CTRL_DEBIT_PRESSION.DEBIT,
                VISITE_CTRL_DEBIT_PRESSION.PRESSION,
                VISITE_CTRL_DEBIT_PRESSION.PRESSION_DYN,
            )
            .distinctOn(PEI.ID)
            .from(PEI)
            .leftJoin(table(lastCDPCteName)).on(PEI.ID.eq(field(name("LAST_CDP_CTE", "PEI_ID"), SQLDataType.UUID)))
            .leftJoin(VISITE).on(PEI.ID.eq(VISITE.PEI_ID)).and(
                field(name("LAST_CDP_CTE", "MAX_DATE"), ZonedDateTime::class.java).eq(
                    VISITE.DATE,
                ),
            )
            .leftJoin(VISITE_CTRL_DEBIT_PRESSION).on(VISITE.ID.eq(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID))
            .join(L_TOURNEE_PEI).on(PEI.ID.eq(L_TOURNEE_PEI.PEI_ID))
            .where(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .orderBy(PEI.ID)
            .fetchInto()
    }

    fun setAvancementTournee(tourneeId: UUID, avancement: Int) =
        dsl.update(TOURNEE)
            .set(TOURNEE.POURCENTAGE_AVANCEMENT, avancement)
            .where(TOURNEE.ID.eq(tourneeId))
            .execute()

    fun desaffectationTournee(tourneeId: UUID) =
        dsl.update(TOURNEE)
            .setNull(TOURNEE.RESERVATION_UTILISATEUR_ID)
            .where(TOURNEE.ID.eq(tourneeId))
            .execute()

    /**
     * Permet de récupérer les PEI associés aux tournées passées en paramètre
     *
     * @param listTourneeId : id des tournées dont on veut connaître les PEI
     * @return une map <idTournee, List<peiId>
     */
    fun getListPeiByListTournee(listTourneeId: List<UUID>) = dsl
        .select(L_TOURNEE_PEI.TOURNEE_ID, L_TOURNEE_PEI.PEI_ID)
        .from(L_TOURNEE_PEI)
        .where(L_TOURNEE_PEI.TOURNEE_ID.`in`(listTourneeId))
        .fetchGroups(L_TOURNEE_PEI.TOURNEE_ID, LTourneePei::class.java)

    fun annuleReservation(idTournee: UUID, idUtilisateur: UUID): Boolean {
        return dsl
            .update(TOURNEE)
            .setNull(TOURNEE.RESERVATION_UTILISATEUR_ID)
            .where(TOURNEE.ID.eq(idTournee))
            .and(TOURNEE.RESERVATION_UTILISATEUR_ID.eq(idUtilisateur))
            .execute() == 1
    }

    data class CDPByPeiId(
        val peiId: UUID,
        val visiteCtrlDebitPressionVisiteId: UUID?,
        val visiteCtrlDebitPressionDebit: Int?,
        val visiteCtrlDebitPressionPression: BigDecimal?,
        val visiteCtrlDebitPressionPressionDyn: BigDecimal?,
    )

    fun updateDateSynchronisation(dateSynchronisation: ZonedDateTime, tourneeId: UUID): Int = dsl.update(TOURNEE)
        .set(TOURNEE.DATE_SYNCHRONISATION, dateSynchronisation)
        .where(TOURNEE.ID.eq(tourneeId))
        .execute()

    fun getTourneeByZoneIntegrationShortData(userInfo: UserInfo): Collection<TourneeShortData> {
        if (userInfo.isSuperAdmin) {
            return dsl.select(TOURNEE.ID, TOURNEE.LIBELLE)
                .from(TOURNEE)
                .fetchInto()
        }
        return dsl.select(TOURNEE.ID, TOURNEE.LIBELLE)
            .from(TOURNEE)
            .join(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.TOURNEE_ID.eq(TOURNEE.ID))
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(userInfo.zoneCompetence?.zoneIntegrationId))
            .join(PEI)
            .on(PEI.ID.eq(L_TOURNEE_PEI.PEI_ID))
            .and(ST_Within(Pei.PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .fetchInto()
    }

    fun getGeometrieTournee(tourneeId: UUID): Collection<Point> =
        dsl.select(PEI.GEOMETRIE)
            .from(PEI)
            .join(L_TOURNEE_PEI).on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
            .join(TOURNEE).on(TOURNEE.ID.eq(tourneeId)).and(TOURNEE.ID.eq(L_TOURNEE_PEI.TOURNEE_ID))
            .fetchInto()
}

data class TourneeShortData(
    val tourneeId: UUID,
    val tourneeLibelle: String,
)
