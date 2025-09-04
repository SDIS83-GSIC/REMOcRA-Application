package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import remocra.data.enums.TypeElementCarte
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.remocra.enums.EtatAdresse
import remocra.db.jooq.remocra.enums.EvenementStatutMode
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.references.ADRESSE
import remocra.db.jooq.remocra.tables.references.ADRESSE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ADRESSE_SOUS_TYPE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ADRESSE_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DEBIT_SIMULTANE
import remocra.db.jooq.remocra.tables.references.DEBIT_SIMULTANE_MESURE
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_DEBIT_SIMULTANE_MESURE_PEI
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.OLDEB
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_DEBROUSSAILLEMENT
import remocra.db.jooq.remocra.tables.references.PEI_PRESCRIT
import remocra.db.jooq.remocra.tables.references.PERMIS
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.RCCI
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.DateUtils
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

class CarteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        val hasIndispoTemp = DSL.exists(
            DSL.select(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID).from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID)),
        ).`as`("hasIndispoTemp")

        val hasTournee = DSL.exists(
            DSL.select(L_TOURNEE_PEI.TOURNEE_ID).from(L_TOURNEE_PEI)
                .where(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID)),
        ).`as`("hasTournee")

        val hasTourneeReservee = DSL.exists(
            DSL.select(L_TOURNEE_PEI.TOURNEE_ID).from(L_TOURNEE_PEI)
                .join(TOURNEE)
                .on(TOURNEE.ID.eq(L_TOURNEE_PEI.TOURNEE_ID))
                .where(
                    L_TOURNEE_PEI.PEI_ID.eq(PEI.ID)
                        .and(TOURNEE.RESERVATION_UTILISATEUR_ID.isNotNull),
                ),
        ).`as`("hasTourneeReservee")

        val hasDebitSimultane = DSL.exists(
            DSL.select(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID).from(L_DEBIT_SIMULTANE_MESURE_PEI)
                .where(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID.eq(PEI.ID)),
        ).`as`("hasDebitSimultane")
    }

    /**
     * Récupère les PEI dans une BBOX selon la zone de compétence
     */
    fun getPeiWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<PeiCarte> {
        return dsl.select(
            ST_Transform(PEI.GEOMETRIE, srid).`as`("elementGeometrie"),
            PEI.ID.`as`("elementId"),
            hasIndispoTemp,
            hasTournee,
            hasTourneeReservee,
            hasDebitSimultane,
            NATURE_DECI.CODE,
            PIBI.TYPE_RESEAU_ID,
            PEI.NUMERO_COMPLET,
        )
            .from(PEI)
            .innerJoin(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .innerJoin(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .innerJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    zoneId?.let {
                        ST_Within(
                            PEI.GEOMETRIE,
                            DSL.field(
                                DSL.select(ZONE_INTEGRATION.GEOMETRIE).from(ZONE_INTEGRATION)
                                    .where(ZONE_INTEGRATION.ID.eq(zoneId)),
                            ),
                        ).isTrue
                    } ?: DSL.noCondition(),
                    isSuperAdmin,
                )
                    .and(bbox?.let { ST_Within(ST_Transform(PEI.GEOMETRIE, srid), bbox) }),
            )
            .fetchInto()
    }

    fun getPeiHighlightWithinZoneAndBbox(srid: Int, listePeiId: Set<UUID>?): Collection<PeiHighLightCarte> {
        return dsl.select(
            ST_Transform(PEI.GEOMETRIE, srid).`as`("elementGeometrie"),
            PEI.ID.`as`("elementId"),
        )
            .from(PEI)
            .where(
                PEI.ID.`in`(listePeiId),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI en projet dans une BBOX selon l'étude
     */
    fun getPeiProjetWithinEtudeAndBbox(etudeId: UUID, bbox: Field<Geometry?>, srid: Int): Collection<PeiProjetCarte> {
        return dsl.select(ST_Transform(PEI_PROJET.GEOMETRIE, srid).`as`("elementGeometrie"), PEI_PROJET.ID.`as`("elementId"))
            .from(PEI_PROJET)
            .where(
                PEI_PROJET.ETUDE_ID.eq(etudeId),
            ).and(
                ST_Within(ST_Transform(PEI_PROJET.GEOMETRIE, srid), bbox),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI en projet selon l'étude
     */
    fun getPeiProjetWithinEtude(etudeId: UUID, srid: Int): Collection<PeiProjetCarte> {
        return dsl.select(ST_Transform(PEI_PROJET.GEOMETRIE, srid).`as`("elementGeometrie"), PEI_PROJET.ID.`as`("elementId"))
            .from(PEI_PROJET)
            .where(
                PEI_PROJET.ETUDE_ID.eq(etudeId),
            )
            .fetchInto()
    }

    fun getPeiPrescritWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<PeiPrescritsCarte> {
        return dsl.select(
            ST_Transform(PEI_PRESCRIT.GEOMETRIE, srid).`as`("elementGeometrie"),
            PEI_PRESCRIT.ID.`as`("elementId"),
            PEI_PRESCRIT.NUM_DOSSIER,
            PEI_PRESCRIT.DEBIT,
            PEI_PRESCRIT.DATE,
            PEI_PRESCRIT.AGENT,
            PEI_PRESCRIT.NB_POTEAUX,
            PEI_PRESCRIT.COMMENTAIRE,
        ).from(PEI_PRESCRIT)
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(PEI_PRESCRIT.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue,
                    isSuperAdmin,
                ),
            )
            .and(bbox?.let { ST_Within(ST_Transform(PEI.GEOMETRIE, srid), bbox) })
            .fetchInto()
    }

    fun getPermisWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<PermisCarte> {
        return dsl.select(
            ST_Transform(PERMIS.GEOMETRIE, srid).`as`("elementGeometrie"),
            PERMIS.ID.`as`("elementId"),
        ).from(PERMIS)
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(PERMIS.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue,
                    isSuperAdmin,
                ),
            )
            .and(bbox?.let { ST_Within(ST_Transform(PERMIS.GEOMETRIE, srid), bbox) })
            .fetchInto()
    }

    /**
     * Récupère les évènements selon la crise et le EvenementStatutMode associé.
     */
    fun getEvenementProjetFromCrise(criseId: UUID, srid: Int, evenementState: EvenementStatutMode?): Collection<EvenementCarte> {
        return dsl.select(ST_Transform(EVENEMENT.GEOMETRIE, srid).`as`("elementGeometrie"), EVENEMENT.ID.`as`("elementId"))
            .from(EVENEMENT)
            .where(
                EVENEMENT.CRISE_ID.eq(criseId),
                EVENEMENT.IS_CLOSED.eq(false),
                evenementState?.let {
                    if (it == EvenementStatutMode.ANTICIPATION) {
                        EVENEMENT.STATUT_MODE.`in`(EvenementStatutMode.OPERATIONNEL, EvenementStatutMode.ANTICIPATION)
                    } else {
                        EVENEMENT.STATUT_MODE.eq(it)
                    }
                },
            )
            .fetchInto()
    }

    /**
     * Récupère les évènements dans une BBOX selon la crise.
     */
    fun getEvenementProjetFromCriseAndBbox(criseId: UUID, bbox: Field<Geometry?>, srid: Int, evenementState: EvenementStatutMode?): Collection<EvenementCarte> {
        return dsl.select(ST_Transform(EVENEMENT.GEOMETRIE, srid).`as`("elementGeometrie"), EVENEMENT.ID.`as`("elementId"))
            .from(EVENEMENT)
            .where(
                EVENEMENT.CRISE_ID.eq(criseId),
                EVENEMENT.IS_CLOSED.eq(false),
                evenementState?.let {
                    if (it == EvenementStatutMode.ANTICIPATION) {
                        EVENEMENT.STATUT_MODE.`in`(EvenementStatutMode.OPERATIONNEL, EvenementStatutMode.ANTICIPATION)
                    } else {
                        EVENEMENT.STATUT_MODE.eq(it)
                    }
                },
            ).and(
                ST_Within(EVENEMENT.GEOMETRIE, ST_Transform(bbox, SRID)),
            )
            .fetchInto()
    }

    fun getAdresse(bbox: Field<Geometry?>?, srid: Int, zoneId: UUID?, isSuperAdmin: Boolean): Collection<AdresseCarte> {
        return dsl.select(
            ST_Transform(ADRESSE.GEOMETRIE, srid).`as`("elementGeometrie"),
            ADRESSE.ID.`as`("elementId"),
            ADRESSE.TYPE,
            ADRESSE.DESCRIPTION,
            ADRESSE.DATE_CONSTAT,
            multiset(
                dsl.select(
                    ADRESSE_SOUS_TYPE_ELEMENT.LIBELLE,
                    multiset(
                        selectDistinct(ADRESSE_TYPE_ANOMALIE.LIBELLE)
                            .from(ADRESSE_TYPE_ANOMALIE)
                            .join(L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE)
                            .on(L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE.ADRESSE_TYPE_ANOMALIE_ID.eq(ADRESSE_TYPE_ANOMALIE.ID))
                            .where(ADRESSE_ELEMENT.ID.eq(L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE.ELEMENT_ID)),
                    ).convertFrom { record ->
                        record?.map { r ->
                            r.value1()
                        }?.joinToString()
                    },
                )
                    .from(ADRESSE_SOUS_TYPE_ELEMENT)
                    .join(ADRESSE_ELEMENT)
                    .on(ADRESSE.ID.eq(ADRESSE_ELEMENT.ADRESSE_ID))
                    .where(ADRESSE_SOUS_TYPE_ELEMENT.ID.eq(ADRESSE_ELEMENT.SOUS_TYPE)),
            ).convertFrom { record ->
                record?.map { r ->
                    SousElementAvecAnomalie(
                        sousElement = r.value1()!!,
                        listeAnomalie = r.value2(),
                    )
                }
            }.`as`("listSousElementAvecAnomalie"),
        )
            .from(ADRESSE)
            .leftJoin(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(ADRESSE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue,
                    isSuperAdmin,
                ),
            )
            .and(bbox?.let { ST_Within(ST_Transform(ADRESSE.GEOMETRIE, srid), bbox) })
            .fetchInto()
    }

    fun getDebitSimultaneWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<DebitSimultaneCarte> {
        return dsl.select(
            ST_Transform(DEBIT_SIMULTANE.GEOMETRIE, srid).`as`("elementGeometrie"),
            DEBIT_SIMULTANE.ID.`as`("elementId"),
            DEBIT_SIMULTANE.NUMERO_DOSSIER,
            PIBI.TYPE_RESEAU_ID.`as`("typeReseauId"),
            multiset(
                selectDistinct(PEI.NUMERO_COMPLET)
                    .from(PEI)
                    .join(L_DEBIT_SIMULTANE_MESURE_PEI)
                    .on(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID.eq(PEI.ID))
                    .join(DEBIT_SIMULTANE_MESURE)
                    .on(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID.eq(DEBIT_SIMULTANE_MESURE.ID))
                    .where(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(DEBIT_SIMULTANE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }?.joinToString()
            }.`as`("listeNumeroPei"),
        )
            .from(DEBIT_SIMULTANE)
            .join(DEBIT_SIMULTANE_MESURE)
            .on(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(DEBIT_SIMULTANE.ID))
            .join(L_DEBIT_SIMULTANE_MESURE_PEI)
            .on(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID.eq(DEBIT_SIMULTANE_MESURE.ID))
            .join(PIBI)
            .on(PIBI.ID.eq(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(
                        DEBIT_SIMULTANE.GEOMETRIE,
                        DSL.field(
                            DSL.select(ZONE_INTEGRATION.GEOMETRIE).from(ZONE_INTEGRATION)
                                .where(ZONE_INTEGRATION.ID.eq(zoneId)),
                        ),
                    ).isTrue,
                    isSuperAdmin,
                ),
            )
            .and(bbox?.let { ST_Within(ST_Transform(DEBIT_SIMULTANE.GEOMETRIE, srid), bbox) })
            .fetchInto()
    }

    fun getOldebWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<OldebCarte> =
        dsl.with(OldebRepository.lastOldebVisiteCte)
            .select(
                ST_Transform(OLDEB.GEOMETRIE, srid).`as`("elementGeometrie"),
                OLDEB.ID.`as`("elementId"),
                OLDEB_TYPE_DEBROUSSAILLEMENT.CODE.`as`("etatDebroussaillement"),
            )
            .from(OLDEB)
            .leftJoin(OldebRepository.lastOldebVisiteCte).on(OLDEB.ID.eq(OldebRepository.lastOldebVisiteCte.field("OLDEB_ID", UUID::class.java)))
            .leftJoin(OLDEB_TYPE_DEBROUSSAILLEMENT).on(OLDEB_TYPE_DEBROUSSAILLEMENT.ID.eq(OldebRepository.lastOldebVisiteCte.field("OLDEB_TYPE_DEBROUSSAILLEMENT_ID", UUID::class.java)))
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(OLDEB.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue,
                    isSuperAdmin,
                ),
            )
            .and(bbox?.let { ST_Within(ST_Transform(OLDEB.GEOMETRIE, srid), bbox) })
            .fetchInto()

    /**
     * Récupère les RCCI selon la zone de compétence
     */
    fun getRcciWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<RcciCarte> =
        dsl.select(
            ST_Transform(RCCI.GEOMETRIE, srid).`as`("elementGeometrie"),
            RCCI.ID.`as`("elementId"),
            RCCI.DATE_INCENDIE,
        )
            .from(RCCI)
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(RCCI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue,
                    isSuperAdmin,
                ),
            )
            .and(bbox?.let { ST_Within(ST_Transform(RCCI.GEOMETRIE, srid), bbox) })
            .fetchInto()

    abstract class ElementCarte {
        abstract val elementGeometrie: Geometry
        abstract val elementId: UUID
        abstract val typeElementCarte: TypeElementCarte

        // Propriétés à afficher dans la tooltip
        abstract var propertiesToDisplay: String?
    }

    data class PeiHighLightCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        override var propertiesToDisplay: String? = null,
    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.PEI_HIGHLIGHT
    }

    data class PeiCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        override var propertiesToDisplay: String? = null,
        val hasIndispoTemp: Boolean = false,
        val hasTourneeReservee: Boolean = false,
        val hasTournee: Boolean = false,
        val hasDebitSimultane: Boolean = false,
        val natureDeciCode: String,
        val pibiTypeReseauId: UUID?,
        val peiNumeroComplet: String,

        // TODO à compléter au besoin

    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.PEI
    }

    data class PeiProjetCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        override var propertiesToDisplay: String? = null,

    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.PEI_PROJET
    }

    data class EvenementCarte(
        override val elementGeometrie: Geometry,
        override val elementId: UUID,
        override var propertiesToDisplay: String? = null,
    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.CRISE
    }

    data class PeiPrescritsCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        val peiPrescritNumDossier: String?,
        val peiPrescritDebit: Int?,
        val peiPrescritDate: ZonedDateTime?,
        val peiPrescritAgent: String?,
        val peiPrescritNbPoteaux: Int?,
        val peiPrescritCommentaire: String?,
    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.PEI_PRESCRIT

        override var propertiesToDisplay: String? =
            "<b>Numéro de dossier :</b> ${peiPrescritNumDossier.orEmpty()}<br/>" +
                "<b>Débit :</b> ${peiPrescritDebit?.toString().orEmpty()}<br/>" +
                "<b>Date dépot :</b> ${peiPrescritDate?.format(DateTimeFormatter.ofPattern(DateUtils.PATTERN_NATUREL_DATE_ONLY, Locale.getDefault())).orEmpty()}<br/>" +
                "<b>Agent :</b> ${peiPrescritAgent.orEmpty()}<br/>" +
                "<b>Nombre de poteaux :</b> ${peiPrescritNbPoteaux?.toString().orEmpty()}<br/>" +
                "<b>Commentaire :</b> ${peiPrescritCommentaire.orEmpty()}<br/>"
    }

    data class PermisCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        override var propertiesToDisplay: String? = null,

    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.PERMIS
    }

    data class DebitSimultaneCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        val listeNumeroPei: String?,
        val debitSimultaneNumeroDossier: String,
        val typeReseauId: UUID,

        // TODO à compléter au besoin

    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.DEBIT_SIMULTANE

        override var propertiesToDisplay: String? =
            "Numéro du dossier : $debitSimultaneNumeroDossier <br />Liste des PEI concernés : $listeNumeroPei"
    }

    data class AdresseCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        val adresseType: EtatAdresse,
        val adresseDescription: String?,
        val adresseDateConstat: ZonedDateTime?,
        val listSousElementAvecAnomalie: List<SousElementAvecAnomalie> = listOf(),

    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.ADRESSE

        override var propertiesToDisplay: String? = "<b>Etat :</b> ${getEtatAdresseLibelle(adresseType)} <br/>" +
            "<b>Description :</b> ${adresseDescription.orEmpty()} " +
            "<br /><b>Date constat :</b> ${adresseDateConstat?.format(DateTimeFormatter.ofPattern(DateUtils.PATTERN_NATUREL, Locale.getDefault()))} " +
            "<br/><b>Liste des élements :</b> ${
                listSousElementAvecAnomalie.joinToString {
                    "${it.sousElement} (anomalies constatées : ${
                        it.listeAnomalie.takeIf { !it.isNullOrBlank() }.apply { it } ?: "aucune anomalie"
                    })"
                }
            }"

        fun getEtatAdresseLibelle(etatAdresse: EtatAdresse) =
            when (etatAdresse) {
                EtatAdresse.EN_COURS -> "En cours"
                EtatAdresse.ACCEPTEE -> "Acceptée"
                EtatAdresse.REFUSEE -> "Refusée"
            }
    }

    data class SousElementAvecAnomalie(
        val sousElement: String,
        val listeAnomalie: String?,
    )

    data class OldebCarte(
        override val elementGeometrie: Polygon,
        override val elementId: UUID,
        val etatDebroussaillement: String? = null,
    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.OLDEB

        override var propertiesToDisplay: String? = "$etatDebroussaillement"
    }

    data class RcciCarte(
        override val elementGeometrie: Point,
        override val elementId: UUID,
        val rcciDateIncendie: Date?,
    ) : ElementCarte() {
        override val typeElementCarte: TypeElementCarte
            get() = TypeElementCarte.RCCI

        override var propertiesToDisplay: String? = "$rcciDateIncendie"
    }
}
