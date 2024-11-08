package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.jooq.SelectSeekStepN
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.data.enums.StatutIndisponibiliteTemporaireEnum
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.db.jooq.remocra.tables.references.INDISPONIBILITE_TEMPORAIRE
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.exception.RemocraResponseException
import remocra.utils.DateUtils
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class IndisponibiliteTemporaireRepository @Inject constructor(private val dsl: DSLContext, private val dateUtils: DateUtils) : AbstractRepository() {

    /**
     * Récupère une collection d'objets IndisponibiliteTemporaireWithPei filtrée et triée en fonction des paramètres fournis.
     *
     * @param params Les paramètres de filtrage et de tri.
     * @return Une collection d'IndisponibiliteTemporaireWithPei correspondant aux critères.
     */
    fun getAllWithListPei(params: Params<Filter, Sort>, isSuperAdmin: Boolean, zoneCompetenceId: UUID?): Collection<IndisponibiliteTemporaireWithPei> {
        return internalWithListPei(params, isSuperAdmin, zoneCompetenceId)
            .fetchInto()
    }

    /**
     * Compte le nombre d'éléments IndisponibiliteTemporaireWithPei correspondant aux paramètres de filtrage et de tri.
     *
     * @param params Les paramètres de filtrage et de tri.
     * @return Le nombre d'éléments correspondants.
     */
    fun countAllWithListPei(filterBy: Filter?, isSuperAdmin: Boolean, zoneCompetenceId: UUID?): Int =
        internalWithListPei(
            Params(limit = null, offset = null, filterBy = filterBy, sortBy = null),
            isSuperAdmin,
            zoneCompetenceId,
        ).count()

    /**
     * Crée une requête SQL pour récupérer des enregistrements d'indisponibilités temporaires
     * avec une liste associée de PEI, selon les paramètres donnés.
     *
     * @param params Les paramètres de filtrage et de tri.
     * @return Une étape de requête SQL pour les résultats filtrés et triés.
     */
    private fun internalWithListPei(params: Params<Filter, Sort>, isSuperAdmin: Boolean, zoneCompetenceId: UUID?): SelectSeekStepN<Record> {
        val nomCte = name("listePei")
        val cte = nomCte.fields("idIT", "listeNumeroPei").`as`(
            dsl.select(
                L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID,
                DSL.listAgg(PEI.NUMERO_COMPLET, ", ")
                    .withinGroupOrderBy(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID),
            )
                .from(PEI)
                .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
                .join(INDISPONIBILITE_TEMPORAIRE)
                .on(INDISPONIBILITE_TEMPORAIRE.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID))
                .leftJoin(ZONE_INTEGRATION)
                .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
                .where(
                    L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID),
                )
                .and(repositoryUtils.checkIsSuperAdminOrCondition(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE), isSuperAdmin))
                .groupBy(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID),

        )
        val indisponibiliteTemporaireId = field(name("listePei", "idIT"), SQLDataType.UUID)
        val listeNumeroPei = field(name("listePei", "listeNumeroPei"), SQLDataType.VARCHAR)

        return dsl.with(cte).select(
            *INDISPONIBILITE_TEMPORAIRE.fields(),
            listeNumeroPei,
        )
            .from(INDISPONIBILITE_TEMPORAIRE)
            .join(table(nomCte))
            .on(indisponibiliteTemporaireId.eq(INDISPONIBILITE_TEMPORAIRE.ID))
            .where(params.filterBy?.toCondition(listeNumeroPei) ?: DSL.noCondition())
            .orderBy(
                params.sortBy?.toCondition(listeNumeroPei) ?: listOf(
                    INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.asc(),
                    INDISPONIBILITE_TEMPORAIRE.DATE_FIN.asc(),
                ),
            )
    }

    fun upsert(element: IndisponibiliteTemporaire) = dsl.insertInto(
        INDISPONIBILITE_TEMPORAIRE,
    ).set(INDISPONIBILITE_TEMPORAIRE.ID, element.indisponibiliteTemporaireId)
        .set(INDISPONIBILITE_TEMPORAIRE.MOTIF, element.indisponibiliteTemporaireMotif)
        .set(INDISPONIBILITE_TEMPORAIRE.OBSERVATION, element.indisponibiliteTemporaireObservation)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT, element.indisponibiliteTemporaireDateDebut)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_FIN, element.indisponibiliteTemporaireDateFin)
        .set(INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_DISPONIBLE, element.indisponibiliteTemporaireBasculeAutoDisponible)
        .set(
            INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_INDISPONIBLE,
            element.indisponibiliteTemporaireBasculeAutoIndisponible,
        )
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailAvantIndisponibilite,
        )
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailApresIndisponibilite,
        )
        .onConflict(INDISPONIBILITE_TEMPORAIRE.ID)
        .doUpdate()
        .set(INDISPONIBILITE_TEMPORAIRE.MOTIF, element.indisponibiliteTemporaireMotif)
        .set(INDISPONIBILITE_TEMPORAIRE.OBSERVATION, element.indisponibiliteTemporaireObservation)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT, element.indisponibiliteTemporaireDateDebut)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_FIN, element.indisponibiliteTemporaireDateFin)
        .set(INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_DISPONIBLE, element.indisponibiliteTemporaireBasculeAutoDisponible)
        .set(
            INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_INDISPONIBLE,
            element.indisponibiliteTemporaireBasculeAutoIndisponible,
        )
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailAvantIndisponibilite,
        )
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailApresIndisponibilite,
        )
        .execute()

    fun insertLiaisonIndisponibiliteTemporairePei(indisponibiliteTemporaireId: UUID, peiId: UUID) = dsl.insertInto(
        L_INDISPONIBILITE_TEMPORAIRE_PEI,
    )
        .set(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID, indisponibiliteTemporaireId)
        .set(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID, peiId)
        .execute()

    fun getWithListPeiById(indisponibiliteTemporaireId: UUID): IndisponibiliteTemporaireData? {
        return getWithListPeiByIdOrPei()
            .where(INDISPONIBILITE_TEMPORAIRE.ID.eq(indisponibiliteTemporaireId))
            .fetchOneInto<IndisponibiliteTemporaireData>()
    }

    fun deleteLiaisonByIndisponibiliteTemporaire(indisponibiliteTemporaireId: UUID) {
        dsl.deleteFrom(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(indisponibiliteTemporaireId))
            .execute()
    }

    fun delete(indisponibiliteTemporaireId: UUID) {
        dsl.deleteFrom(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.ID.eq(indisponibiliteTemporaireId))
            .execute()
    }

    fun getWithListPeiByPei(idPei: UUID): List<IndisponibiliteTemporaireData> {
        return getWithListPeiByIdOrPei()
            .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID))
            .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(idPei))
            .fetchInto()
    }

    private fun getWithListPeiByIdOrPei(): SelectJoinStep<Record> = dsl.select(
        *INDISPONIBILITE_TEMPORAIRE.fields(),
        multiset(
            dsl.selectDistinct(PEI.ID)
                .from(PEI)
                .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
                .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID)),

        ).`as`("indisponibiliteTemporaireListePeiId").convertFrom { record ->
            record?.map { r ->
                r.value1().let { it as UUID }
            }
        },
    ).from(INDISPONIBILITE_TEMPORAIRE)

    data class IndisponibiliteTemporaireWithPei(
        val indisponibiliteTemporaireId: UUID,
        val indisponibiliteTemporaireDateDebut: ZonedDateTime,
        val indisponibiliteTemporaireDateFin: ZonedDateTime?,
        val indisponibiliteTemporaireMotif: String,
        val indisponibiliteTemporaireObservation: String?,
        val indisponibiliteTemporaireBasculeAutoIndisponible: Boolean,
        val indisponibiliteTemporaireBasculeAutoDisponible: Boolean,
        val indisponibiliteTemporaireMailAvantIndisponibilite: Boolean,
        val indisponibiliteTemporaireMailApresIndisponibilite: Boolean,
        val listeNumeroPei: String?,
    ) {
        val indisponibiliteTemporaireStatut: StatutIndisponibiliteTemporaireEnum
            get() {
                val now = ZonedDateTime.now()
                return when {
                    // Indisponibilité en cours
                    this.indisponibiliteTemporaireDateDebut.isBefore(now) &&
                        (this.indisponibiliteTemporaireDateFin?.isAfter(now) != false) -> {
                        StatutIndisponibiliteTemporaireEnum.EN_COURS
                    }

                    // Indisponibilité planifiée (début dans le futur)
                    this.indisponibiliteTemporaireDateDebut.isAfter(now) -> {
                        StatutIndisponibiliteTemporaireEnum.PLANIFIEE
                    }

                    // Indisponibilité terminée (fin dans le passé)
                    this.indisponibiliteTemporaireDateFin?.isBefore(now) == true -> {
                        StatutIndisponibiliteTemporaireEnum.TERMINEE
                    }

                    // Exception pour les statuts non trouvés
                    else -> {
                        throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_STATUT_INTROUVABLE)
                    }
                }
            }
    }

    data class Sort(

        val indisponibiliteTemporaireDateDebut: Int?,
        val indisponibiliteTemporaireDateFin: Int?,
        val indisponibiliteTemporaireMotif: Int?,
        val indisponibiliteTemporaireObservation: Int?,
        val indisponibiliteTemporaireStatut: Int?,
        val indisponibiliteTemporaireBasculeAutoIndisponible: Int?,
        val indisponibiliteTemporaireBasculeAutoDisponible: Int?,
        val indisponibiliteTemporaireMailAvantIndisponibilite: Int?,
        val indisponibiliteTemporaireMailApresIndisponibilite: Int?,
        val listeNumeroPei: Int?,

    ) {
        fun toCondition(listeNumeroPeiField: Field<String>): List<SortField<*>> = listOfNotNull(
            INDISPONIBILITE_TEMPORAIRE.MOTIF.getSortField(indisponibiliteTemporaireMotif),
            INDISPONIBILITE_TEMPORAIRE.OBSERVATION.getSortField(indisponibiliteTemporaireObservation),

            INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE.getSortField(
                indisponibiliteTemporaireMailApresIndisponibilite,
            ),
            INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE.getSortField(
                indisponibiliteTemporaireMailAvantIndisponibilite,
            ),
            INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_DISPONIBLE.getSortField(
                indisponibiliteTemporaireBasculeAutoDisponible,
            ),
            INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_INDISPONIBLE.getSortField(
                indisponibiliteTemporaireBasculeAutoIndisponible,
            ),

            INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.getSortField(indisponibiliteTemporaireDateDebut),
            INDISPONIBILITE_TEMPORAIRE.DATE_FIN.getSortField(indisponibiliteTemporaireDateFin),

            DSL.length(listeNumeroPeiField).getSortField(this.listeNumeroPei),
            listeNumeroPeiField.getSortField(this.listeNumeroPei),
        )
    }

    data class Filter(
        val indisponibiliteTemporaireMotif: String?,
        val indisponibiliteTemporaireObservation: String?,
        val indisponibiliteTemporaireStatut: StatutIndisponibiliteTemporaireEnum?,
        val indisponibiliteTemporaireBasculeAutoIndisponible: Boolean?,
        val indisponibiliteTemporaireBasculeAutoDisponible: Boolean?,
        val indisponibiliteTemporaireMailAvantIndisponibilite: Boolean?,
        val indisponibiliteTemporaireMailApresIndisponibilite: Boolean?,
        val listeNumeroPei: String?,
    ) {

        fun toCondition(listeNumeroPeiField: Field<String>): Condition =
            DSL.and(
                listOfNotNull(
                    // TODO voir pour les unaccents
                    indisponibiliteTemporaireMotif?.let { DSL.and(INDISPONIBILITE_TEMPORAIRE.MOTIF.containsIgnoreCase(it)) },
                    indisponibiliteTemporaireObservation?.let {
                        DSL.and(
                            INDISPONIBILITE_TEMPORAIRE.OBSERVATION.containsIgnoreCase(it),
                        )
                    },
                    this.listeNumeroPei?.let { DSL.and(listeNumeroPeiField.containsIgnoreCase(it)) },
                    indisponibiliteTemporaireMailApresIndisponibilite
                        ?.let { booleanFilter(it, INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE) },
                    indisponibiliteTemporaireMailAvantIndisponibilite
                        ?.let { booleanFilter(it, INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE) },
                    indisponibiliteTemporaireBasculeAutoDisponible
                        ?.let { booleanFilter(it, INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_DISPONIBLE) },
                    indisponibiliteTemporaireBasculeAutoIndisponible
                        ?.let { booleanFilter(it, INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_INDISPONIBLE) },
                ),
            )
    }

    /**
     * Retourne VRAI si le PEI possède au moins une IT en cours à l'instant donné, FALSE sinon
     * @param peiId: UUID
     *
     */
    fun hasPeiIndisponibiliteTemporaire(peiId: UUID): Boolean {
        val now = dateUtils.now()
        return dsl.fetchExists(
            dsl.select(INDISPONIBILITE_TEMPORAIRE.ID)
                .from(INDISPONIBILITE_TEMPORAIRE)
                .innerJoin(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .on(INDISPONIBILITE_TEMPORAIRE.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID))
                .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(peiId))
                .and(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(now))
                .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull.or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(now))),
        )
    }

    fun getITToNotifyDebut(delta: Long): List<IndisponibiliteTemporaire> =
        dsl.selectFrom(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE.isTrue)
            .and(INDISPONIBILITE_TEMPORAIRE.NOTIFICATION_DEBUT.isNull)
            .and(
                INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT
                    .sub(field("INTERVAL '$delta minute'", String::class.java))
                    .lessThan(dateUtils.now()),
            )
            .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(dateUtils.now()))
            .fetchInto()

    fun setNotificationDebut(dateNotification: ZonedDateTime) =
        dsl.update(INDISPONIBILITE_TEMPORAIRE)
            .set(INDISPONIBILITE_TEMPORAIRE.NOTIFICATION_DEBUT, dateNotification)
            .execute()

    fun getITToNotifyFin(delta: Long): List<IndisponibiliteTemporaire> =
        dsl.selectFrom(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE.isTrue)
            .and(INDISPONIBILITE_TEMPORAIRE.NOTIFICATION_FIN.isNull)
            .and(
                INDISPONIBILITE_TEMPORAIRE.DATE_FIN
                    .sub(field("INTERVAL '$delta minute'", String::class.java))
                    .lessThan(dateUtils.now()),
            )
            .and(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(dateUtils.now()))
            .fetchInto()

    fun setNotificationFin(dateNotification: ZonedDateTime) =
        dsl.update(INDISPONIBILITE_TEMPORAIRE)
            .set(INDISPONIBILITE_TEMPORAIRE.NOTIFICATION_FIN, dateNotification)
            .execute()

    /** Remonte toutes les Indispo Temp terminées dont la date NOTIFICATION_RESTE_INDISPO est null
     * @return une liste d'indisponibiliteTemporaire
     */
    fun getAllResteIndispoNotNotified(): List<IndisponibiliteTemporaire> =
        dsl.selectFrom(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE.isTrue)
            .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.le(dateUtils.now()))
            .and(INDISPONIBILITE_TEMPORAIRE.NOTIFICATION_RESTE_INDISPO.isNull)
            .and(INDISPONIBILITE_TEMPORAIRE.BASCULE_FIN)
            .fetchInto()

    fun setNotificationResteIndispo(dateNotification: ZonedDateTime) =
        dsl.update(INDISPONIBILITE_TEMPORAIRE)
            .set(INDISPONIBILITE_TEMPORAIRE.NOTIFICATION_RESTE_INDISPO, dateNotification)
            .execute()

    fun getPeiFromListIt(listItId: List<UUID>): List<PeiForItMoulinette> =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
        )
            .from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .join(PEI).on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
            .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.`in`(listItId))
            .fetchInto()

    fun getPeiResteIndispoFromItId(listItId: List<UUID>): List<PeiForItMoulinette> =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
        )
            .from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .join(PEI).on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
            .where(PEI.DISPONIBILITE_TERRESTRE.eq(Disponibilite.INDISPONIBLE))
            .and(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.`in`(listItId))
            .fetchInto()

    data class PeiForItMoulinette(
        val peiId: UUID,
        val peiNumeroComplet: String,
    )

    fun getItEnCoursToCalculIndispo(): List<UUID> =
        dsl.select(INDISPONIBILITE_TEMPORAIRE.ID)
            .from(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(dateUtils.now()))
            .and((INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(dateUtils.now())).or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull))
            .and(INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_INDISPONIBLE)
            .and(INDISPONIBILITE_TEMPORAIRE.BASCULE_DEBUT.isFalse)
            .fetchInto()

    fun getItTermineeToCalculIndispo(): List<UUID> =
        dsl.select(INDISPONIBILITE_TEMPORAIRE.ID)
            .from(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.le(dateUtils.now()))
            .and(INDISPONIBILITE_TEMPORAIRE.BASCULE_AUTO_DISPONIBLE)
            .and(INDISPONIBILITE_TEMPORAIRE.BASCULE_FIN.isFalse)
            .fetchInto()

    fun getAllPeiIdFromItId(itId: UUID): List<UUID> =
        dsl.select(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID)
            .from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(itId))
            .fetchInto()

    fun setBasculeDebutTrue(itId: UUID) =
        dsl.update(INDISPONIBILITE_TEMPORAIRE)
            .set(INDISPONIBILITE_TEMPORAIRE.BASCULE_DEBUT, true)
            .where(INDISPONIBILITE_TEMPORAIRE.ID.eq(itId))
            .execute()

    fun setBasculeFinTrue(itId: UUID) =
        dsl.update(INDISPONIBILITE_TEMPORAIRE)
            .set(INDISPONIBILITE_TEMPORAIRE.BASCULE_FIN, true)
            .where(INDISPONIBILITE_TEMPORAIRE.ID.eq(itId))
            .execute()
}
