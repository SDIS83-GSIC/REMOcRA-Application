package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.jooq.impl.SQLDataType
import remocra.GlobalConstants
import remocra.data.GlobalData
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.pojos.Commune
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.DOMAINE
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.L_MODELE_COURRIER_PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PENA_ASPIRATION
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.RESERVOIR
import remocra.db.jooq.remocra.tables.references.ROLE_CONTACT
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.TYPE_PENA_ASPIRATION
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import remocra.db.jooq.remocra.tables.references.VISITE
import remocra.db.jooq.remocra.tables.references.VISITE_CTRL_DEBIT_PRESSION
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class CourrierRopRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        const val NATURE_DECI_PRIVE = "PRIVE"

        val croisementTable = VOIE.`as`("CROISEMENT")
    }

    private fun getDataPeiField(condition: Condition?): List<Field<out Any?>> {
        return listOf(
            PEI.ID,
            VOIE.LIBELLE,
            DOMAINE.LIBELLE,
            NATURE.LIBELLE,
            PEI.NUMERO_COMPLET,
            PEI.NUMERO_VOIE,
            PEI.COMPLEMENT_ADRESSE,
            croisementTable.LIBELLE.`as`("croisementLibelle"),
            PEI.DISPONIBILITE_TERRESTRE,
            PEI.OBSERVATION,
            multiset(
                selectDistinct(ANOMALIE.LIBELLE)
                    .from(ANOMALIE)
                    .join(L_PEI_ANOMALIE)
                    .on(L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                    // jointure présente pour la condition
                    .join(ANOMALIE_CATEGORIE)
                    .on(ANOMALIE_CATEGORIE.ID.eq(ANOMALIE.ANOMALIE_CATEGORIE_ID))
                    .where(L_PEI_ANOMALIE.PEI_ID.eq(Pei.PEI.ID))
                    .and(condition ?: DSL.trueCondition()),
            ).`as`("listeAnomalie").convertFrom { record ->
                record?.map { r ->
                    r.value1().let { it as String }
                }?.joinToString(",")
            },
            COMMUNE.LIBELLE,
            // Le champ dateRop sera récupéré au besoin dans une autre requête
            DSL.inline(null, SQLDataType.VARCHAR).`as`("dateRop"),
            NATURE_DECI.LIBELLE,
            NATURE_DECI.CODE,
            GESTIONNAIRE.LIBELLE,
        )
    }

    /**
     * Récupère les PEI indisponibles pour la rapport de ROP
     */
    fun getPeiIndisponibles(communeId: UUID): Collection<PeiIndisponible> =
        dsl.select(
            COMMUNE.LIBELLE,
            DOMAINE.LIBELLE,
            NATURE.LIBELLE,
            PEI.NUMERO_COMPLET,
            DSL.field("ST_X(${PEI.GEOMETRIE})").`as`("coordonneeX"),
            DSL.field("ST_Y(${PEI.GEOMETRIE})").`as`("coordonneeY"),
            VOIE.LIBELLE,
            croisementTable.LIBELLE.`as`("croisementLibelle"),
            multiset(
                selectDistinct(ANOMALIE.LIBELLE)
                    .from(ANOMALIE)
                    .join(L_PEI_ANOMALIE)
                    .on(L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                    .where(L_PEI_ANOMALIE.PEI_ID.eq(Pei.PEI.ID)),
            ).`as`("listeAnomalie").convertFrom { record ->
                record?.map { r ->
                    r.value1().let { it as String }
                }?.joinToString(",")
            },
            NATURE_DECI.LIBELLE,
        )
            .from(PEI)
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .join(DOMAINE)
            .on(DOMAINE.ID.eq(PEI.DOMAINE_ID))
            .join(NATURE)
            .on(NATURE.ID.eq(PEI.NATURE_ID))
            .join(NATURE_DECI)
            .on(NATURE_DECI.ID.eq(PEI.NATURE_DECI_ID))
            .join(VOIE)
            .on(VOIE.ID.eq(PEI.VOIE_ID))
            .leftJoin(croisementTable)
            .on(VOIE.ID.eq(PEI.CROISEMENT_ID))
            .where(PEI.DISPONIBILITE_TERRESTRE.eq(Disponibilite.INDISPONIBLE))
            .and(COMMUNE.ID.eq(communeId))
            .orderBy(PEI.NUMERO_COMPLET)
            .fetchInto()

    data class PeiIndisponible(
        val communeLibelle: String,
        val domaineLibelle: String,
        val natureLibelle: String,
        val natureDeciLibelle: String,
        val peiNumeroComplet: String,
        val coordonneeX: String,
        val coordonneeY: String,
        val listeAnomalie: String,
        val voieLibelle: String?,
        val croisementLibelle: String?,
    )

    /**
     * Récupère les PIBI
     */
    fun getPibi(communeId: UUID?, gestionnaireId: UUID? = null, conditionAnomalies: Condition? = null, onlyPublic: Boolean = false): Collection<PibiRop> =
        dsl.select(
            getDataPeiField(conditionAnomalies),
        ).select(
            DIAMETRE.LIBELLE,
            PIBI.DIAMETRE_CANALISATION,
            RESERVOIR.CAPACITE,
        )
            .from(PEI)
            .join(DOMAINE)
            .on(DOMAINE.ID.eq(PEI.DOMAINE_ID))
            .join(NATURE)
            .on(NATURE.ID.eq(PEI.NATURE_ID))
            .join(NATURE_DECI)
            .on(NATURE_DECI.ID.eq(PEI.NATURE_DECI_ID))
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .leftJoin(VOIE)
            .on(VOIE.ID.eq(PEI.VOIE_ID))
            .leftJoin(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(PEI.GESTIONNAIRE_ID))
            .leftJoin(croisementTable)
            .on(VOIE.ID.eq(PEI.CROISEMENT_ID))
            .join(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(DIAMETRE)
            .on(DIAMETRE.ID.eq(PIBI.DIAMETRE_ID))
            .leftJoin(RESERVOIR)
            .on(RESERVOIR.ID.eq(PIBI.RESERVOIR_ID))
            .where(
                if (onlyPublic) {
                    NATURE_DECI.CODE.notEqual(NATURE_DECI_PRIVE).and(PEI.COMMUNE_ID.eq(communeId))
                } else if (gestionnaireId != null) {
                    NATURE_DECI.CODE.eq(NATURE_DECI_PRIVE).and(PEI.GESTIONNAIRE_ID.eq(gestionnaireId))
                } else {
                    PEI.COMMUNE_ID.eq(communeId)
                },
            )
            .orderBy(PEI.NUMERO_COMPLET)
            .fetchInto()

    /**
     * Récupère les PENA
     */
    fun getPena(communeId: UUID?, gestionnaireId: UUID? = null, conditionAnomalies: Condition? = null, onlyPublic: Boolean = false): Collection<PenaRop> =
        dsl.select(
            getDataPeiField(conditionAnomalies),
        )
            .select(
                PENA.CAPACITE,
                PENA.QUANTITE_APPOINT,
            )
            .from(PEI)
            .join(DOMAINE)
            .on(DOMAINE.ID.eq(PEI.DOMAINE_ID))
            .join(NATURE)
            .on(NATURE.ID.eq(PEI.NATURE_ID))
            .join(NATURE_DECI)
            .on(NATURE_DECI.ID.eq(PEI.NATURE_DECI_ID))
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .leftJoin(VOIE)
            .on(VOIE.ID.eq(PEI.VOIE_ID))
            .leftJoin(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(PEI.GESTIONNAIRE_ID))
            .leftJoin(croisementTable)
            .on(VOIE.ID.eq(PEI.CROISEMENT_ID))
            .join(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .leftJoin(PENA_ASPIRATION)
            .on(PENA_ASPIRATION.PENA_ID.eq(PENA.ID))
            .leftJoin(TYPE_PENA_ASPIRATION)
            .on(TYPE_PENA_ASPIRATION.ID.eq(PENA_ASPIRATION.TYPE_PENA_ASPIRATION_ID))
            .where(
                if (onlyPublic) {
                    NATURE_DECI.CODE.notEqual(NATURE_DECI_PRIVE).and(PEI.COMMUNE_ID.eq(communeId))
                } else if (gestionnaireId != null) {
                    NATURE_DECI.CODE.eq(NATURE_DECI_PRIVE).and(PEI.GESTIONNAIRE_ID.eq(gestionnaireId))
                } else {
                    PEI.COMMUNE_ID.eq(communeId)
                },
            )
            .orderBy(PEI.NUMERO_COMPLET)
            .fetchInto()

    fun getPenaAspiration(listePenaId: Collection<UUID>): Collection<PenaAspirationWithType> =
        dsl.select(
            PENA_ASPIRATION.PENA_ID.`as`("penaId"),
            PENA_ASPIRATION.EST_NORMALISE,
            TYPE_PENA_ASPIRATION.LIBELLE,
        )
            .from(PENA_ASPIRATION)
            .leftJoin(TYPE_PENA_ASPIRATION)
            .on(TYPE_PENA_ASPIRATION.ID.eq(PENA_ASPIRATION.TYPE_PENA_ASPIRATION_ID))
            .where(PENA_ASPIRATION.PENA_ID.`in`(listePenaId))
            .fetchInto()

    data class PenaAspirationWithType(
        val penaId: UUID,
        val penaAspirationEstNormalise: Boolean = false,
        val typePenaAspirationLibelle: String?,
    )

    abstract class PeiRop {
        abstract val peiId: UUID
        abstract val peiNumeroComplet: String
        abstract val peiComplementAdresse: String?
        abstract val peiNumeroVoie: Int?
        abstract val voieLibelle: String?
        abstract val croisementLibelle: String?
        abstract val domaineLibelle: String
        abstract val natureLibelle: String
        abstract val peiDisponibiliteTerrestre: String
        abstract val listeAnomalie: String?
        abstract val peiObservation: String?
        abstract val communeLibelle: String
        abstract val natureDeciLibelle: String
        abstract val natureDeciCode: String
        abstract var dateRop: String?
        abstract var dateCtp: String?
        abstract var gestionnaireLibelle: String?
    }

    class PibiRop(
        override val peiId: UUID,
        override val peiNumeroComplet: String,
        override val voieLibelle: String?,
        override val croisementLibelle: String?,
        override val domaineLibelle: String,
        override val natureLibelle: String,
        override val peiDisponibiliteTerrestre: String,
        override val listeAnomalie: String?,
        override val peiObservation: String?,
        override val communeLibelle: String,
        override var dateRop: String?,
        override val natureDeciLibelle: String,
        override val peiComplementAdresse: String?,
        override val peiNumeroVoie: Int?,
        override var dateCtp: String?,
        override val natureDeciCode: String,
        override var gestionnaireLibelle: String?,

        val diametreLibelle: String?,
        val pibiDiametreCanalisation: Int?,
        val reservoirCapacite: Int?,
        var visiteCtrlDebitPressionDebit: Int?,
        var visiteCtrlDebitPressionPression: Double?,
        var visiteCtrlDebitPressionPressionDyn: Double?,
    ) : PeiRop()

    class PenaRop(
        override val peiId: UUID,
        override val peiNumeroComplet: String,
        override val voieLibelle: String?,
        override val croisementLibelle: String?,
        override val domaineLibelle: String,
        override val natureLibelle: String,
        override val peiDisponibiliteTerrestre: String,
        override val listeAnomalie: String?,
        override val peiObservation: String?,
        override val communeLibelle: String,
        override var dateRop: String?,
        override val natureDeciLibelle: String,
        override val peiComplementAdresse: String?,
        override val peiNumeroVoie: Int?,
        override var dateCtp: String?,
        override val natureDeciCode: String,
        override var gestionnaireLibelle: String?,

        val penaCapacite: Int?,
        val penaQuantiteAppoint: Double?,
        var penaAspirationEstNormalise: Boolean = false,
        var typePenaAspirationLibelle: String?,
    ) : PeiRop()

    fun getLastCtrlDebitPression(mapPibiByDateCtp: Map<UUID?, ZonedDateTime?>): Map<UUID, CtrlDebitPressionWithPei?> =
        dsl.select(
            VISITE.PEI_ID,
            VISITE.DATE,
            *VISITE_CTRL_DEBIT_PRESSION.fields(),
        )
            .from(VISITE_CTRL_DEBIT_PRESSION)
            .leftJoin(VISITE)
            .on(VISITE.ID.eq(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID))
            .where(VISITE.PEI_ID.`in`(mapPibiByDateCtp.keys))
            .orderBy(VISITE.DATE.desc())
            .fetch()
            .map { record ->
                if (mapPibiByDateCtp[record.getValue(VISITE.PEI_ID)] == record.getValue(VISITE.DATE)) {
                    return@map CtrlDebitPressionWithPei(
                        peiId = record.getValue(VISITE.PEI_ID)!!,
                        visiteCtrlDebitPression = VisiteCtrlDebitPression(
                            visiteCtrlDebitPressionVisiteId = record.getValue(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID)!!,
                            visiteCtrlDebitPressionPression = record.getValue(VISITE_CTRL_DEBIT_PRESSION.PRESSION),
                            visiteCtrlDebitPressionPressionDyn = record.getValue(VISITE_CTRL_DEBIT_PRESSION.PRESSION_DYN),
                            visiteCtrlDebitPressionDebit = record.getValue(VISITE_CTRL_DEBIT_PRESSION.DEBIT),
                        ),
                    )
                } else {
                    return@map null
                }
            }
            .filterNotNull()
            .groupBy { it.peiId }
            .mapValues { it.value[0] }

    data class CtrlDebitPressionWithPei(
        val peiId: UUID,
        val visiteCtrlDebitPression: VisiteCtrlDebitPression?,
    )
    fun getLastDateCtp(listPeiId: Collection<UUID>): MutableMap<UUID?, ZonedDateTime?> =
        getLastDate(listPeiId, TypeVisite.CTP)

    fun getLastDateRop(listPeiId: Collection<UUID>): MutableMap<UUID?, ZonedDateTime?> =
        getLastDate(listPeiId, TypeVisite.RECOP)

    private fun getLastDate(listPeiId: Collection<UUID>, typeVisite: TypeVisite): MutableMap<UUID?, ZonedDateTime?> =
        dsl.select(VISITE.PEI_ID, DSL.max(VISITE.DATE)).from(VISITE)
            .where(
                VISITE.PEI_ID.`in`(listPeiId),
            )
            .and(VISITE.TYPE_VISITE.eq(typeVisite))
            .groupBy(VISITE.PEI_ID)
            .fetchMap(VISITE.PEI_ID, DSL.max(VISITE.DATE))

    fun getOrganismeCommune(communeId: UUID): UUID? =
        dsl.select(ORGANISME.ID)
            .from(ORGANISME)
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(communeId))
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
            .join(TYPE_ORGANISME)
            .on(TYPE_ORGANISME.ID.eq(ORGANISME.TYPE_ORGANISME_ID))
            .where(ST_Within(COMMUNE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .and(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_COMMUNE))
            .fetchAnyInto()

    fun getDestinataireMaire(organismeId: UUID): Contact? =
        dsl.select(
            CONTACT.fields().asList(),
        )
            .from(CONTACT)
            .join(L_CONTACT_ORGANISME)
            .on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
            .join(L_CONTACT_ROLE)
            .on(L_CONTACT_ROLE.CONTACT_ID.eq(CONTACT.ID))
            .join(ROLE_CONTACT)
            .on(ROLE_CONTACT.ID.eq(L_CONTACT_ROLE.ROLE_ID))
            .where(ROLE_CONTACT.CODE.eq(GlobalConstants.ROLE_DESTINATAIRE_MAIRE_ROP))
            .and(L_CONTACT_ORGANISME.ORGANISME_ID.eq(organismeId))
            .fetchOneInto()

    fun getExpediteurGroupement(organismeId: UUID): ExpediteurGroupement? =
        dsl.select(
            *CONTACT.fields(),
            FONCTION_CONTACT.LIBELLE.`as`("fonction"),
        )
            .from(CONTACT)
            .join(FONCTION_CONTACT)
            .on(FONCTION_CONTACT.ID.eq(CONTACT.FONCTION_CONTACT_ID))
            .join(L_CONTACT_ORGANISME)
            .on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
            .join(L_CONTACT_ROLE)
            .on(L_CONTACT_ROLE.CONTACT_ID.eq(CONTACT.ID))
            .join(ROLE_CONTACT)
            .on(ROLE_CONTACT.ID.eq(L_CONTACT_ROLE.ROLE_ID))
            .where(ROLE_CONTACT.CODE.eq(GlobalConstants.ROLE_DESTINATAIRE_MAIRE_ROP))
            .and(L_CONTACT_ORGANISME.ORGANISME_ID.eq(organismeId))
            .fetchOneInto()

    data class ExpediteurGroupement(
        val contactId: UUID,
        val contactActif: Boolean,
        val contactCivilite: TypeCivilite?,
        val contactNom: String?,
        val contactPrenom: String?,
        val contactNumeroVoie: String?,
        val contactSuffixeVoie: String?,
        val contactLieuDitText: String?,
        val contactLieuDitId: UUID?,
        val contactVoieText: String?,
        val contactVoieId: UUID?,
        val contactCodePostal: String?,
        val contactCommuneText: String?,
        val contactCommuneId: UUID?,
        val contactPays: String?,
        val contactTelephone: String?,
        val contactEmail: String?,
        val contactFonctionContactId: UUID?,
        val fonctionContactLibelle: String?,
    )

    /**
     * Pour le SDIS 01, les CIS ont forcément un parent de type GROUPEMENT
     */
    fun getGroupement01(organismeId: UUID): DataGroupement? =
        dsl.select(
            ORGANISME.`as`("ORGANISME_PARENT").LIBELLE.`as`("groupementLibelle"),
            ORGANISME.`as`("ORGANISME_PARENT").EMAIL_CONTACT.`as`("groupementEmail"),
        )
            .from(ORGANISME)
            .join(TYPE_ORGANISME)
            .on(TYPE_ORGANISME.ID.eq(ORGANISME.TYPE_ORGANISME_ID))
            .join(ORGANISME.`as`("ORGANISME_PARENT"))
            .on(ORGANISME.PARENT_ID.eq(ORGANISME.`as`("ORGANISME_PARENT").ID))
            .where(ORGANISME.ID.eq(organismeId))
            .and(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_GROUPEMENT))
            .fetchOneInto()

    data class DataGroupement(
        val groupementLibelle: String,
        val groupementEmail: String?,
        val groupementTelephone: String?,
    )

    /**
     * Pour le SDIS 01, seul les utilisateurs avec un organisme de type CIS peuvent générer un courrier
     * Va chercher le cis de l'utilisateur connecté
     * retourne le nom du CIS
     */
    fun getCis01(organismeId: UUID): String? =
        dsl.select(
            ORGANISME.LIBELLE,
        )
            .from(ORGANISME)
            .join(TYPE_ORGANISME)
            .on(TYPE_ORGANISME.ID.eq(ORGANISME.TYPE_ORGANISME_ID))
            .where(ORGANISME.ID.eq(organismeId))
            .and(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_CIS))
            .fetchOneInto()

    // On va chercher le groupement en fonction de l'organisme de type commune
    fun getGroupement(organismeCommuneId: UUID): GlobalData.IdCodeLibelleData? =
        dsl.select(ORGANISME.ID.`as`("id"), ORGANISME.CODE.`as`("code"), ORGANISME.LIBELLE.`as`("libelle"))
            .from(ORGANISME)
            .join(TYPE_ORGANISME)
            .on(TYPE_ORGANISME.ID.eq(ORGANISME.TYPE_ORGANISME_ID))
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
            .join(ORGANISME.`as`("ORGANISME_COMMUNE"))
            .on(ORGANISME.`as`("ORGANISME_COMMUNE").ID.eq(organismeCommuneId))
            .join(ZONE_INTEGRATION.`as`("ZONE_COMMUNE"))
            .on(ZONE_INTEGRATION.`as`("ZONE_COMMUNE").ID.eq(ORGANISME.`as`("ORGANISME_COMMUNE").ZONE_INTEGRATION_ID))
            .where(ST_Within(ZONE_INTEGRATION.`as`("ZONE_COMMUNE").GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .and(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_GROUPEMENT))
            .fetchOneInto()

    fun getCommune(communeId: UUID): Commune =
        dsl.selectFrom(COMMUNE)
            .where(COMMUNE.ID.eq(communeId))
            .fetchSingleInto()

    fun checkProfilDroitRop(utilisateurId: UUID) =
        dsl.fetchExists(
            dsl.select(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID)
                .from(L_MODELE_COURRIER_PROFIL_DROIT)
                .join(MODELE_COURRIER)
                .on(MODELE_COURRIER.ID.eq(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID))
                .join(PROFIL_DROIT)
                .on(PROFIL_DROIT.ID.eq(L_MODELE_COURRIER_PROFIL_DROIT.PROFIL_DROIT_ID))
                .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
                .on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                .join(UTILISATEUR)
                .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
                .join(ORGANISME)
                .on(
                    ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID)
                        .and(UTILISATEUR.ORGANISME_ID.eq(ORGANISME.ID)),
                )
                .where(UTILISATEUR.ID.eq(utilisateurId))
                .and(MODELE_COURRIER.CODE.eq(GlobalConstants.COURRIER_CODE_ROP)),
        )

    fun getCis(cisId: UUID): Organisme =
        dsl.selectFrom(ORGANISME)
            .where(ORGANISME.ID.eq(cisId))
            .fetchSingleInto()
}
