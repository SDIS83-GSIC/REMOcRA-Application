package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
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
import remocra.db.jooq.remocra.tables.references.INDISPONIBILITE_TEMPORAIRE
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
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
            // On projette tous les champs composant l'adresse
            PEI.EN_FACE, PEI.NUMERO_VOIE, PEI.SUFFIXE_VOIE, PEI.VOIE_TEXTE, VOIE.LIBELLE, PEI.COMPLEMENT_ADRESSE,
            COMMUNE.CODE_INSEE,
            COMMUNE.CODE_POSTAL,
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
            V_PEI_VISITE_DATE.LAST_ROP,
            V_PEI_VISITE_DATE.LAST_CTP,
            DIAMETRE.LIBELLE,
            DSL.exists(
                DSL.select(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID).from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                    .join(INDISPONIBILITE_TEMPORAIRE)
                    .on(INDISPONIBILITE_TEMPORAIRE.ID.eq(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID))
                    .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID))
                    .and(INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT.le(dateUtils.now()))
                    .and(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.isNull.or(INDISPONIBILITE_TEMPORAIRE.DATE_FIN.ge(dateUtils.now()))),
            ).`as`("hasIndispoTemp"),
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
        val lastRop: ZonedDateTime?,
        val lastCtp: ZonedDateTime?,
        val peiEnFace: Boolean?,
        val peiNumeroVoie: String?,
        val peiSuffixeVoie: String?,
        val peiVoieTexte: String?,
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
        val penaCapacite: Int?,
        val hasIndispoTemp: Boolean = false,
    )

    fun getCis(peiId: UUID): Collection<String>? = dsl.selectDistinct(
        ORGANISME.LIBELLE,
    ).from(ORGANISME)
        .join(TYPE_ORGANISME)
        .on(TYPE_ORGANISME.ID.eq(ORGANISME.TYPE_ORGANISME_ID))
        .and(TYPE_ORGANISME.CODE.eq(GlobalConstants.TYPE_ORGANISME_CIS))
        .join(ZONE_INTEGRATION)
        .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
        .join(PEI)
        .on(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
        .where(PEI.ID.eq(peiId))
        .fetchInto()

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
