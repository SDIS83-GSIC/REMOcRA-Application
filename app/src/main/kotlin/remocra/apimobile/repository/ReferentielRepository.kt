package remocra.apimobile.repository

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.GlobalConstants
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.ContactRoleForApiMobileData
import remocra.apimobile.data.PeiAnomalieForApiMobileData
import remocra.apimobile.data.PeiForApiMobileData
import remocra.data.enums.PeiCaracteristique
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.LIEU_DIT
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import remocra.db.jooq.remocra.tables.references.VOIE

class ReferentielRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
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
                // TODO
                DSL.noField().`as`("dispoHbe"),
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
            .fetchInto()
    }

    fun getContactRoleList(): List<ContactRoleForApiMobileData> {
        return dsl
            .selectDistinct(
                L_CONTACT_ROLE.CONTACT_ID,
                L_CONTACT_ROLE.ROLE_ID,
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
    ): Map<Long, List<PeiCaracteristiquePojo>> {
        return mapOf()
//        val mapPeiCaracteristiques: MutableMap<Long, List<PeiCaracteristiquePojo>> = HashMap()
//
//        // Les PIBI
//        if (pibiSelectedFields.isNotEmpty()) {
//            var onClausePibi: SelectOnConditionStep<Record?>? =
//                    dsl
//                            .select(buildSelectFields(pibiSelectedFields))
//                            .from(PEI)
//                            .innerJoin(PIBI)
//                            .on(PEI.ID.eq(PIBI.ID))
//            onClausePibi = buildJoinClauses(pibiSelectedFields, onClausePibi, createAlias())
//
//            mapPeiCaracteristiques.putAll(fetchAndMapResults(pibiSelectedFields, onClausePibi))
//        }
//
//        // Les PENA
//        if (penaSelectedFields.isNotEmpty()) {
//            var onClausePena: SelectOnConditionStep<Record?>? =
//                    dsl
//                            .select(buildSelectFields(penaSelectedFields))
//                            .from(PEI)
//                            .innerJoin(PENA)
//                            .on(PEI.ID.eq(PENA.ID))
//            onClausePena = buildJoinClauses(penaSelectedFields, onClausePena, createAlias())
//
//            mapPeiCaracteristiques.putAll(fetchAndMapResults(penaSelectedFields, onClausePena))
//        }
//
//        return mapPeiCaracteristiques
//    }
//
//    /**
//     * Construit un Set des Fields qu'on a besoin de projeter
//     *
//     * @param selectedFields List<PeiCaracteristique>
//     * @return Set<Field></Field>>
//    </PeiCaracteristique> */
//    private fun buildSelectFields(selectedFields: List<PeiCaracteristique>): Set<Field<*>> {
//        val fieldsToSelect: MutableSet<Field<*>> = HashSet()
//        // On a besoin de l'ID pour construire la map à retourner
//        fieldsToSelect.add(PEI.ID)
//        for (selectedField in selectedFields) {
//            // à chaque objet correspond un champ en base
//            fieldsToSelect.add(getFieldFromCaracteristique(selectedField, createAlias()))
//        }
//        return fieldsToSelect
    }
//
//    fun createAlias(): Map<PeiCaracteristique, TableImpl<*>> {
//        val mapAliases: MutableMap<PeiCaracteristique, TableImpl<*>> = HashMap()
//
//        val autoritePolice: Organisme = Organisme.ORGANISME.`as`("autorite_police")
//        val servicePublic: Organisme = Organisme.ORGANISME.`as`("service_public")
//        val maintenceCtp: Organisme = Organisme.ORGANISME.`as`("maintenance_ctp")
//
//        mapAliases[PeiCaracteristique.AUTORITE_POLICE] = autoritePolice
//        mapAliases[PeiCaracteristique.SERVICE_PUBLIC] = servicePublic
//        mapAliases[PeiCaracteristique.MAINTENANCE_CTP] = maintenceCtp
//        return mapAliases
//    }
//
//    /**
//     * Fetch les résultats de la requête, et map les résultats dans une Map<idHydrant></idHydrant>,
//     * List<PeiCaracteristiquePojo>>
//     *
//     * @param selectedFields List<PeiCaracteristique>
//     * @param onClause SelectOnConditionStep
//     * @return Map<Long></Long>, List<PeiCaracteristiquePojo>>
//    </PeiCaracteristiquePojo></PeiCaracteristique></PeiCaracteristiquePojo> */
//    fun fetchAndMapResults(
//            selectedFields: List<PeiCaracteristique>, onClause: SelectOnConditionStep<Record>): Map<Long, List<PeiCaracteristiquePojo>> {
//        val mapPeiCaracteristiques: MutableMap<Long, List<PeiCaracteristiquePojo>> = HashMap<Long, List<PeiCaracteristiquePojo>>()
//        onClause.fetchInto<RecordHandler<Record>>(
//                RecordHandler<Record> { record: Record ->
//                    val peiCaracteristiques: List<PeiCaracteristiquePojo> =
//                            selectedFields.stream()
//                                    .map(
//                                            Function<PeiCaracteristique, PeiCaracteristiquePojo> { caracteristique: PeiCaracteristique? ->
//                                                val value: Any =
//                                                        record.get(
//                                                                getFieldFromCaracteristique(caracteristique, createAlias()))
//                                                PeiCaracteristiquePojo(caracteristique, value)
//                                            })
//                                    .collect(Collectors.toList())
//                    mapPeiCaracteristiques[record.get<Long>(PEI.ID)] = peiCaracteristiques
//                })
//        return mapPeiCaracteristiques
//    }
//
//    /**
//     * Construit les clauses "INNER JOIN" à rajouter à la requête en fonction des champs voulus por
//     * l'utilisateur. <br></br>
//     * S'assure qu'on n'a qu'une seule fois la même jointure
//     *
//     * @param selectedFields List<PeiCaracteristique>
//     * @param onClause SelectOnConditionStep
//     * @return SelectOnConditionStep (mis à jour)
//    </PeiCaracteristique> */
//    fun buildJoinClauses(
//            selectedFields: List<PeiCaracteristique>,
//            onClause: SelectOnConditionStep<Record?>,
//            mapAlias: Map<PeiCaracteristique, TableImpl<*>>): SelectOnConditionStep<Record?> {
//        var onClause = onClause
//        var jointureNature = false
//        for (caracteristique in selectedFields) {
//            when (caracteristique) {
//
//
//
//                AUTORITE_POLICE -> {
//                    val autoriteDeci: Organisme = mapAlias[PeiCaracteristique.AUTORITE_POLICE] as Organisme
//                    onClause = onClause.innerJoin(autoriteDeci).on(PEI.AUTORITE_DECI.eq(autoriteDeci.ID))
//                }
//
//                SERVICE_PUBLIC -> {
//                    val servicePublic: Organisme = mapAlias[PeiCaracteristique.SERVICE_PUBLIC] as Organisme
//                    onClause = onClause.innerJoin(servicePublic).on(PEI.SP_DECI.eq(servicePublic.ID))
//                }
//
//                MAINTENANCE_CTP -> {
//                    val maintenanceCtp: Organisme = mapAlias[PeiCaracteristique.MAINTENANCE_CTP] as Organisme
//                    onClause =
//                            onClause.innerJoin(maintenanceCtp).on(PEI.MAINTENANCE_DECI.eq(maintenanceCtp.ID))
//                }
//
//                else -> null
//            }
//        }
//        return onClause
//    }
//
//    /**
//     * Retourne le FIELD associé à la valeur d'un [PeiCaracteristique]
//     *
//     * @param caracteristique PeiCaracteristique
//     * @return Field
//     */
//    fun getFieldFromCaracteristique(
//            caracteristique: PeiCaracteristique, mapAlias: Map<PeiCaracteristique, TableImpl<*>>): Field<*> {
//        when (caracteristique) {
//            AUTORITE_POLICE -> return (mapAlias[PeiCaracteristique.AUTORITE_POLICE] as Organisme).LIBELLE
//            SERVICE_PUBLIC -> return (mapAlias[PeiCaracteristique.SERVICE_PUBLIC] as Organisme).LIBELLE
//            MAINTENANCE_CTP -> return (mapAlias[PeiCaracteristique.MAINTENANCE_CTP] as Organisme).LIBELLE
//            COMPLEMENT -> return PEI.COMPLEMENT_ADRESSE
//            DATE_RECEPTION -> return PEI.DATE_RECEP
//            DEBIT -> return PIBI.DEBIT
//            CAPACITE -> return PENA.CAPACITE
//            else −>  null
//        }
//
//        throw IllegalArgumentException("Valeur '$caracteristique' non prévue")
//    }

    /**
     * Classe permettant de représenter un type d'attribut (défini par PeiCaracteristique) et la
     * valeur concernée (value)
     */
    class PeiCaracteristiquePojo(caracteristique: PeiCaracteristique, val value: Any) {
        private val caracteristique: PeiCaracteristique = caracteristique

        fun getCaracteristique(): PeiCaracteristique {
            return caracteristique
        }
    }
}
