package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.GlobalConstants
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.FicheResumeBloc
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.FICHE_RESUME_BLOC
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import remocra.db.jooq.remocra.tables.references.TOURNEE
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.VISITE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.db.jooq.remocra.tables.references.V_PEI_VISITE_DATE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class FicheResumeRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getFicheResume(): Collection<FicheResumeBloc> =
        dsl.selectFrom(FICHE_RESUME_BLOC)
            .orderBy(FICHE_RESUME_BLOC.COLONNE, FICHE_RESUME_BLOC.LIGNE).fetchInto()

    fun getPeiInfoFicheResume(peiId: UUID): PeiFicheResume {
        val pibiJumeleTable = PEI.`as`("PIBI_JUMELE")
        return dsl.select(
            PEI.DISPONIBILITE_TERRESTRE,
            PEI.TYPE_PEI,
            PEI.NUMERO_VOIE,
            PEI.SUFFIXE_VOIE,
            VOIE.LIBELLE,
            COMMUNE.CODE_INSEE,
            COMMUNE.CODE_POSTAL,
            PEI.COMPLEMENT_ADRESSE,
            COMMUNE.LIBELLE,
            multiset(
                selectDistinct(TOURNEE.LIBELLE)
                    .from(TOURNEE)
                    .join(L_TOURNEE_PEI)
                    .on(L_TOURNEE_PEI.TOURNEE_ID.eq(TOURNEE.ID))
                    .where(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID)),
            ).`as`("listeTournee").convertFrom { record ->
                record?.map { r ->
                    r.value1().let { it as String }
                }?.joinToString(",")
            },
            multiset(
                selectDistinct(
                    ANOMALIE.LIBELLE,
                    POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE,
                    POIDS_ANOMALIE.VAL_INDISPO_HBE,
                )
                    .from(ANOMALIE)
                    .join(L_PEI_ANOMALIE)
                    .on(L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                    .join(POIDS_ANOMALIE)
                    .on(POIDS_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                    .where(L_PEI_ANOMALIE.PEI_ID.eq(PEI.ID))
                    .and(POIDS_ANOMALIE.NATURE_ID.eq(PEI.NATURE_ID)),
            ).`as`("listeAnomalieValIndispo").convertFrom { record ->
                record?.map { r ->
                    AnomalieValIndispo(
                        anomalieLibelle = r.value1().toString(),
                        valIndispoTerrestre = r.value2(),
                        valIndispoHbe = r.value3(),
                    )
                }
            },
            PIBI.DIAMETRE_CANALISATION,
            PIBI.DEBIT_RENFORCE,
            pibiJumeleTable.NUMERO_COMPLET.`as`("pibiJumele"),
            PENA.CAPACITE,
            V_PEI_VISITE_DATE.LAST_RECOP,
            V_PEI_VISITE_DATE.LAST_CTP,
        ).from(PEI)
            .leftJoin(VOIE)
            .on(VOIE.ID.eq(PEI.VOIE_ID))
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(PEI.COMMUNE_ID))
            .join(NATURE)
            .on(NATURE.ID.eq(PEI.NATURE_ID))
            .leftJoin(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(DIAMETRE)
            .on((DIAMETRE.ID.eq(PIBI.DIAMETRE_ID)))
            .leftJoin(pibiJumeleTable)
            .on(pibiJumeleTable.ID.eq(PIBI.JUMELE_ID))
            .leftJoin(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .leftJoin(V_PEI_VISITE_DATE)
            .on(V_PEI_VISITE_DATE.PEI_ID.eq(PEI.ID))
            .where(PEI.ID.eq(peiId))
            .fetchSingleInto()
    }

    data class AnomalieValIndispo(
        val anomalieLibelle: String,
        val valIndispoTerrestre: Int?,
        val valIndispoHbe: Int?,
    )

    data class PeiFicheResume(
        val peiDisponibiliteTerrestre: Disponibilite,
        val peiTypePei: TypePei,
        val lastRecop: ZonedDateTime?,
        val lastCtp: ZonedDateTime?,
        val peiNumeroVoie: Int?,
        val peiSuffixeVoie: String?,
        val voieLibelle: String?,
        val peiComplementAdresse: String?,
        val communeCodeInsee: String,
        val communeCodePostal: String,
        val communeLibelle: String,
        val listeTournee: String?,
        val listeAnomalieValIndispo: Collection<AnomalieValIndispo>,
        val pibiDebitRenforce: Boolean?,
        val pibiJumele: String?,
        val diametreLibelle: String?,
        val pibiDiametreCanalisation: Int?,
        val capacite: Int?,
    )

    fun getCis(peiId: UUID): String? {
        val zoneIntegrationCisTable = ZONE_INTEGRATION.`as`("ZONE_COMPETENCE_CIS")
        val organismeCisTable = ORGANISME.`as`("ORGANISME_CIS")
        return dsl.select(
            ORGANISME.LIBELLE,
        ).from(ORGANISME)
            .join(TOURNEE)
            .on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
            .join(L_TOURNEE_PEI)
            .on(TOURNEE.ID.eq(L_TOURNEE_PEI.TOURNEE_ID))
            .join(PEI)
            .on(PEI.ID.eq(L_TOURNEE_PEI.PEI_ID))
            .join(zoneIntegrationCisTable)
            .on(ST_Within(ZONE_INTEGRATION.GEOMETRIE, zoneIntegrationCisTable.GEOMETRIE))
            .join(organismeCisTable)
            .on(organismeCisTable.ZONE_INTEGRATION_ID.eq(zoneIntegrationCisTable.ID))
            .join(TYPE_ORGANISME)
            .on(TYPE_ORGANISME.ID.eq(organismeCisTable.TYPE_ORGANISME_ID))
            .where(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_CIS))
            .and(PEI.ID.eq(peiId))
            .and(ST_Within(PEI.GEOMETRIE, zoneIntegrationCisTable.GEOMETRIE))
            .fetchOneInto()
    }

    fun getLastObservation(peiId: UUID): String? =
        dsl.select(VISITE.OBSERVATION)
            .from(VISITE)
            .where(VISITE.PEI_ID.eq(peiId))
            .orderBy(VISITE.DATE.desc())
            .limit(1)
            .fetchOneInto()

    fun getCaserne(peiId: UUID): String? =
        dsl.select(ORGANISME.LIBELLE)
            .from(ORGANISME)
            .join(TYPE_ORGANISME)
            .on(TYPE_ORGANISME.ID.eq(ORGANISME.TYPE_ORGANISME_ID))
            .join(TOURNEE)
            .on(TOURNEE.ORGANISME_ID.eq(ORGANISME.ID))
            .join(L_TOURNEE_PEI)
            .on(L_TOURNEE_PEI.TOURNEE_ID.eq(TOURNEE.ID))
            .where(L_TOURNEE_PEI.PEI_ID.eq(peiId))
            .and(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_CASERNE))
            .fetchOneInto()

    fun upsertFicheResume(ficheResumeBloc: FicheResumeBloc) {
        val record = dsl.newRecord(FICHE_RESUME_BLOC, ficheResumeBloc)
        dsl.insertInto(FICHE_RESUME_BLOC)
            .set(record)
            .onConflict(FICHE_RESUME_BLOC.ID)
            .doUpdate()
            .set(record)
            .execute()
    }

    fun deleteFicheResumeBloc() =
        dsl.deleteFrom(FICHE_RESUME_BLOC)
            .execute()
}
