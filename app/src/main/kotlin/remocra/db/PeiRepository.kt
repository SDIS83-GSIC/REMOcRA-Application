package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.InsertSetStep
import org.jooq.Record
import org.jooq.SortField
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.PeiData
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.Pei
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.web.pei.PeiEndPoint
import java.util.UUID

class PeiRepository
@Inject constructor(
    private val dsl: DSLContext,
) {
    companion object {

        // Alias de table
        val autoriteDeciAlias: Table<*> = ORGANISME.`as`("AUTORITE_DECI")
        val servicePublicDeciAlias: Table<*> = ORGANISME.`as`("SP_DECI")

        val peiData = listOf(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            PEI.NUMERO_INTERNE,
            PEI.DISPONIBILITE_TERRESTRE,
            PEI.TYPE_PEI,
            PEI.GEOMETRIE,
            PEI.COMMUNE_ID,
            PEI.VOIE_ID,
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

    fun getPeiWithFilter(param: PeiEndPoint.Params): List<PeiForTableau> {
        return dsl.select(
            PEI.ID,
            PEI.NUMERO_COMPLET,
            PEI.NUMERO_INTERNE,
            PEI.TYPE_PEI,
            PEI.DISPONIBILITE_TERRESTRE,
            PENA.DISPONIBILITE_HBE,
            NATURE.LIBELLE,
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
            /*
            Join des anomalies uniquement pour les filtres c'est pour cette raison qu'on ne prend pas de field
            de cette jointure
             */
            .leftJoin(L_PEI_ANOMALIE)
            .on(L_PEI_ANOMALIE.PEI_ID.eq(PEI.ID))
            .leftJoin(ANOMALIE)
            .on(ANOMALIE.ID.eq(L_PEI_ANOMALIE.ANOMALIE_ID)).and(ANOMALIE.ID.eq(L_PEI_ANOMALIE.ANOMALIE_ID))
            .where(param.filterBy?.toCondition() ?: DSL.noCondition())
            .groupBy(
                PEI.ID,
                PEI.NUMERO_COMPLET,
                PEI.NUMERO_INTERNE,
                PEI.TYPE_PEI,
                PEI.DISPONIBILITE_TERRESTRE,
                PENA.DISPONIBILITE_HBE,
                NATURE.LIBELLE,
                COMMUNE.LIBELLE,
                NATURE_DECI.LIBELLE,
                autoriteDeciAlias.field(ORGANISME.LIBELLE)?.`as`("AUTORITE_DECI"),
                servicePublicDeciAlias.field(ORGANISME.LIBELLE)?.`as`("SERVICE_PUBLIC_DECI"),
            )
            .orderBy(
                param.sortBy?.toCondition() ?: listOf(
                    DSL.length(PEI.NUMERO_COMPLET).asc(),
                    PEI.NUMERO_COMPLET.asc(),
                ),
            )
            .limit(param.limit)
            .offset(param.offset)
            .fetchInto()
    }

    fun countAllPeiWithFilter(param: PeiEndPoint.Params): Int {
        return getPeiWithFilter(PeiEndPoint.Params(filterBy = param.filterBy, sortBy = null, limit = null)).size
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
        val natureDeciLibelle: String,
        val autoriteDeci: String?,
        val servicePublicDeci: String?,
        val listeAnomalie: List<UUID>?,

        /*
            TODO
                - rajouter les dates quand on aura les visites
                - rajouter libellé tournée quand on aura les tournées
         */
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
    ) {

        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    peiNumeroComplet?.let { DSL.and(PEI.NUMERO_COMPLET.contains(it)) },
                    peiNumeroInterne?.let { DSL.and(PEI.NUMERO_INTERNE.contains(it)) },
                    communeId?.let { DSL.and(PEI.COMMUNE_ID.eq(it)) },
                    typePei?.let { DSL.and(PEI.TYPE_PEI.eq(it)) },
                    natureDeci?.let { DSL.and(PEI.NATURE_DECI_ID.eq(it)) },
                    natureId?.let { DSL.and(PEI.NATURE_ID.eq(it)) },
                    autoriteDeci?.let { DSL.and(PEI.AUTORITE_DECI_ID.eq(it)) },
                    servicePublicDeci?.let { DSL.and(PEI.SERVICE_PUBLIC_DECI_ID.eq(it)) },
                    peiDisponibiliteTerrestre?.let { DSL.and(PEI.DISPONIBILITE_TERRESTRE.eq(it)) },
                    penaDisponibiliteHbe?.let { DSL.and(PENA.DISPONIBILITE_HBE.eq(it)) },
                    listeAnomalie?.let { DSL.and(ANOMALIE.LIBELLE.containsIgnoreCase(it)) },
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
        )
    }

    fun getTypePei(idPei: UUID): TypePei =
        dsl.select(PEI.TYPE_PEI)
            .from(PEI)
            .where(PEI.ID.eq(idPei))
            .fetchSingleInto()

    fun getInfoPei(peiId: UUID): PeiData =
        dsl.select(peiData)
            .from(PEI)
            .where(PEI.ID.eq(peiId))
            .fetchSingleInto()

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
}
