package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import remocra.auth.WrappedUserInfo
import remocra.data.ApiIndispoTemporaireData
import remocra.data.GeometrieWithPeiId
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.data.enums.StatutIndisponibiliteTemporaireEnum
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.INDISPONIBILITE_TEMPORAIRE
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.exception.RemocraResponseException
import remocra.utils.DateUtils
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.collections.joinToString

class IndisponibiliteTemporaireRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Récupère une collection d'objets IndisponibiliteTemporaireWithPei filtrée et triée en fonction des paramètres fournis.
     *
     * @param params Les paramètres de filtrage et de tri.
     * @return Une collection d'IndisponibiliteTemporaireWithPei correspondant aux critères.
     */
    fun getAllWithListPei(params: Params<Filter, Sort>, isSuperAdmin: Boolean, zoneCompetenceId: UUID?): Collection<IndisponibiliteTemporaireWithPei> {
        val nomCte = name("liste_pei")
        val cte = nomCte.fields("id_it", "liste_numero_pei").`as`(
            DSL.select(
                L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID,
                DSL.listAgg(PEI.NUMERO_COMPLET, ", ")
                    .withinGroupOrderBy(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID),
            )
                .from(PEI)
                .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
                .join(INDISPONIBILITE_TEMPORAIRE)
                .on(INDISPONIBILITE_TEMPORAIRE.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID))
                .where(
                    L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID),
                )
                .groupBy(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID),

        )

        val indisponibiliteTemporaireId = field(name("liste_pei", "id_it"), SQLDataType.UUID)
        val listeNumeroPei = field(name("liste_pei", "liste_numero_pei"), SQLDataType.VARCHAR)

        return dsl.with(cte).select(
            *INDISPONIBILITE_TEMPORAIRE.fields(),
            listeNumeroPei,
            // Ajout du multiset pour les communes distinctes
            multiset(
                DSL.selectDistinct(COMMUNE.LIBELLE)
                    .from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                    .join(PEI).on(PEI.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID))
                    .join(COMMUNE).on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
                    .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID)),
            ).`as`("listeCommunes").convertFrom { it?.map { r -> r.value1() }?.joinToString() },
        )
            .from(INDISPONIBILITE_TEMPORAIRE)
            .join(table(nomCte))
            .on(indisponibiliteTemporaireId.eq(INDISPONIBILITE_TEMPORAIRE.ID))
            .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .on(
                L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID)
                    .and(indisponibiliteTemporaireId.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID)),
            )
            .join(PEI)
            .on(PEI.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID))
            .leftJoin(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
            // Pour les filtres, on join sur la commune
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .where(params.filterBy?.toCondition(dateUtils) ?: DSL.noCondition())
            .and(repositoryUtils.checkIsSuperAdminOrCondition(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue, isSuperAdmin))
            .groupBy(
                INDISPONIBILITE_TEMPORAIRE.ID,
                listeNumeroPei,
            )
            .orderBy(
                params.sortBy?.toCondition(listeNumeroPei)?.let {
                    it.ifEmpty {
                        listOf(
                            INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.asc(),
                            INDISPONIBILITE_TEMPORAIRE.DATE_FIN.asc(),
                        )
                    }
                },
            )
            .fetchInto()
    }

    fun getAllWithListPei(params: Params<Filter, Sort>, userInfo: WrappedUserInfo): Collection<IndisponibiliteTemporaireWithPei> {
        val listeIndisponibiliteTemporaire = getAllWithListPei(params, userInfo.isSuperAdmin, userInfo.zoneCompetence?.zoneIntegrationId)
        val listeIndispoTempNonModifiable = getIndispoTemporaireHorsZC(
            userInfo.isSuperAdmin,
            userInfo.zoneCompetence?.zoneIntegrationId,
            listeIndisponibiliteTemporaire.map { it.indisponibiliteTemporaireId },
        )

        listeIndisponibiliteTemporaire.forEach {
            it.isModifiable = !listeIndispoTempNonModifiable.contains(it.indisponibiliteTemporaireId)
        }
        return listeIndisponibiliteTemporaire
    }

    fun getIndispoTemporaireHorsZC(isSuperAdmin: Boolean, zoneCompetenceId: UUID?, listItId: List<UUID>): List<UUID> =
        if (isSuperAdmin) {
            listOf()
        } else {
            dsl.select(
                L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID,
            )
                .from(PEI)
                .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
                .leftJoin(ZONE_INTEGRATION)
                .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
                .where(
                    ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isFalse,
                )
                .and(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.`in`(listItId))
                .fetchInto()
        }

    fun upsert(element: IndisponibiliteTemporaire) = dsl.insertInto(
        INDISPONIBILITE_TEMPORAIRE,
    ).set(INDISPONIBILITE_TEMPORAIRE.ID, element.indisponibiliteTemporaireId)
        .set(INDISPONIBILITE_TEMPORAIRE.MOTIF, element.indisponibiliteTemporaireMotif)
        .set(INDISPONIBILITE_TEMPORAIRE.OBSERVATION, element.indisponibiliteTemporaireObservation)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT, element.indisponibiliteTemporaireDateDebut)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_FIN, element.indisponibiliteTemporaireDateFin)
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailAvantIndisponibilite,
        )
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailApresIndisponibilite,
        )
        .onConflict(INDISPONIBILITE_TEMPORAIRE.ID)
        .doUpdate()
        .set(INDISPONIBILITE_TEMPORAIRE.MOTIF, element.indisponibiliteTemporaireMotif)
        .set(INDISPONIBILITE_TEMPORAIRE.OBSERVATION, element.indisponibiliteTemporaireObservation)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT, element.indisponibiliteTemporaireDateDebut)
        .set(INDISPONIBILITE_TEMPORAIRE.DATE_FIN, element.indisponibiliteTemporaireDateFin)
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailAvantIndisponibilite,
        )
        .set(
            INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE,
            element.indisponibiliteTemporaireMailApresIndisponibilite,
        )
        .execute()

    fun insertLiaisonIndisponibiliteTemporairePei(indisponibiliteTemporaireId: UUID, peiId: UUID) = dsl.insertInto(
        L_INDISPONIBILITE_TEMPORAIRE_PEI,
    )
        .set(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID, indisponibiliteTemporaireId)
        .set(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID, peiId)
        .execute()

    fun getWithListPeiById(indisponibiliteTemporaireId: UUID): IndisponibiliteTemporaireData {
        return getWithListPeiByIdOrPei()
            .where(INDISPONIBILITE_TEMPORAIRE.ID.eq(indisponibiliteTemporaireId))
            .fetchOneInto<IndisponibiliteTemporaireData>()
            ?: throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_INEXISTANTE)
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
        val listeCommunes: String,
        var isModifiable: Boolean = false,
    )

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
        val listePeiId: List<UUID>?,
        val communeLibelle: String?,
    ) {

        fun toCondition(dateUtils: DateUtils): Condition =
            DSL.and(
                listOfNotNull(
                    indisponibiliteTemporaireMotif?.let { DSL.and(INDISPONIBILITE_TEMPORAIRE.MOTIF.containsIgnoreCaseUnaccent(it)) },
                    indisponibiliteTemporaireObservation?.let {
                        DSL.and(
                            INDISPONIBILITE_TEMPORAIRE.OBSERVATION.containsIgnoreCaseUnaccent(it),
                        )
                    },
                    indisponibiliteTemporaireStatut?.let { statut ->
                        val now = dateUtils.now()
                        when (statut) {
                            StatutIndisponibiliteTemporaireEnum.PLANIFIEE ->
                                INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.gt(now)
                            StatutIndisponibiliteTemporaireEnum.EN_COURS ->
                                INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(now)
                                    .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(now).or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull))

                            StatutIndisponibiliteTemporaireEnum.TERMINEE ->
                                INDISPONIBILITE_TEMPORAIRE.DATE_FIN.lt(now)

                            StatutIndisponibiliteTemporaireEnum.EN_COURS_PLANIFIEE ->
                                INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.gt(now)
                                    .or(
                                        INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(now)
                                            .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(now).or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull)),
                                    )
                        }
                    },
                    listePeiId?.let { DSL.and(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.`in`(it)) },
                    indisponibiliteTemporaireMailApresIndisponibilite
                        ?.let { booleanFilter(it, INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE) },
                    indisponibiliteTemporaireMailAvantIndisponibilite
                        ?.let { booleanFilter(it, INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE) },
                    communeLibelle?.let {
                        DSL.and(
                            COMMUNE.LIBELLE.containsIgnoreCaseUnaccent(it),
                        )
                    },
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
            .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(dateUtils.now()).or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull))
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
            .and(INDISPONIBILITE_TEMPORAIRE.BASCULE_DEBUT.isFalse)
            .fetchInto()

    fun getItTermineeToCalculIndispo(): List<UUID> =
        dsl.select(INDISPONIBILITE_TEMPORAIRE.ID)
            .from(INDISPONIBILITE_TEMPORAIRE)
            .where(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.le(dateUtils.now()))
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

    fun getGeometrieIndispoTemp(indisponibiliteTemporaireId: UUID): Collection<GeometrieWithPeiId> =
        dsl.select(PEI.GEOMETRIE, PEI.ID)
            .from(PEI)
            .join(L_INDISPONIBILITE_TEMPORAIRE_PEI)
            .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
            .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(indisponibiliteTemporaireId))
            .fetchInto()

    fun getAllForApi(organismeId: UUID, filterPeiNumeroComplet: String?, statutIndisponibiliteTemporaireEnum: StatutIndisponibiliteTemporaireEnum?): Collection<ApiIndispoTemporaireData> {
        val nomCte = name("liste_pei")
        val indisponibiliteTemporaireId = field(name("liste_pei", "id_it"), SQLDataType.UUID)
        val listeNumeroPei = field(name("liste_pei", "liste_numero_pei"), SQLDataType.VARCHAR)
        val cte = nomCte.fields("id_it", "liste_numero_pei").`as`(
            DSL.select(
                L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID,
                DSL.listAgg(PEI.NUMERO_COMPLET, ", ")
                    .withinGroupOrderBy(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID),
            )
                .from(PEI)
                .join(L_INDISPONIBILITE_TEMPORAIRE_PEI).on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
                .groupBy(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID),
        )

        return dsl.with(cte).select(
            INDISPONIBILITE_TEMPORAIRE.ID,
            INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT,
            INDISPONIBILITE_TEMPORAIRE.DATE_FIN,
            INDISPONIBILITE_TEMPORAIRE.MOTIF,
            INDISPONIBILITE_TEMPORAIRE.OBSERVATION,
            INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT_INDISPONIBILITE,
            INDISPONIBILITE_TEMPORAIRE.MAIL_APRES_INDISPONIBILITE,
            listeNumeroPei,
        ).from(PEI)
            .join(L_INDISPONIBILITE_TEMPORAIRE_PEI).on(PEI.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID))
            .join(INDISPONIBILITE_TEMPORAIRE)
            .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(INDISPONIBILITE_TEMPORAIRE.ID))
            .join(ORGANISME).on(ORGANISME.ID.eq(organismeId))
            .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .leftJoin(PIBI).on(PEI.ID.eq(PIBI.ID))
            .join(table(nomCte)).on(indisponibiliteTemporaireId.eq(INDISPONIBILITE_TEMPORAIRE.ID))
            .where(
                TYPE_ORGANISME.DROIT_API.contains(DroitApi.ADMINISTRER)
                    .or(
                        PEI.MAINTENANCE_DECI_ID.eq(organismeId)
                            .or(PEI.SERVICE_PUBLIC_DECI_ID.eq(organismeId))
                            .or(PIBI.SERVICE_EAU_ID.eq(organismeId)),
                    ),
            )
            .and(filterPeiNumeroComplet?.let { listeNumeroPei.contains(filterPeiNumeroComplet) } ?: DSL.noCondition())
            .and(
                statutIndisponibiliteTemporaireEnum?.let { statut ->
                    val now = dateUtils.now()
                    when (statut) {
                        StatutIndisponibiliteTemporaireEnum.PLANIFIEE ->
                            INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.gt(now)
                        StatutIndisponibiliteTemporaireEnum.EN_COURS ->
                            INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(now)
                                .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(now).or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull))

                        StatutIndisponibiliteTemporaireEnum.TERMINEE ->
                            INDISPONIBILITE_TEMPORAIRE.DATE_FIN.lt(now)

                        StatutIndisponibiliteTemporaireEnum.EN_COURS_PLANIFIEE ->
                            INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.gt(now)
                                .or(
                                    INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(now)
                                        .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(now).or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull)),
                                )
                    }
                } ?: DSL.noCondition(),
            )
            .groupBy(INDISPONIBILITE_TEMPORAIRE.ID, listeNumeroPei)
            .fetchInto()
    }
}
