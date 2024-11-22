package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.locationtech.jts.geom.Point
import remocra.CoordonneesXYSrid
import remocra.GlobalConstants
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.DebitSimultaneMesure
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LDebitSimultaneMesurePei
import remocra.db.jooq.remocra.tables.references.DEBIT_SIMULTANE
import remocra.db.jooq.remocra.tables.references.DEBIT_SIMULTANE_MESURE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_DEBIT_SIMULTANE_MESURE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.SITE
import remocra.db.jooq.remocra.tables.references.TYPE_RESEAU
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Distance
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class DebitSimultaneRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        private const val DISTANCE_PEI_DEBIT_SIMULTANE = 500
    }

    fun updateDebitSimultane(
        siteId: UUID?,
        pointGeometrie: Point,
        numeroDossier: String,
        debitSimultaneId: UUID,
    ) =
        dsl.update(DEBIT_SIMULTANE)
            .set(DEBIT_SIMULTANE.SITE_ID, siteId)
            .set(DEBIT_SIMULTANE.GEOMETRIE, pointGeometrie)
            .set(DEBIT_SIMULTANE.NUMERO_DOSSIER, numeroDossier)
            .where(DEBIT_SIMULTANE.ID.eq(debitSimultaneId))
            .execute()

    fun insertDebitSimultane(
        siteId: UUID?,
        pointGeometrie: Point,
        numeroDossier: String,
        debitSimultaneId: UUID,
    ) =
        dsl.insertInto(DEBIT_SIMULTANE)
            .set(DEBIT_SIMULTANE.ID, debitSimultaneId)
            .set(DEBIT_SIMULTANE.SITE_ID, siteId)
            .set(DEBIT_SIMULTANE.GEOMETRIE, pointGeometrie)
            .set(DEBIT_SIMULTANE.NUMERO_DOSSIER, numeroDossier)
            .execute()

    fun deleteDebitSimultane(debitSimultaneId: UUID) =
        dsl.deleteFrom(DEBIT_SIMULTANE)
            .where(DEBIT_SIMULTANE.ID.eq(debitSimultaneId))
            .execute()

    fun deleteDebitSimultaneMesure(debitSimultaneMesureId: UUID) =
        dsl.deleteFrom(DEBIT_SIMULTANE_MESURE)
            .where(DEBIT_SIMULTANE_MESURE.ID.eq(debitSimultaneMesureId))
            .execute()

    fun deleteDebitSimultaneMesureByDebitSimultaneId(debitSimultaneId: UUID) =
        dsl.deleteFrom(DEBIT_SIMULTANE_MESURE)
            .where(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(debitSimultaneId))
            .execute()

    fun deleteLDebitSimultaneMesurePei(debitSimultaneMesureId: UUID) =
        dsl.deleteFrom(L_DEBIT_SIMULTANE_MESURE_PEI)
            .where(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID.eq(debitSimultaneMesureId))
            .execute()

    fun insertLDebitSimultaneMesurePei(lDebitSimultaneMesurePei: LDebitSimultaneMesurePei) =
        dsl.insertInto(L_DEBIT_SIMULTANE_MESURE_PEI)
            .set(dsl.newRecord(L_DEBIT_SIMULTANE_MESURE_PEI, lDebitSimultaneMesurePei))
            .execute()

    fun upsertDebitSimultaneMesure(debitSimultaneMesure: DebitSimultaneMesure) =
        dsl.insertInto(DEBIT_SIMULTANE_MESURE)
            .set(dsl.newRecord(DEBIT_SIMULTANE_MESURE, debitSimultaneMesure))
            .onConflict(DEBIT_SIMULTANE_MESURE.ID)
            .doUpdate()
            .set(dsl.newRecord(DEBIT_SIMULTANE_MESURE, debitSimultaneMesure))
            .execute()

    fun getDebitSimultane(debitSimultaneId: UUID, isSuperAdmin: Boolean, zoneCompetenceId: UUID?): DebitSimultaneWithSite =
        dsl.select(
            DEBIT_SIMULTANE.ID,
            DEBIT_SIMULTANE.SITE_ID,
            DEBIT_SIMULTANE.NUMERO_DOSSIER,
            SITE.LIBELLE,
        )
            .from(DEBIT_SIMULTANE)
            .leftJoin(SITE)
            .on(SITE.ID.eq(DEBIT_SIMULTANE.SITE_ID))
            .leftJoin(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(zoneCompetenceId))
            .where(DEBIT_SIMULTANE.ID.eq(debitSimultaneId))
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(DEBIT_SIMULTANE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE),
                    isSuperAdmin,
                ),
            )
            .fetchSingleInto()

    data class DebitSimultaneWithSite(
        val debitSimultaneId: UUID,
        val debitSimultaneSiteId: UUID?,
        val debitSimultaneNumeroDossier: String,
        val siteLibelle: String?,
    )

    fun getDebitSimultaneMesure(debitSimultaneId: UUID): Collection<DebitSimultaneMesureWithPibi> =
        dsl.select(
            DEBIT_SIMULTANE_MESURE.ID,
            DEBIT_SIMULTANE_MESURE.DEBIT_REQUIS,
            DEBIT_SIMULTANE_MESURE.DEBIT_MESURE,
            DEBIT_SIMULTANE_MESURE.DEBIT_RETENU,
            DEBIT_SIMULTANE_MESURE.DATE_MESURE,
            DEBIT_SIMULTANE_MESURE.COMMENTAIRE,
            DEBIT_SIMULTANE_MESURE.IDENTIQUE_RESEAU_VILLE,
            DOCUMENT.ID,
            DOCUMENT.NOM_FICHIER,
            multiset(
                selectDistinct(
                    PIBI.ID,
                    PIBI.DIAMETRE_CANALISATION,
                    TYPE_RESEAU.LIBELLE,
                )
                    .from(PIBI)
                    .join(L_DEBIT_SIMULTANE_MESURE_PEI)
                    .on(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID.eq(PIBI.ID))
                    .leftJoin(TYPE_RESEAU)
                    .on(PIBI.TYPE_RESEAU_ID.eq(TYPE_RESEAU.ID))
                    .where(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID.eq(DEBIT_SIMULTANE_MESURE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    PibiCanalisationTypeReseau(
                        pibiId = r.value1().let { it as UUID },
                        pibiDiametreCanalisation = r.value2(),
                        typeReseauLibelle = r.value3(),
                    )
                }
            }.`as`("listePibi"),
        )
            .from(DEBIT_SIMULTANE_MESURE)
            .leftJoin(DOCUMENT)
            .on(DOCUMENT.ID.eq(DEBIT_SIMULTANE_MESURE.DOCUMENT_ID))
            .where(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(debitSimultaneId))
            .fetchInto()

    fun getListDebitSimultaneMesure(debitSimultaneId: UUID): Collection<DebitSimultaneMesure> =
        dsl.select(
            DEBIT_SIMULTANE_MESURE.fields().asList(),
        )
            .from(DEBIT_SIMULTANE_MESURE)
            .where(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(debitSimultaneId))
            .fetchInto()

    data class DebitSimultaneMesureWithPibi(
        val debitSimultaneMesureId: UUID?,
        val debitSimultaneMesureDebitRequis: Int?,
        val debitSimultaneMesureDebitMesure: Int?,
        val debitSimultaneMesureDebitRetenu: Int?,
        val debitSimultaneMesureDateMesure: ZonedDateTime,
        val debitSimultaneMesureCommentaire: String?,
        val debitSimultaneMesureIdentiqueReseauVille: Boolean?,
        val documentId: UUID?,
        val documentNomFichier: String?,
        val listePibi: Collection<PibiCanalisationTypeReseau>,
    )

    data class PibiCanalisationTypeReseau(
        val pibiId: UUID,
        val pibiDiametreCanalisation: Int?,
        val typeReseauLibelle: String?,
    )

    fun getPibiForDebitSimultane(coordonneesXYSrid: CoordonneesXYSrid, typeReseauId: UUID): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            PEI.ID.`as`("id"),
            PEI.NUMERO_COMPLET.`as`("code"),
            PEI.NUMERO_COMPLET.`as`("libelle"),
        ).from(PEI)
            .join(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .join(NATURE_DECI)
            .on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .where(PIBI.TYPE_RESEAU_ID.eq(typeReseauId))
            .and(NATURE_DECI.CODE.eq(GlobalConstants.NATURE_DECI_PRIVE))
            .and(
                ST_Distance(
                    geometrieField = PEI.GEOMETRIE,
                    srid = coordonneesXYSrid.srid,
                    coordonneeX = coordonneesXYSrid.coordonneeX,
                    coordonneeY = coordonneesXYSrid.coordonneeY,
                ).lt(DISTANCE_PEI_DEBIT_SIMULTANE.toDouble()),
            )
            .fetchInto()

    fun getDocumentByDebitSimultaneMesureId(debitSimultaneId: UUID): Map<UUID?, Document> =
        dsl.select(
            *DOCUMENT.fields(),
            DEBIT_SIMULTANE_MESURE.ID,
        )
            .from(DOCUMENT)
            .join(DEBIT_SIMULTANE_MESURE)
            .on(DEBIT_SIMULTANE_MESURE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .fetchMap(DEBIT_SIMULTANE_MESURE.ID, Document::class.java)

    fun getDistance(listePibiId: Set<UUID>, coordonneesXYSrid: CoordonneesXYSrid): Collection<Boolean> =
        dsl.select(
            ST_Distance(PEI.GEOMETRIE, coordonneesXYSrid.srid, coordonneesXYSrid.coordonneeX, coordonneesXYSrid.coordonneeY).lt(
                DISTANCE_PEI_DEBIT_SIMULTANE.toDouble(),
            ),
        )
            .from(PEI)
            .where(PEI.ID.`in`(listePibiId))
            .fetchInto()

    fun getInfosGenerales(listePibiId: Set<UUID>): Collection<TypeReseauMaxCanalisationSite> =
        dsl.select(SITE.LIBELLE, TYPE_RESEAU.LIBELLE, PIBI.DIAMETRE_CANALISATION)
            .from(PEI)
            .join(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(SITE)
            .on(PEI.SITE_ID.eq(SITE.ID))
            .leftJoin(TYPE_RESEAU)
            .on(PIBI.TYPE_RESEAU_ID.eq(TYPE_RESEAU.ID))
            .where(PIBI.ID.`in`(listePibiId))
            .fetchInto()

    data class TypeReseauMaxCanalisationSite(
        val siteLibelle: String?,
        val typeReseauLibelle: String,
        val pibiDiametreCanalisation: Int?,
    )

    fun existDebitSimultaneWithPibi(pibiId: UUID) =
        dsl.fetchExists(
            dsl.select(DEBIT_SIMULTANE.ID).from(DEBIT_SIMULTANE)
                .join(DEBIT_SIMULTANE_MESURE)
                .on(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(DEBIT_SIMULTANE.ID))
                .join(L_DEBIT_SIMULTANE_MESURE_PEI)
                .on(DEBIT_SIMULTANE_MESURE.ID.eq(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID))
                .where(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID.eq(pibiId)),
        )
}
