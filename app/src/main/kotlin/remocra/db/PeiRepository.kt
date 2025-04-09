package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.InsertSetStep
import org.jooq.Record
import org.jooq.Record16
import org.jooq.SelectForUpdateStep
import org.jooq.SortField
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.Params
import remocra.data.PeiData
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.Pei
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.DOMAINE
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.MARQUE_PIBI
import remocra.db.jooq.remocra.tables.references.MATERIAU
import remocra.db.jooq.remocra.tables.references.MODELE_PIBI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.SITE
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.TYPE_CANALISATION
import remocra.db.jooq.remocra.tables.references.TYPE_RESEAU
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.db.jooq.remocra.tables.references.V_PEI_VISITE_DATE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.AdresseDecorator
import remocra.utils.DateUtils
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class PeiRepository
@Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {
    companion object {

        // Alias de table
        val autoriteDeciAlias: Table<*> = ORGANISME.`as`("AUTORITE_DECI")
        val servicePublicDeciAlias: Table<*> = ORGANISME.`as`("SP_DECI")

        /**
         * L'utilisation du [AdresseDecorator] est impossible avec le filter + count, on redécoupe donc, mais ça doit rester en phase !
         * @see AdresseDecorator.decorateAdresse
         */
        val adresseField = DSL.concat(
            DSL.`when`(PEI.EN_FACE.isTrue, AdresseDecorator.FACE_A + " ").otherwise(""),
            DSL.`when`(PEI.NUMERO_VOIE.isNotNull, DSL.concat(PEI.NUMERO_VOIE, " ")).otherwise(""),
            DSL.`when`(PEI.SUFFIXE_VOIE.isNotNull, DSL.concat(PEI.SUFFIXE_VOIE, " ")).otherwise(""),
            DSL.`when`(PEI.VOIE_ID.isNotNull, DSL.concat(VOIE.LIBELLE, " ")).otherwise(PEI.VOIE_TEXTE),
        )

        val peiData = listOf(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            PEI.NUMERO_INTERNE,
            PEI.DISPONIBILITE_TERRESTRE,
            PEI.TYPE_PEI,
            PEI.GEOMETRIE,
            PEI.COMMUNE_ID,
            PEI.VOIE_ID,
            PEI.VOIE_TEXTE,
            PEI.NUMERO_VOIE,
            PEI.SUFFIXE_VOIE,
            PEI.LIEU_DIT_ID,
            PEI.CROISEMENT_ID,
            PEI.COMPLEMENT_ADRESSE,
            PEI.EN_FACE,
            PEI.DOMAINE_ID,
            PEI.NATURE_ID,
            PEI.SITE_ID,
            PEI.GESTIONNAIRE_ID,
            PEI.NATURE_DECI_ID,
            PEI.ZONE_SPECIALE_ID,
            PEI.ANNEE_FABRICATION,
            PEI.AUTORITE_DECI_ID,
            PEI.SERVICE_PUBLIC_DECI_ID,
            PEI.MAINTENANCE_DECI_ID,
            PEI.NIVEAU_ID,
            PEI.OBSERVATION,
        )
    }

    enum class PageFilter {
        INDISPONIBILITE_TEMPORAIRE,
        TOURNEE,
        LISTE_PEI,
        PEI_LONGUE_INDISPO,
    }

    fun getPeiWithFilterByIndisponibiliteTemporaire(param: Params<Filter, Sort>, idIndisponibiliteTemporaire: UUID, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): List<PeiForTableau> {
        param.filterBy?.idIndisponibiliteTemporaire = idIndisponibiliteTemporaire
        return getAllWithFilterAndConditionalJoin(param, zoneCompetenceId, PageFilter.INDISPONIBILITE_TEMPORAIRE, isSuperAdmin).fetchInto()
    }

    // Pour les PEI indisponibles depuis trop longtemps
    fun getPeiWithFilterByMessageAlerte(param: Params<Filter, Sort>, listePeiId: Set<UUID>, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): List<PeiForTableau> {
        param.filterBy?.listePeiId = listePeiId
        return getAllWithFilterAndConditionalJoin(
            param,
            zoneCompetenceId,
            PageFilter.PEI_LONGUE_INDISPO,
            isSuperAdmin,
        )
            .fetchInto()
    }

    fun countAllPeiWithFilterByMessageAlerte(filterBy: Filter?, listePeiId: Set<UUID>, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): Int {
        filterBy?.listePeiId = listePeiId
        return countAllPeiWithFilter(filterBy, zoneCompetenceId, isSuperAdmin, PageFilter.PEI_LONGUE_INDISPO)
    }

    fun countAllPeiWithFilterByIndisponibiliteTemporaire(filterBy: Filter?, idIndisponibiliteTemporaire: UUID, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): Int {
        filterBy?.idIndisponibiliteTemporaire = idIndisponibiliteTemporaire
        return countAllPeiWithFilter(filterBy, zoneCompetenceId, isSuperAdmin, PageFilter.INDISPONIBILITE_TEMPORAIRE)
    }

    fun getPeiWithFilterByTournee(param: Params<Filter, Sort>, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): List<PeiForTableau> {
        return getAllWithFilterAndConditionalJoin(param, zoneCompetenceId, PageFilter.TOURNEE, isSuperAdmin).fetchInto()
    }

    // Très peu de données donc peu d'impact d'utiliser le count jooq plutôt que la primitive SQL
    fun countAllPeiWithFilterByTournee(filterBy: Filter?, idTournee: UUID, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): Int {
        filterBy?.idTournee = idTournee
        return countAllPeiWithFilter(filterBy, zoneCompetenceId, isSuperAdmin, PageFilter.TOURNEE)
    }

    fun getPeiWithFilter(param: Params<Filter, Sort>, zoneCompetenceId: UUID?, isSuperAdmin: Boolean): List<PeiForTableau> =
        getAllWithFilterAndConditionalJoin(param, zoneCompetenceId, PageFilter.LISTE_PEI, isSuperAdmin)
            .fetch().map { record ->
                PeiForTableau(
                    peiId = record.component1()!!,
                    peiNumeroComplet = record.component2()!!,
                    peiNumeroInterne = record.component3()!!,
                    peiTypePei = record.component4()!!,
                    peiDisponibiliteTerrestre = record.component5(),
                    penaDisponibiliteHbe = record.component6(),
                    natureLibelle = record.component7()!!,
                    adresse = record.component8(),
                    communeLibelle = record.component9()!!,
                    natureDeciLibelle = record.component10()!!,
                    autoriteDeci = record.component11()!!,
                    servicePublicDeci = record.component12()!!,
                    listeAnomalie = record.component13()!!,
                    tourneeLibelle = record.component14()!!,
                    peiNextRop = record.component15(),
                    peiNextCtp = record.component16(),
                )
            }

    fun countAllPeiWithFilter(filterBy: Filter?, zoneCompetenceId: UUID?, isSuperAdmin: Boolean, pageFilter: PageFilter = PageFilter.LISTE_PEI): Int =
        dsl.selectDistinct(PEI.ID)
            .from(PEI)
            .join(NATURE)
            .on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(PENA)
            .on(PEI.ID.eq(PENA.ID))
            .leftJoin(L_PEI_ANOMALIE)
            .on(L_PEI_ANOMALIE.PEI_ID.eq(PEI.ID))
            .leftJoin(ANOMALIE)
            .on(ANOMALIE.ID.eq(L_PEI_ANOMALIE.ANOMALIE_ID)).and(ANOMALIE.ID.eq(L_PEI_ANOMALIE.ANOMALIE_ID))
            .leftJoin(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
            .leftJoin(TOURNEE)
            .on(TOURNEE.ID.eq(L_TOURNEE_PEI.TOURNEE_ID))
            .leftJoin(V_PEI_VISITE_DATE)
            .on(V_PEI_VISITE_DATE.PEI_ID.eq(PEI.ID))
            .leftJoin(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .let {
                when (pageFilter) {
                    PageFilter.INDISPONIBILITE_TEMPORAIRE -> it.leftJoin(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                        .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))

                    // la tournée est déjà jointe pour afficher le libelle
                    PageFilter.TOURNEE -> it

                    // Si on vient de la page des pei pas de join supplémentaire
                    PageFilter.LISTE_PEI -> it

                    PageFilter.PEI_LONGUE_INDISPO -> it
                }
            }
            .where(filterBy?.toCondition(dateUtils) ?: DSL.noCondition())
            // Et la zone de compétence de l'utilisateur s'il n'est pas super admin
            .and(repositoryUtils.checkIsSuperAdminOrCondition(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue, isSuperAdmin))
            .count()

    fun isInZoneCompetence(
        geometry: Field<Geometry?>,
        idOrganisme: UUID,
    ): Boolean =
        dsl.fetchExists(
            dsl.select().from(PEI)
                .join(ORGANISME)
                .on(ORGANISME.ID.eq(idOrganisme))
                .join(ZONE_INTEGRATION)
                .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
                .where(
                    ST_Within(
                        ST_Transform(geometry, SRID),
                        ZONE_INTEGRATION.GEOMETRIE,
                    ),
                ).and(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)),
        )

    private fun getAllWithFilterAndConditionalJoin(
        param: Params<Filter, Sort>,
        zoneCompetenceId: UUID?,
        pageFilter: PageFilter = PageFilter.LISTE_PEI,
        isSuperAdmin: Boolean,
    ): SelectForUpdateStep<Record16<UUID?, String?, Int?, TypePei?, Disponibilite?, Disponibilite?, String?, String, String?, String?, String?, String?, MutableList<UUID>, String?, ZonedDateTime?, ZonedDateTime?>> {
        return dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            PEI.NUMERO_INTERNE,
            PEI.TYPE_PEI,
            PEI.DISPONIBILITE_TERRESTRE,
            PENA.DISPONIBILITE_HBE,
            NATURE.LIBELLE,
            adresseField.`as`("adresse"),
            COMMUNE.LIBELLE,
            NATURE_DECI.LIBELLE,
            autoriteDeciAlias.field(ORGANISME.LIBELLE)?.`as`("AUTORITE_DECI"),
            servicePublicDeciAlias.field(ORGANISME.LIBELLE)?.`as`("SERVICE_PUBLIC_DECI"),
                /*
                 * le multiset permet de renvoyer une liste dans une liste
                 * */
            multiset(
                selectDistinct(L_PEI_ANOMALIE.ANOMALIE_ID)
                    .from(L_PEI_ANOMALIE)
                    .where(L_PEI_ANOMALIE.PEI_ID.eq(PEI.ID)),
            ).`as`("listeAnomalie").convertFrom { record ->
                record?.map { r ->
                    r.value1().let { it as UUID }
                }
            },
            multiset(
                selectDistinct(TOURNEE.LIBELLE)
                    .from(TOURNEE)
                    .join(L_TOURNEE_PEI)
                    .on(L_TOURNEE_PEI.TOURNEE_ID.eq(TOURNEE.ID))
                    .where(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID)),
            ).`as`("listeTournee").convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }?.joinToString()
            }.`as`("tourneeLibelle"),
            V_PEI_VISITE_DATE.PEI_NEXT_ROP,
            V_PEI_VISITE_DATE.PEI_NEXT_CTP,
        )
            .from(PEI)
            .join(COMMUNE)
            .on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .join(NATURE)
            .on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(PENA)
            .on(PEI.ID.eq(PENA.ID))
            .leftJoin(PIBI)
            .on(PEI.ID.eq(PIBI.ID))
            .join(NATURE_DECI)
            .on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .leftJoin(autoriteDeciAlias)
            .on(PEI.AUTORITE_DECI_ID.eq(autoriteDeciAlias.field(ORGANISME.ID)))
            .leftJoin(servicePublicDeciAlias)
            .on(PEI.SERVICE_PUBLIC_DECI_ID.eq(servicePublicDeciAlias.field(ORGANISME.ID)))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
                /*
            Join des anomalies uniquement pour les filtres c'est pour cette raison qu'on ne prend pas de field
            de cette jointure
                 */
            .leftJoin(L_PEI_ANOMALIE)
            .on(L_PEI_ANOMALIE.PEI_ID.eq(PEI.ID))
            .leftJoin(ANOMALIE)
            .on(ANOMALIE.ID.eq(L_PEI_ANOMALIE.ANOMALIE_ID)).and(ANOMALIE.ID.eq(L_PEI_ANOMALIE.ANOMALIE_ID))
            .leftJoin(V_PEI_VISITE_DATE)
            .on(V_PEI_VISITE_DATE.PEI_ID.eq(PEI.ID))
            .leftJoin(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
            .leftJoin(TOURNEE)
            .on(TOURNEE.ID.eq(L_TOURNEE_PEI.TOURNEE_ID))
            .leftJoin(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
            .let {
                // Join conditionnel en fonction de la page qui demande (exemple les indispos temporaires, on n'en a besoin QUE
                // pour les indispos temporaires)
                when (pageFilter) {
                    PageFilter.INDISPONIBILITE_TEMPORAIRE -> it.leftJoin(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                        .on(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))

                    // la tournée est déjà jointe pour afficher le libelle
                    PageFilter.TOURNEE -> it

                    // Si on vient de la page des pei pas de join supplémentaire
                    PageFilter.LISTE_PEI -> it

                    // Pour les PEI indisponibles depuis trop longtemps
                    PageFilter.PEI_LONGUE_INDISPO -> it
                }
            }
            .where(param.filterBy?.toCondition(dateUtils) ?: DSL.noCondition())
            .and(repositoryUtils.checkIsSuperAdminOrCondition(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue, isSuperAdmin))
            .groupBy(
                PEI.ID,
                PEI.NUMERO_COMPLET,
                PEI.NUMERO_INTERNE,
                PEI.TYPE_PEI,
                PEI.DISPONIBILITE_TERRESTRE,
                PENA.DISPONIBILITE_HBE,
                NATURE.LIBELLE,
                adresseField.`as`("adresse"),
                COMMUNE.LIBELLE,
                NATURE_DECI.LIBELLE,
                autoriteDeciAlias.field(ORGANISME.LIBELLE)?.`as`("AUTORITE_DECI"),
                servicePublicDeciAlias.field(ORGANISME.LIBELLE)?.`as`("SERVICE_PUBLIC_DECI"),
                V_PEI_VISITE_DATE.PEI_NEXT_ROP,
                V_PEI_VISITE_DATE.PEI_NEXT_CTP,
            )
            .orderBy(
                param.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(
                    DSL.length(PEI.NUMERO_COMPLET).asc(),
                    PEI.NUMERO_COMPLET.asc(),
                ),
            )
            .limit(param.limit)
            .offset(param.offset)
    }

    data class PeiForTableau(
        val peiId: UUID,
        val peiNumeroComplet: String,
        val peiNumeroInterne: Int,
        val peiTypePei: TypePei,
        val peiDisponibiliteTerrestre: Disponibilite?,
        val penaDisponibiliteHbe: Disponibilite?,
        val natureLibelle: String,
        val communeLibelle: String,
        val adresse: String?,
        val natureDeciLibelle: String,
        val autoriteDeci: String?,
        val servicePublicDeci: String?,
        val listeAnomalie: List<UUID>?,
        val peiNextRop: ZonedDateTime?,
        val peiNextCtp: ZonedDateTime?,
        val tourneeLibelle: String?,
    )

    data class Filter(
        val peiNumeroComplet: String?,
        val peiNumeroInterne: Int?,
        val communeId: UUID?,
        val typePei: TypePei?,
        val natureDeci: UUID?,
        val natureId: UUID?,
        val autoriteDeci: UUID?,
        val servicePublicDeci: UUID?,
        val peiDisponibiliteTerrestre: Disponibilite?,
        val penaDisponibiliteHbe: Disponibilite?,
        val listeAnomalie: String?,
        var idIndisponibiliteTemporaire: UUID?,
        var idTournee: UUID?,
        val tourneeLibelle: String?,
        var listePeiId: Set<UUID>?,
        var adresse: String?,
        val prochaineDateRop: ProchaineDate?,
        val prochaineDateCtp: ProchaineDate?,
    ) {

        enum class ProchaineDate {
            DATE_PASSEE,
            INFERIEUR_1_MOIS,
            INFERIEUR_2_MOIS,
            INFERIEUR_6_MOIS,
            INFERIEUR_12_MOIS,
            INFERIEUR_24_MOIS,
        }

        fun toCondition(dateUtils: DateUtils): Condition =
            DSL.and(
                listOfNotNull(
                    peiNumeroComplet?.let { DSL.and(PEI.NUMERO_COMPLET.containsIgnoreCaseUnaccent(it)) },
                    peiNumeroInterne?.let { DSL.and(PEI.NUMERO_INTERNE.contains(it)) },
                    communeId?.let { DSL.and(PEI.COMMUNE_ID.eq(it)) },
                    typePei?.let { DSL.and(PEI.TYPE_PEI.eq(it)) },
                    natureDeci?.let { DSL.and(PEI.NATURE_DECI_ID.eq(it)) },
                    natureId?.let { DSL.and(PEI.NATURE_ID.eq(it)) },
                    autoriteDeci?.let { DSL.and(PEI.AUTORITE_DECI_ID.eq(it)) },
                    servicePublicDeci?.let { DSL.and(PEI.SERVICE_PUBLIC_DECI_ID.eq(it)) },
                    peiDisponibiliteTerrestre?.let { DSL.and(PEI.DISPONIBILITE_TERRESTRE.eq(it)) },
                    penaDisponibiliteHbe?.let { DSL.and(PENA.DISPONIBILITE_HBE.eq(it)) },
                    listeAnomalie?.let { DSL.and(ANOMALIE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    idIndisponibiliteTemporaire?.let {
                        DSL.and(
                            L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID.eq(
                                it,
                            ),
                        )
                    },
                    idTournee?.let { DSL.and(L_TOURNEE_PEI.TOURNEE_ID.eq(it)) },
                    listePeiId?.let { DSL.and(PEI.ID.`in`(it)) },
                    prochaineDateRop?.let {
                        when (it) {
                            ProchaineDate.DATE_PASSEE -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_ROP.le(dateUtils.now()))
                            ProchaineDate.INFERIEUR_1_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_ROP.between(dateUtils.now(), dateUtils.now().minusMonths(1)))
                            ProchaineDate.INFERIEUR_2_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_ROP.between(dateUtils.now(), dateUtils.now().minusMonths(2)))
                            ProchaineDate.INFERIEUR_6_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_ROP.between(dateUtils.now(), dateUtils.now().minusMonths(6)))
                            ProchaineDate.INFERIEUR_12_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_ROP.between(dateUtils.now(), dateUtils.now().minusMonths(12)))
                            ProchaineDate.INFERIEUR_24_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_ROP.between(dateUtils.now(), dateUtils.now().minusMonths(24)))
                        }
                    },
                    prochaineDateCtp?.let {
                        when (it) {
                            ProchaineDate.DATE_PASSEE -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_CTP.le(dateUtils.now()))
                            ProchaineDate.INFERIEUR_1_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_CTP.between(dateUtils.now(), dateUtils.now().minusMonths(1)))
                            ProchaineDate.INFERIEUR_2_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_CTP.between(dateUtils.now(), dateUtils.now().minusMonths(2)))
                            ProchaineDate.INFERIEUR_6_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_CTP.between(dateUtils.now(), dateUtils.now().minusMonths(6)))
                            ProchaineDate.INFERIEUR_12_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_CTP.between(dateUtils.now(), dateUtils.now().minusMonths(12)))
                            ProchaineDate.INFERIEUR_24_MOIS -> DSL.and(V_PEI_VISITE_DATE.PEI_NEXT_CTP.between(dateUtils.now(), dateUtils.now().minusMonths(24)))
                        }
                    },
                    adresse?.let {
                        DSL.and(adresseField.containsIgnoreCaseUnaccent(it))
                    },
                ),
            )
    }

    data class Sort(
        val peiNumeroComplet: Int?,
        val peiNumeroInterne: Int?,
        val peiTypePei: Int?,
        val peiDisponibilite: Int?,
        val penaDisponibiliteHBE: Int?,
        val natureLibelle: Int?,
        val communeLibelle: Int?,
        val natureDeciLibelle: Int?,
        val autoriteDeci: Int?,
        val servicePublicDeci: Int?,
        val peiNextRop: Int?,
        val peiNextCtp: Int?,
        val tourneeLibelle: Int?,
        var ordreTournee: Int?,

    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            PEI.NUMERO_INTERNE.getSortField(peiNumeroInterne),
            PEI.TYPE_PEI.getSortField(peiTypePei),
            PEI.DISPONIBILITE_TERRESTRE.getSortField(peiDisponibilite),
            PENA.DISPONIBILITE_HBE.getSortField(penaDisponibiliteHBE),
            NATURE.LIBELLE.getSortField(natureLibelle),
            COMMUNE.LIBELLE.getSortField(communeLibelle),
            NATURE_DECI.LIBELLE.getSortField(natureDeciLibelle),
            autoriteDeciAlias.field(ORGANISME.LIBELLE)?.getSortField(autoriteDeci),
            servicePublicDeciAlias.field(ORGANISME.LIBELLE)?.getSortField(servicePublicDeci),
            /* NUMERO_COMPLET est un string il faut donc ordonner par la longueur
               pour avoir un ordre "numérique"
             */
            DSL.length(PEI.NUMERO_COMPLET).getSortField(peiNumeroComplet),
            PEI.NUMERO_COMPLET.getSortField(peiNumeroComplet),
            V_PEI_VISITE_DATE.PEI_NEXT_ROP.getSortField(peiNextRop),
            V_PEI_VISITE_DATE.PEI_NEXT_CTP.getSortField(peiNextCtp),
        )
    }

    fun getTypePei(idPei: UUID): TypePei =
        dsl.select(PEI.TYPE_PEI)
            .from(PEI)
            .where(PEI.ID.eq(idPei))
            .fetchSingleInto()

    fun getNumeroCompletPei(idPei: UUID): String =
        dsl.select(PEI.NUMERO_COMPLET)
            .from(PEI)
            .where(PEI.ID.eq(idPei))
            .fetchSingleInto()

    fun getCommune(idPei: UUID): UUID =
        dsl.select(PEI.COMMUNE_ID)
            .from(PEI)
            .where(PEI.ID.eq(idPei))
            .fetchSingleInto()

    fun getInfoPei(peiId: UUID): PeiData =
        dsl.select(peiData)
            .from(PEI)
            .where(PEI.ID.eq(peiId))
            .fetchSingleInto()

    fun getInfoPei(): List<PeiData> =
        dsl.select(peiData)
            .from(PEI)
            .fetchInto()

    fun upsert(pei: PeiData) =
        dsl.insertInto(PEI).setPeiField(pei)

    /**
     * Permet d'insérer ou d'update les champs d'un PEI.
     * Le jour où un champ est ajouté, il suffira de mettre à jour cette fonction.
     */
    private fun <R : Record?> InsertSetStep<R>.setPeiField(pei: PeiData): Int {
        // On crée le record, si le PEI existe alors on met à jour ces champs
        val record = dsl.newRecord(
            PEI,
            Pei(
                peiId = pei.peiId,
                peiTypePei = pei.peiTypePei,
                peiGestionnaireId = pei.peiGestionnaireId.takeIf { pei.peiSiteId == null },
                peiNumeroInterne = pei.peiNumeroInterne!!,
                peiCommuneId = pei.peiCommuneId,
                peiDomaineId = pei.peiDomaineId,
                peiGeometrie = pei.peiGeometrie,
                peiEnFace = pei.peiEnFace,
                peiSiteId = pei.peiSiteId,
                peiCroisementId = pei.peiCroisementId,
                peiObservation = pei.peiObservation,
                peiVoieId = pei.peiVoieId,
                peiVoieTexte = pei.peiVoieTexte,
                peiNatureId = pei.peiNatureId,
                peiNiveauId = pei.peiNiveauId,
                peiAnneeFabrication = pei.peiAnneeFabrication,
                peiAutoriteDeciId = pei.peiAutoriteDeciId,
                peiZoneSpecialeId = pei.peiZoneSpecialeId,
                peiNumeroComplet = pei.peiNumeroComplet!!,
                peiComplementAdresse = pei.peiComplementAdresse,
                peiMaintenanceDeciId = pei.peiMaintenanceDeciId,
                peiLieuDitId = pei.peiLieuDitId,
                peiNumeroVoie = pei.peiNumeroVoie,
                peiSuffixeVoie = pei.peiSuffixeVoie,
                peiNatureDeciId = pei.peiNatureDeciId,
                peiDisponibiliteTerrestre = pei.peiDisponibiliteTerrestre,
                peiServicePublicDeciId = pei.peiServicePublicDeciId,
            ),
        )

        return set(record).onConflict(PEI.ID)
            .doUpdate()
            .set(record)
            .execute()
    }

    /**
     * Indique si le PEI spécifié existe bien en base
     *
     * @param numero Le numéro du PEI
     */
    fun peiExist(numero: String?): Boolean {
        return dsl.fetchExists(
            dsl.select(PEI.ID).from(PEI).where(PEI.NUMERO_COMPLET.equalIgnoreCase(numero)),
        )
    }

    fun getPeiIdFromNumero(numero: String): UUID? =
        dsl.select(PEI.ID).from(PEI).where(PEI.NUMERO_COMPLET.equalIgnoreCase(numero)).fetchOneInto()

    fun getPeiFromNumero(numero: String): Pei? =
        dsl.selectFrom(PEI).where(PEI.NUMERO_COMPLET.equalIgnoreCase(numero)).fetchOneInto()

    @Suppress("UNCHECKED_CAST")
    fun <T : ApiPeiData> getPeiCaracteristiques(numero: String): T {
        val idTypePei: IdTypePei =
            dsl
                .select(PEI.ID, PEI.TYPE_PEI)
                .from(PEI)
                .where(PEI.NUMERO_COMPLET.equalIgnoreCase(numero))
                .fetchSingleInto()

        return if (TypePei.PIBI == idTypePei.peiTypePei) {
            getPibiCaracteristiques(idTypePei.peiId) as T
        } else {
            getPenaCaracteristiques(idTypePei.peiId) as T
        }
    }

    private data class IdTypePei(val peiId: UUID, val peiTypePei: TypePei)

    private fun getPibiCaracteristiques(id: UUID): ApiPibiData {
        return dsl.select(
            PEI.ID.`as`("id"),
            PEI.NUMERO_COMPLET.`as`("numeroComplet"),
            PEI.NUMERO_INTERNE.`as`("numeroInterne"),
            PEI.ANNEE_FABRICATION.`as`("anneeFabrication"),
        )
            .select(
                PIBI.DIAMETRE_CANALISATION.`as`("diametreCanalisation"),
                PIBI.NUMERO_SCP.`as`("numeroScp"),
                PIBI.RENVERSABLE.`as`("renversable"),
                PIBI.DISPOSITIF_INVIOLABILITE.`as`("dispositifInviolabilite"),
                PIBI.DEBIT_RENFORCE.`as`("debitRenforce"),
                PIBI.SURPRESSE.`as`("surpresse"),
                PIBI.ADDITIVE.`as`("additive"),
            )
            .select(COMMUNE.CODE_INSEE).select(DOMAINE.CODE).select(NATURE.CODE).select(NATURE_DECI.CODE)
            .select(DIAMETRE.CODE).select(MODELE_PIBI.CODE)
            .select(TYPE_CANALISATION.CODE)
            .select(MARQUE_PIBI.CODE)
            .select(TYPE_RESEAU.CODE)
            .select(ORGANISME.CODE.`as`("serviceEauCode"))
            .select(PEI.`as`("PEI_JUMELE").NUMERO_COMPLET.`as`("pibiJumele"))
            .from(PEI)
            .innerJoin(PIBI).on(PEI.ID.eq(PIBI.ID))
            // jointures PEI
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .join(DOMAINE).on(PEI.DOMAINE_ID.eq(DOMAINE.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            // jointures PIBI
            .leftJoin(DIAMETRE).on(PIBI.DIAMETRE_ID.eq(DIAMETRE.ID))
            .leftJoin(ORGANISME).on(PIBI.SERVICE_EAU_ID.eq(ORGANISME.ID))
            .leftJoin(MODELE_PIBI).on(PIBI.MODELE_PIBI_ID.eq(MODELE_PIBI.ID))
            .leftJoin(MARQUE_PIBI).on(MARQUE_PIBI.ID.eq(PIBI.MARQUE_PIBI_ID))
            .leftJoin(TYPE_CANALISATION).on(PIBI.TYPE_CANALISATION_ID.eq(TYPE_CANALISATION.ID))
            .leftJoin(TYPE_RESEAU).on(PIBI.TYPE_RESEAU_ID.eq(TYPE_RESEAU.ID))
            // Jointure pour le PIBI jumelé
            .leftJoin(PIBI.`as`("PIBI_JUMELE")).on(PIBI.`as`("PIBI_JUMELE").ID.eq(PIBI.JUMELE_ID))
            .leftJoin(PEI.`as`("PEI_JUMELE")).on(PEI.`as`("PEI_JUMELE").ID.eq(PIBI.`as`("PIBI_JUMELE").ID))
            .where(PEI.ID.eq(id))
            .fetchSingleInto<ApiPibiData>()
    }

    private fun getPenaCaracteristiques(id: UUID): ApiPenaData =
        dsl.select(
            PEI.ID.`as`("id"),
            PEI.NUMERO_COMPLET.`as`("numeroComplet"),
            PEI.NUMERO_INTERNE.`as`("numeroInterne"),
            PEI.ANNEE_FABRICATION,
            PENA.CAPACITE.`as`("capacite"),
            PENA.CAPACITE_ILLIMITEE.`as`("capaciteIllimite"),
            PENA.QUANTITE_APPOINT.`as`("quantiteAppoint"),
            PENA.DISPONIBILITE_HBE.`as`("disponibiliteHbe"),
            MATERIAU.CODE,
        )
            .select(COMMUNE.CODE_INSEE).select(DOMAINE.CODE).select(NATURE.CODE).select(NATURE_DECI.CODE)
            .from(PEI).innerJoin(PENA).on(PEI.ID.eq(PENA.ID))
            .leftJoin(MATERIAU).on(MATERIAU.ID.eq(PENA.MATERIAU_ID))
            // jointures PEI
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .join(DOMAINE).on(PEI.DOMAINE_ID.eq(DOMAINE.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .where(PEI.ID.eq(id))
            .fetchSingleInto<ApiPenaData>()

    fun getPeiAccessibility(listPei: Set<UUID>): List<ApiPeiAccessibility> =
        dsl
            .select(
                PEI.ID,
                PEI.NUMERO_COMPLET,
                PEI.MAINTENANCE_DECI_ID,
                PEI.SERVICE_PUBLIC_DECI_ID,
                PIBI.SERVICE_EAU_ID,
            )
            .from(PEI)
            .leftJoin(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .where(if (listPei.isEmpty()) DSL.noCondition() else PEI.ID.`in`(listPei))
            .fetchInto()

    fun getListIdNumeroCompletInZoneCompetence(organismeId: UUID?): Collection<IdNumeroComplet> =
        dsl.select(PEI.ID, PEI.NUMERO_COMPLET)
            .from(PEI)
            .join(ORGANISME).on(ORGANISME.ID.eq(organismeId))
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
            .where(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .fetchInto()

    fun deleteById(peiId: UUID) = dsl.deleteFrom(PEI).where(PEI.ID.eq(peiId)).execute()

    fun getGeometriePei(peiId: UUID): Point =
        dsl.select(PEI.GEOMETRIE)
            .from(PEI)
            .where(PEI.ID.eq(peiId))
            .fetchSingleInto()

    fun getGeometriesPei(listPeiId: Collection<UUID>): Collection<Point> =
        dsl.select(PEI.GEOMETRIE)
            .from(PEI)
            .where(PEI.ID.`in`(listPeiId))
            .fetchInto()

    fun getSiteId(listePeiId: Collection<UUID>): UUID? =
        dsl.select(PEI.SITE_ID)
            .from(PEI)
            .where(PEI.ID.`in`(listePeiId))
            .and(PEI.SITE_ID.isNotNull)
            .limit(1)
            .fetchOneInto()

    fun checkExists(peiId: UUID) =
        dsl.fetchExists(
            dsl.select(PEI.ID)
                .from(PEI)
                .where(PEI.ID.eq(peiId)),
        )

    /**
     * Vérifie que la cohérence du triplet (id, numéroInterne, codeInsee) pour le PEI
     *
     * @param id UUID
     * @param numeroInterne int
     * @param codeInsee String
     * @return true si le résultat est cohérent, false sinon
     */
    fun checkForImportCTP(id: UUID, numeroInterne: Int, codeInsee: String): Boolean {
        return dsl.fetchExists(
            dsl
                .select(PEI.ID)
                .from(PEI)
                .join(COMMUNE)
                .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
                .where(COMMUNE.CODE_INSEE.eq(codeInsee))
                .and(PEI.NUMERO_INTERNE.eq(numeroInterne))
                .and(PEI.ID.eq(id)),
        )
    }

    fun exportCTP(communeId: UUID?, organismeId: UUID?): List<ExportCTPData> =
        dsl.select(
            PEI.ID,
            COMMUNE.LIBELLE,
            COMMUNE.CODE_INSEE,
            PEI.NUMERO_INTERNE,
            PEI.EN_FACE,
            PEI.NUMERO_VOIE,
            PEI.SUFFIXE_VOIE,
            VOIE.LIBELLE,
            PEI.VOIE_TEXTE,
            DSL.field("St_Y(St_transform(${PEI.GEOMETRIE}, ${GlobalConstants.SRID_4326}))").`as`("peiLatitude"),
            DSL.field("St_X(St_transform(${PEI.GEOMETRIE}, ${GlobalConstants.SRID_4326}))").`as`("peiLongitude"),
            NATURE_DECI.LIBELLE,
            NATURE.LIBELLE,
            DIAMETRE.LIBELLE,
        )
            .from(PEI)
            .join(PIBI).on(PEI.ID.eq(PIBI.ID))
            .leftJoin(DIAMETRE).on(PIBI.DIAMETRE_ID.eq(DIAMETRE.ID))
            .join(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .join(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .leftJoin(ORGANISME).on(ORGANISME.ID.eq(organismeId))
            .leftJoin(ZONE_INTEGRATION).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .where(
                if (communeId != null) {
                    COMMUNE.ID.eq(communeId)
                } else
                    DSL.noCondition(),
            ).and(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .orderBy(COMMUNE.LIBELLE.asc(), PEI.NUMERO_INTERNE.asc())
            .fetchInto()

    data class ExportCTPData(
        val peiId: UUID,
        val communeLibelle: String,
        val communeCodeInsee: String,
        val peiNumeroInterne: Int,
        val peiEnFace: Boolean = false,
        val peiNumeroVoie: Int?,
        val peiSuffixeVoie: String?,
        val voieLibelle: String?,
        val peiVoieTexte: String?,
        val peiLatitude: Double,
        val peiLongitude: Double,
        val natureDeciLibelle: String,
        val natureLibelle: String,
        val diametreLibelle: String?,
    )

    fun getNatureDeciId(listePeiId: Set<UUID>): List<UUID> =
        dsl.selectDistinct(PEI.NATURE_DECI_ID)
            .from(PEI)
            .where(PEI.ID.`in`(listePeiId))
            .fetchInto()

    private fun getListPeiForApiRequete() =
        dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            PEI.GEOMETRIE,
            PEI.NUMERO_INTERNE,
            PEI.DISPONIBILITE_TERRESTRE,
            PEI.NUMERO_VOIE,
            PEI.VOIE_TEXTE,
            PEI.SUFFIXE_VOIE,
            PEI.EN_FACE,
            PEI.COMPLEMENT_ADRESSE,
            NATURE.CODE,
            NATURE.LIBELLE,
            NATURE_DECI.CODE,
            NATURE_DECI.LIBELLE,
            COMMUNE.LIBELLE,
            COMMUNE.CODE_INSEE,
            ORGANISME.LIBELLE.`as`("serviceEauLibelle"),
            DIAMETRE.CODE,
            PIBI.DIAMETRE_CANALISATION,
            SITE.LIBELLE,
            PENA.CAPACITE,
            PENA.DISPONIBILITE_HBE,
            V_PEI_VISITE_DATE.LAST_CTP,
            V_PEI_VISITE_DATE.LAST_ROP,
            V_PEI_VISITE_DATE.LAST_RECEPTION,
            V_PEI_VISITE_DATE.LAST_RECO_INIT,
        )
            .from(PEI)
            .leftJoin(V_PEI_VISITE_DATE)
            .on(V_PEI_VISITE_DATE.PEI_ID.eq(PEI.ID))
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .join(DOMAINE)
            .on(DOMAINE.ID.eq(PEI.DOMAINE_ID))
            .join(NATURE)
            .on(NATURE.ID.eq(PEI.NATURE_ID))
            .join(NATURE_DECI)
            .on(NATURE_DECI.ID.eq(PEI.NATURE_DECI_ID))
            .leftJoin(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(DIAMETRE)
            .on(DIAMETRE.ID.eq(PIBI.DIAMETRE_ID))
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(PIBI.SERVICE_EAU_ID))
            .leftJoin(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .leftJoin(VOIE)
            .on(VOIE.ID.eq(PEI.VOIE_ID))
            .leftJoin(SITE)
            .on(SITE.ID.eq(PEI.SITE_ID))

    fun getListPeiForApi(
        codeInsee: String?,
        type: TypePei?,
        codeNature: String?,
        codeNatureDECI: String?,
        limit: Int?,
        offset: Int?,
    ): Collection<PeiDataForApi> =
        getListPeiForApiRequete()
            .where(
                DSL.and(
                    listOfNotNull(
                        codeInsee?.let { DSL.and(COMMUNE.CODE_INSEE.contains(it)) },
                        type?.let { DSL.and(PEI.TYPE_PEI.eq(it)) },
                        codeNatureDECI?.let { DSL.and(NATURE_DECI.CODE.eq(it)) },
                        codeNature?.let { DSL.and(NATURE.CODE.eq(it)) },
                    ),

                ),
            )
            .limit(limit)
            .offset(offset)
            .fetchInto()

    fun getPeiIdIndisponibles(zoneCompetenceId: UUID?, isSuperAdmin: Boolean): Collection<UUID> =
        dsl.select(PEI.ID)
            .from(PEI)
            .where(PEI.DISPONIBILITE_TERRESTRE.eq(Disponibilite.INDISPONIBLE))
            .fetchInto()

    fun getPeiForApi(
        peiId: UUID,
    ): PeiDataForApi =
        getListPeiForApiRequete()
            .where(
                peiId?.let {
                    PEI.ID.eq(it)
                },
            )
            .fetchSingleInto()

    fun getIdByNumeroComplet(numeroComplet: String): UUID? =
        dsl.select(PEI.ID)
            .from(PEI)
            .where(PEI.NUMERO_COMPLET.eq(numeroComplet))
            .fetchOneInto()

    fun getIdByNumeroComplet(listNumeroComplet: List<String>): List<UUID> =
        dsl.select(PEI.ID)
            .from(PEI)
            .where(PEI.NUMERO_COMPLET.`in`(listNumeroComplet))
            .fetchInto()

    fun getCaracteristiquesForTooltip() {
    }

    data class PeiDataForApi(
        val peiId: UUID,
        val peiNumeroComplet: String,
        val peiGeometrie: Point,
        val peiNumeroInterne: String,
        val peiDisponibiliteTerrestre: Disponibilite,
        val peiNumeroVoie: String?,
        val peiSuffixeVoie: String?,
        val peiVoieTexte: String?,
        val peiVoieId: UUID?,
        val peiComplementAdresse: String?,
        val peiEnFace: Boolean = false,
        val natureCode: String,
        val natureLibelle: String,
        val natureDeciLibelle: String,
        val communeLibelle: String,
        val communeCodeInsee: String,
        val serviceEauLibelle: String?,
        val diametreCode: String?,
        val pibiDiametreCanalisation: Int?,
        val siteLibelle: String?,
        val penaCapacite: Int?,
        val penaDisponibiliteHbe: Disponibilite?,
        val lastReception: ZonedDateTime?,
        val lastRecoInit: ZonedDateTime?,
        val lastCtp: ZonedDateTime?,
        val lastRop: ZonedDateTime?,
    )

    fun getPeiByZoneIntegrationShortData(userInfo: UserInfo): Collection<PeiShortData> {
        if (userInfo.isSuperAdmin) {
            return dsl.select(PEI.ID, PEI.NUMERO_COMPLET)
                .from(PEI)
                .fetchInto()
        }
        return dsl.select(PEI.ID, PEI.NUMERO_COMPLET)
            .from(PEI)
            .join(ZONE_INTEGRATION)
            .on(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .and(ZONE_INTEGRATION.ID.eq(userInfo.zoneCompetence?.zoneIntegrationId))
            .fetchInto()
    }

    data class PeiShortData(
        val peiId: UUID,
        val peiNumeroComplet: String,
    )
}

data class IdNumeroComplet(
    val peiId: UUID,
    val peiNumeroComplet: String,
)

data class ApiPeiAccessibility(
    val id: UUID,
    val numeroComplet: String,
    val maintenanceDeciId: UUID,
    val servicePublicDeciId: UUID,
    val serviceEauxId: UUID,
)

/**
 * Modèle général de représentation d'un PEI pour utilisation dans le back et le front ; ce modèle est décliné (hérité) pour chaque type (PIBI, PENA) afin de rajouter la sémantique nécessaire à ces spécificités.
 */
open class ApiPeiData {
    lateinit var id: String
    lateinit var numeroComplet: String
    lateinit var numeroInterne: String
    var communeCodeInsee: String? = null
    var domaineCode: String? = null
    var natureCode: String? = null
    var natureDeciCode: String? = null
    var anneeFabrication: Int? = null
}

// TODO vérifier toutes les propriétés de l'ancien type pour garantir le périmètre ISO + documenter les différences
// TODO benchmark entre remontée objets complexes (commune, ...) et simplement le "code" à aller chercher dans un référentiel froid
// faire une option pour permettre les 2 au cas par cas ?
class ApiPibiData : ApiPeiData() {
    var diametreCode: String? = null
    var serviceEauCode: String? = null
    var numeroScp: String? = null
    var renversable: Boolean? = false
    var dispositifInviolabilite: Boolean = false
    var modelePibiCode: String? = null

    var reservoirCode: String? = null
    var debitRenforce: Boolean? = false
    var typeCanalisationCode: String? = null
    var typeReseauCode: String? = null
    var diametreCanalisation: Int? = null
    var surpresse: Boolean? = false
    var additive: Boolean? = false

    var pibiJumele: String? = null
    var marquePibiCode: String? = null
}

class ApiPenaData : ApiPeiData() {
    var disponibiliteHbe: Disponibilite? = null
    var capacite: Int? = null
    var capaciteIllimitee: Boolean? = null
    var materiauCode: String? = null
    var quantiteAppoint: Double? = null
}
