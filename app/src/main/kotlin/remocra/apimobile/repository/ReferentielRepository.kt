package remocra.apimobile.repository

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectOnConditionStep
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import remocra.GlobalConstants
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.ContactRoleForApiMobileData
import remocra.apimobile.data.PeiAnomalieForApiMobileData
import remocra.apimobile.data.PeiForApiMobileData
import remocra.data.PeiCaracteristqueData
import remocra.data.enums.PeiCaracteristique
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.jooq.remocra.tables.Organisme
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.LIEU_DIT
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.db.jooq.remocra.tables.references.V_PEI_LAST_MESURES
import remocra.db.jooq.remocra.tables.references.V_PEI_VISITE_DATE
import remocra.utils.AdresseUtils
import java.util.UUID
import java.util.stream.Collectors

class ReferentielRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        val pibiJumeleTable = PEI.`as`("PIBI_JUMELE")
    }

    fun getPeiList(): List<PeiForApiMobileData> {
        val X = DSL.field("round(st_x({0})::numeric, 2)", PEI.GEOMETRIE).`as`("x")
        val Y = DSL.field("round(st_y({0})::numeric, 2)", PEI.GEOMETRIE).`as`("y")
        val LON =
            DSL.field("round(st_x(st_transform({0}, 4326))::numeric, 8)", PEI.GEOMETRIE).`as`("lon")
        val LAT =
            DSL.field("round(st_y(st_transform({0}, 4326))::numeric, 8)", PEI.GEOMETRIE).`as`("lat")

        return dsl
            .select(
                PEI.ID,
                PEI.NATURE_ID.`as`("natureId"),
                PEI.NATURE_DECI_ID.`as`("natureDeciId"),
                PEI.DOMAINE_ID.`as`("domaineId"),
                PENA.DISPONIBILITE_HBE.`as`("dispoHbe"),
                PEI.DISPONIBILITE_TERRESTRE.`as`("dispoTerrestre"),
                X,
                Y,
                LON,
                LAT,
                PEI.NUMERO_COMPLET,
                PEI.TYPE_PEI,
                PEI.EN_FACE,
                PEI.NUMERO_VOIE,
                PEI.SUFFIXE_VOIE,
                PEI.VOIE_ID,
                VOIE.LIBELLE,
                PEI.VOIE_TEXTE,
                PEI.COMPLEMENT_ADRESSE,
                COMMUNE.CODE_POSTAL,
                COMMUNE.LIBELLE,
                PEI.LIEU_DIT_ID,
                LIEU_DIT.LIBELLE,
                PEI.OBSERVATION,
                PEI.GESTIONNAIRE_ID,
            )
            .from(PEI)
            .leftJoin(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            .leftJoin(LIEU_DIT).on(PEI.LIEU_DIT_ID.eq(LIEU_DIT.ID))
            .fetchInto()
    }

    fun getPeiAnomalieList(): List<PeiAnomalieForApiMobileData> {
        return dsl
            .selectDistinct(
                L_PEI_ANOMALIE.PEI_ID,
                L_PEI_ANOMALIE.ANOMALIE_ID,
            )
            .from(L_PEI_ANOMALIE)
            .where(
                L_PEI_ANOMALIE.ANOMALIE_ID.`in`(
                    dsl
                        .selectDistinct(ANOMALIE.ID)
                        .from(ANOMALIE)
                        .where(ANOMALIE.PROTECTED.isFalse)
                        .and(
                            ANOMALIE.ID.`in`(
                                dsl
                                    .selectDistinct(POIDS_ANOMALIE.ANOMALIE_ID)
                                    .from(POIDS_ANOMALIE),
                            ),
                        ),
                ),
            )
            .fetchInto()
    }

    fun getContactList(): List<ContactForApiMobileData> {
        return dsl
            .select(
                CONTACT.ID,
                CONTACT.ACTIF,
                L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID,
                CONTACT.FONCTION_CONTACT_ID,
                CONTACT.CIVILITE,
                CONTACT.NOM,
                CONTACT.PRENOM,
                CONTACT.NUMERO_VOIE,
                CONTACT.SUFFIXE_VOIE,
                CONTACT.VOIE_ID,
                VOIE.LIBELLE,
                CONTACT.VOIE_TEXT,
                CONTACT.LIEU_DIT_ID,
                LIEU_DIT.LIBELLE,
                CONTACT.LIEU_DIT_TEXT,
                CONTACT.COMMUNE_ID,
                COMMUNE.LIBELLE,
                CONTACT.CODE_POSTAL,
                CONTACT.COMMUNE_TEXT,
                CONTACT.PAYS,
                CONTACT.TELEPHONE,
                CONTACT.EMAIL,
            )
            .from(CONTACT)
            .innerJoin(L_CONTACT_GESTIONNAIRE).on(CONTACT.ID.eq(L_CONTACT_GESTIONNAIRE.CONTACT_ID))
            .leftJoin(VOIE).on(CONTACT.VOIE_ID.eq(VOIE.ID))
            .leftJoin(FONCTION_CONTACT).on(CONTACT.FONCTION_CONTACT_ID.eq(FONCTION_CONTACT.ID))
            .leftJoin(LIEU_DIT).on(CONTACT.LIEU_DIT_ID.eq(LIEU_DIT.ID))
            .leftJoin(COMMUNE).on(CONTACT.COMMUNE_ID.eq(COMMUNE.ID))
            .join(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID))
            .where(GESTIONNAIRE.ACTIF.isTrue)
            .fetchInto()
    }

    fun getContactRoleList(): List<ContactRoleForApiMobileData> {
        return dsl
            .selectDistinct(
                L_CONTACT_ROLE.CONTACT_ID,
                L_CONTACT_ROLE.ROLE_ID.`as`("roleContactId"),
            )
            .from(L_CONTACT_ROLE)
            .innerJoin(L_CONTACT_GESTIONNAIRE)
            .on(L_CONTACT_ROLE.CONTACT_ID.eq(L_CONTACT_GESTIONNAIRE.CONTACT_ID))
            .fetchInto()
    }

    /**
     * Retourne l'ensemble des poids/anomalies non système
     */
    fun getAnomaliePoidsList(): Collection<PoidsAnomalie> =
        dsl.select(*POIDS_ANOMALIE.fields())
            .from(POIDS_ANOMALIE)
            .innerJoin(ANOMALIE).on(POIDS_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
            .innerJoin(ANOMALIE_CATEGORIE).on(ANOMALIE.ANOMALIE_CATEGORIE_ID.eq(ANOMALIE_CATEGORIE.ID))
            .where(ANOMALIE_CATEGORIE.CODE.notEqual(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME))
            .fetchInto()

    /**
     * Construit une Map des caractéristiques désirées pour les PEI ; On *veut* conserver l'ordre des
     * caractéristiques pour affichage dans l'appli mobile.
     */
    fun getPeiCaracteristiques(
        pibiSelectedFields: List<PeiCaracteristique>,
        penaSelectedFields: List<PeiCaracteristique>,
    ): MutableMap<UUID, List<PeiCaracteristqueData?>> {
        val mapPeiCaracteristiques: MutableMap<UUID, List<PeiCaracteristqueData?>> =
            mutableMapOf<UUID, List<PeiCaracteristqueData?>>()

        // Les PIBI
        if (!pibiSelectedFields.isEmpty()) {
            var onClausePibi: SelectOnConditionStep<Record?> =
                dsl.select(buildSelectFields(pibiSelectedFields))
                    .from(PEI)
                    .innerJoin(PIBI)
                    .on(PEI.ID.eq(PIBI.ID))
            onClausePibi = buildJoinClauses(pibiSelectedFields, onClausePibi, createAlias())

            mapPeiCaracteristiques.putAll(fetchAndMapResults(pibiSelectedFields, onClausePibi))
        }

        // Les PENA
        if (!penaSelectedFields.isEmpty()) {
            var onClausePena: SelectOnConditionStep<Record?> =
                dsl
                    .select(buildSelectFields(penaSelectedFields))
                    .from(PEI)
                    .innerJoin(PENA)
                    .on(PEI.ID.eq(PENA.ID))
            onClausePena = buildJoinClauses(penaSelectedFields, onClausePena, createAlias())

            mapPeiCaracteristiques.putAll(fetchAndMapResults(penaSelectedFields, onClausePena))
        }

        return mapPeiCaracteristiques
    }

    /**
     * Construit un Set des Fields qu'on a besoin de projeter
     *
     * @param selectedFields List<PeiCaracteristique>
     * @return Set<Field></Field>>
     </PeiCaracteristique> */
    private fun buildSelectFields(selectedFields: List<PeiCaracteristique>): MutableSet<Field<*>?> {
        val fieldsToSelect: MutableSet<Field<*>?> = HashSet<Field<*>?>()
        // On a besoin de l'ID pour construire la map à retourner
        fieldsToSelect.add(PEI.ID)
        for (selectedField in selectedFields) {
            // à chaque objet correspond un champ en base
            fieldsToSelect.add(getFieldFromCaracteristique(selectedField, createAlias()))
        }
        return fieldsToSelect
    }

    private fun createAlias(): MutableMap<PeiCaracteristique?, TableImpl<*>?> {
        val mapAliases: MutableMap<PeiCaracteristique?, TableImpl<*>?> = HashMap<PeiCaracteristique?, TableImpl<*>?>()

        val autoritePolice: Organisme? = Organisme.ORGANISME.`as`("autorite_police")
        val servicePublic: Organisme? = Organisme.ORGANISME.`as`("service_public")
        val maintenceCtp: Organisme? = Organisme.ORGANISME.`as`("maintenance_ctp")

        mapAliases.put(PeiCaracteristique.AUTORITE_POLICE, autoritePolice)
        mapAliases.put(PeiCaracteristique.SERVICE_PUBLIC, servicePublic)
        mapAliases.put(PeiCaracteristique.MAINTENANCE_CTP, maintenceCtp)
        return mapAliases
    }

    /**
     * Fetch les résultats de la requête, et map les résultats dans une Map<idHydrant></idHydrant>,
     * List<PeiCaracteristiquePojo>>
     *
     * @param selectedFields List<PeiCaracteristique>
     * @param onClause SelectOnConditionStep
     * @return Map<Long></Long>, List<PeiCaracteristiquePojo>>
     </PeiCaracteristiquePojo></PeiCaracteristique></PeiCaracteristiquePojo> */
    private fun fetchAndMapResults(
        selectedFields: List<PeiCaracteristique>,
        onClause: SelectOnConditionStep<Record?>,
    ): MutableMap<UUID, List<PeiCaracteristqueData?>> {
        val mapPeiCaracteristiques: MutableMap<UUID, List<PeiCaracteristqueData?>> =
            mutableMapOf<UUID, List<PeiCaracteristqueData?>>()
        onClause.fetch().forEach { record ->
            val peiCaracteristiques: List<PeiCaracteristqueData?> = selectedFields.stream()
                .map { caracteristique: PeiCaracteristique ->
                    val value: Any? = record!!.get(getFieldFromCaracteristique(caracteristique, createAlias()))
                    PeiCaracteristqueData(caracteristique, value)
                }
                .collect(Collectors.toList())

            mapPeiCaracteristiques.put(record!!.get(PEI.ID)!!, peiCaracteristiques)
        }
        return mapPeiCaracteristiques
    }

    /**
     * Construit les clauses "INNER JOIN" à rajouter à la requête en fonction des champs voulus por
     * l'utilisateur. <br></br>
     * S'assure qu'on n'a qu'une seule fois la même jointure
     *
     * @param selectedFields List<PeiCaracteristique>
     * @param onClause SelectOnConditionStep
     * @return SelectOnConditionStep (mis à jour)
     </PeiCaracteristique> */
    private fun buildJoinClauses(
        selectedFields: List<PeiCaracteristique>,
        onClause: SelectOnConditionStep<Record?>,
        mapAlias: MutableMap<PeiCaracteristique?, TableImpl<*>?>,
    ): SelectOnConditionStep<Record?> {
        var onClause = onClause
        var jointureNature = false
        var jointureDiametre = false
        for (caracteristique in selectedFields) {
            when (caracteristique) {
                PeiCaracteristique.NATURE_PEI -> if (!jointureNature) {
                    onClause =
                        onClause
                            .innerJoin(NATURE)
                            .on(PEI.NATURE_ID.eq(NATURE.ID))
                    jointureNature = true
                }

                PeiCaracteristique.TYPE_DECI ->
                    onClause =
                        onClause
                            .innerJoin(NATURE_DECI)
                            .on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))

                PeiCaracteristique.AUTORITE_POLICE -> {
                    val autoriteDeci: Organisme = mapAlias.get(PeiCaracteristique.AUTORITE_POLICE) as Organisme
                    onClause = onClause.leftJoin(autoriteDeci).on(PEI.AUTORITE_DECI_ID.eq(autoriteDeci.ID))
                }

                PeiCaracteristique.SERVICE_PUBLIC -> {
                    val servicePublic: Organisme = mapAlias.get(PeiCaracteristique.SERVICE_PUBLIC) as Organisme
                    onClause = onClause.leftJoin(servicePublic).on(PEI.SERVICE_PUBLIC_DECI_ID.eq(servicePublic.ID))
                }

                PeiCaracteristique.MAINTENANCE_CTP -> {
                    val maintenanceCtp: Organisme = mapAlias.get(PeiCaracteristique.MAINTENANCE_CTP) as Organisme
                    onClause =
                        onClause.leftJoin(maintenanceCtp).on(PEI.MAINTENANCE_DECI_ID.eq(maintenanceCtp.ID))
                }

                PeiCaracteristique.DIAMETRE_NOMINAL -> if (!jointureDiametre) {
                    onClause =
                        onClause
                            .innerJoin(DIAMETRE)
                            .on(PIBI.DIAMETRE_ID.eq(DIAMETRE.ID))
                    jointureDiametre = true
                }

                PeiCaracteristique.TYPE_PEI -> if (!jointureNature) {
                    onClause =
                        onClause
                            .innerJoin(NATURE)
                            .on(PEI.NATURE_ID.eq(NATURE.ID))
                    jointureNature = true
                }

                PeiCaracteristique.COMPLEMENT -> Unit
                PeiCaracteristique.DATE_RECEPTION -> {
                    onClause =
                        onClause
                            .leftJoin(V_PEI_VISITE_DATE)
                            .on(V_PEI_VISITE_DATE.PEI_ID.eq(PEI.ID))
                }

                PeiCaracteristique.DEBIT -> {
                    onClause =
                        onClause
                            .leftJoin(V_PEI_LAST_MESURES)
                            .on(V_PEI_LAST_MESURES.PEI_ID.eq(PEI.ID))
                }

                PeiCaracteristique.CAPACITE -> Unit
                PeiCaracteristique.NUMERO_COMPLET -> Unit
                PeiCaracteristique.JUMELE -> {
                    onClause =
                        onClause
                            .leftJoin(pibiJumeleTable)
                            .on(pibiJumeleTable.ID.eq(PIBI.JUMELE_ID))
                }
                PeiCaracteristique.GROS_DEBIT -> {
                    if (!jointureNature) {
                        onClause =
                            onClause
                                .innerJoin(NATURE)
                                .on(PEI.NATURE_ID.eq(NATURE.ID))
                        jointureNature = true
                    }

                    if (!jointureDiametre) {
                        onClause =
                            onClause
                                .innerJoin(DIAMETRE)
                                .on(PIBI.DIAMETRE_ID.eq(DIAMETRE.ID))
                        jointureDiametre = true
                    }
                }
                PeiCaracteristique.ADRESSE -> onClause = onClause.leftJoin(VOIE).on(PEI.VOIE_ID.eq(VOIE.ID))
            }
        }
        return onClause
    }

    /**
     * Retourne le FIELD associé à la valeur d'un [PeiCaracteristique]
     *
     * @param caracteristique PeiCaracteristique
     * @return Field
     */
    private fun getFieldFromCaracteristique(
        caracteristique: PeiCaracteristique,
        mapAlias: MutableMap<PeiCaracteristique?, TableImpl<*>?>,
    ): Field<*> {
        when (caracteristique) {
            PeiCaracteristique.TYPE_PEI -> return NATURE.TYPE_PEI
            PeiCaracteristique.NATURE_PEI -> return NATURE.LIBELLE
            PeiCaracteristique.AUTORITE_POLICE -> return (mapAlias.get(PeiCaracteristique.AUTORITE_POLICE) as Organisme).LIBELLE
            PeiCaracteristique.TYPE_DECI -> return NATURE_DECI.LIBELLE
            PeiCaracteristique.SERVICE_PUBLIC -> return (mapAlias.get(PeiCaracteristique.SERVICE_PUBLIC) as Organisme).LIBELLE
            PeiCaracteristique.MAINTENANCE_CTP -> return (mapAlias.get(PeiCaracteristique.MAINTENANCE_CTP) as Organisme).LIBELLE
            PeiCaracteristique.COMPLEMENT -> return PEI.COMPLEMENT_ADRESSE
            PeiCaracteristique.DIAMETRE_NOMINAL -> return DIAMETRE.LIBELLE
            PeiCaracteristique.CAPACITE -> return PENA.CAPACITE
            PeiCaracteristique.DATE_RECEPTION -> return V_PEI_VISITE_DATE.LAST_RECEPTION
            PeiCaracteristique.DEBIT -> return V_PEI_LAST_MESURES.DEBIT
            PeiCaracteristique.NUMERO_COMPLET -> return PEI.NUMERO_COMPLET
            PeiCaracteristique.JUMELE -> return pibiJumeleTable.NUMERO_COMPLET
            PeiCaracteristique.GROS_DEBIT -> {
                val caseExpression = DSL.case_()
                    .`when`(
                        (
                            NATURE.CODE.eq(GlobalConstants.NATURE_PI)
                                .and(DIAMETRE.CODE.eq(GlobalConstants.DIAMETRE_150))
                            )
                            .or(
                                NATURE.CODE.eq(GlobalConstants.NATURE_BI).and(PIBI.JUMELE_ID.isNotNull),
                            ),
                        DSL.`val`("Oui"),
                    )
                    .otherwise(DSL.`val`("Non"))
                return caseExpression
            }

            PeiCaracteristique.ADRESSE -> return AdresseUtils.getDslConcatForAdresse()
        }
    }
}
