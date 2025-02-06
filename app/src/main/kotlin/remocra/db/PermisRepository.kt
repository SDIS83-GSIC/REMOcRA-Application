package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.max
import org.jooq.impl.DSL.select
import remocra.data.GlobalData
import remocra.db.jooq.historique.tables.references.TRACABILITE
import remocra.db.jooq.remocra.tables.pojos.LPermisCadastreParcelle
import remocra.db.jooq.remocra.tables.pojos.Permis
import remocra.db.jooq.remocra.tables.references.L_PERMIS_CADASTRE_PARCELLE
import remocra.db.jooq.remocra.tables.references.PERMIS
import remocra.db.jooq.remocra.tables.references.TYPE_PERMIS_AVIS
import remocra.db.jooq.remocra.tables.references.TYPE_PERMIS_INTERSERVICE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.time.ZonedDateTime
import java.util.UUID

class PermisRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getById(permisId: UUID): Permis =
        dsl.selectFrom(PERMIS).where(PERMIS.ID.eq(permisId)).fetchSingleInto()

    fun getAvis(): Collection<GlobalData.IdCodeLibellePprifData> =
        dsl.select(TYPE_PERMIS_AVIS.ID.`as`("id"), TYPE_PERMIS_AVIS.CODE.`as`("code"), TYPE_PERMIS_AVIS.LIBELLE.`as`("libelle"), TYPE_PERMIS_AVIS.PPRIF.`as`("pprif"))
            .from(TYPE_PERMIS_AVIS)
            .where(TYPE_PERMIS_AVIS.ACTIF)
            .fetchInto()

    fun getInterservice(): Collection<GlobalData.IdCodeLibellePprifData> =
        dsl.select(TYPE_PERMIS_INTERSERVICE.ID.`as`("id"), TYPE_PERMIS_INTERSERVICE.CODE.`as`("code"), TYPE_PERMIS_INTERSERVICE.LIBELLE.`as`("libelle"), TYPE_PERMIS_INTERSERVICE.PPRIF.`as`("pprif"))
            .from(TYPE_PERMIS_INTERSERVICE)
            .where(TYPE_PERMIS_INTERSERVICE.ACTIF)
            .fetchInto()

    fun insertPermis(permis: Permis) =
        dsl.insertInto(PERMIS).set(dsl.newRecord(PERMIS, permis)).execute()

    fun updatePermis(permis: Permis) =
        dsl.update(PERMIS)
            .set(PERMIS.LIBELLE, permis.permisLibelle)
            .set(PERMIS.NUMERO, permis.permisNumero)
            .set(PERMIS.SERVICE_INSTRUCTEUR_ID, permis.permisServiceInstructeurId)
            .set(PERMIS.TYPE_PERMIS_INTERSERVICE_ID, permis.permisTypePermisInterserviceId)
            .set(PERMIS.TYPE_PERMIS_AVIS_ID, permis.permisTypePermisAvisId)
            .set(PERMIS.RI_RECEPTIONNEE, permis.permisRiReceptionnee)
            .set(PERMIS.DOSSIER_RI_VALIDE, permis.permisDossierRiValide)
            .set(PERMIS.OBSERVATIONS, permis.permisObservations)
            .set(PERMIS.VOIE_TEXT, permis.permisVoieText)
            .set(PERMIS.VOIE_ID, permis.permisVoieId)
            .set(PERMIS.COMPLEMENT, permis.permisComplement)
            .set(PERMIS.COMMUNE_ID, permis.permisCommuneId)
            .set(PERMIS.ANNEE, permis.permisAnnee)
            .where(PERMIS.ID.eq(permis.permisId))
            .execute()

    fun batchInsertPermisParcelle(listePermisParcelle: List<LPermisCadastreParcelle>) =
        dsl.batch(
            listePermisParcelle.map {
                DSL.insertInto(L_PERMIS_CADASTRE_PARCELLE).set(
                    dsl.newRecord(
                        L_PERMIS_CADASTRE_PARCELLE,
                        it,
                    ),
                )
            },
        ).execute()

    fun getParcelleByPermisId(permisId: UUID): List<UUID> =
        dsl.select(L_PERMIS_CADASTRE_PARCELLE.CADASTRE_PARCELLE_ID).from(L_PERMIS_CADASTRE_PARCELLE).where(
            L_PERMIS_CADASTRE_PARCELLE.PERMIS_ID.eq(permisId),
        ).fetchInto()

    fun deletePermisParcelle(permisId: UUID) =
        dsl.deleteFrom(L_PERMIS_CADASTRE_PARCELLE).where(L_PERMIS_CADASTRE_PARCELLE.PERMIS_ID.eq(permisId)).execute()

    fun deletePermis(permisId: UUID) =
        dsl.deleteFrom(PERMIS).where(PERMIS.ID.eq(permisId)).execute()

    fun getLastUpdateDate(permisId: UUID): ZonedDateTime =
        dsl.select(max(TRACABILITE.DATE))
            .from(TRACABILITE)
            .where(TRACABILITE.OBJET_ID.eq(permisId))
            .fetchSingleInto()

    fun getInstructeurUsername(permisId: UUID): String =
        dsl.select(UTILISATEUR.USERNAME)
            .from(PERMIS)
            .join(UTILISATEUR).on(PERMIS.INSTRUCTEUR_ID.eq(UTILISATEUR.ID))
            .where(PERMIS.ID.eq(permisId))
            .fetchSingleInto()
}
