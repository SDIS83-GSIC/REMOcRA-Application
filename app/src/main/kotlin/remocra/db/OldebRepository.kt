package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Geometry
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.select
import remocra.data.GlobalData
import remocra.data.Params
import remocra.data.oldeb.OldebData
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.Oldeb
import remocra.db.jooq.remocra.tables.pojos.OldebCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebLocataire
import remocra.db.jooq.remocra.tables.pojos.OldebProprietaire
import remocra.db.jooq.remocra.tables.pojos.OldebPropriete
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAcces
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAction
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAvis
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCategorieAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCategorieCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebTypeDebroussaillement
import remocra.db.jooq.remocra.tables.pojos.OldebTypeResidence
import remocra.db.jooq.remocra.tables.pojos.OldebTypeSuite
import remocra.db.jooq.remocra.tables.pojos.OldebTypeZoneUrbanisme
import remocra.db.jooq.remocra.tables.pojos.OldebVisite
import remocra.db.jooq.remocra.tables.pojos.OldebVisiteAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebVisiteDocument
import remocra.db.jooq.remocra.tables.pojos.OldebVisiteSuite
import remocra.db.jooq.remocra.tables.references.CADASTRE_PARCELLE
import remocra.db.jooq.remocra.tables.references.CADASTRE_SECTION
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.OLDEB
import remocra.db.jooq.remocra.tables.references.OLDEB_CARACTERISTIQUE
import remocra.db.jooq.remocra.tables.references.OLDEB_LOCATAIRE
import remocra.db.jooq.remocra.tables.references.OLDEB_PROPRIETAIRE
import remocra.db.jooq.remocra.tables.references.OLDEB_PROPRIETE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_ACCES
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_ACTION
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_AVIS
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_CARACTERISTIQUE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_CATEGORIE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_DEBROUSSAILLEMENT
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_RESIDENCE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_SUITE
import remocra.db.jooq.remocra.tables.references.OLDEB_TYPE_ZONE_URBANISME
import remocra.db.jooq.remocra.tables.references.OLDEB_VISITE
import remocra.db.jooq.remocra.tables.references.OLDEB_VISITE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.OLDEB_VISITE_DOCUMENT
import remocra.db.jooq.remocra.tables.references.OLDEB_VISITE_SUITE
import remocra.db.jooq.remocra.tables.references.VOIE
import java.util.UUID

class OldebRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        val lastOldebVisite = name("LAST_OLDEB_VISITE")
        val lastOldebVisiteCte = lastOldebVisite.fields("OLDEB_ID", "OLDEB_TYPE_AVIS_ID", "OLDEB_TYPE_DEBROUSSAILLEMENT_ID", "DATE_DERNIERE_VISITE").`as`(
            select(
                OLDEB_VISITE.OLDEB_ID,
                OLDEB_VISITE.OLDEB_TYPE_AVIS_ID.`as`("OLDEB_TYPE_AVIS_ID"),
                OLDEB_VISITE.DEBROUSSAILLEMENT_PARCELLE_ID.`as`("OLDEB_TYPE_DEBROUSSAILLEMENT_ID"),
                OLDEB_VISITE.DATE_VISITE.`as`("DATE_DERNIERE_VISITE"),
            ).distinctOn(OLDEB_VISITE.OLDEB_ID)
                .from(OLDEB_VISITE)
                .orderBy(OLDEB_VISITE.OLDEB_ID, OLDEB_VISITE.DATE_VISITE.desc()),
        )
    }

    fun getList(params: Params<Filter, Sort>): List<OldebData> =
        dsl.with(lastOldebVisiteCte)
            .select(OLDEB.ID)
            .select(COMMUNE.LIBELLE.`as`("OLDEB_COMMUNE"))
            .select(VOIE.LIBELLE.`as`("OLDEB_ADRESSE"))
            .select(OLDEB_TYPE_AVIS.LIBELLE.`as`("OLDEB_TYPE_AVIS"))
            .select(lastOldebVisiteCte.field("DATE_DERNIERE_VISITE")?.`as`("OLDEB_DATE_DERNIERE_VISITE"))
            .select(OLDEB_TYPE_DEBROUSSAILLEMENT.LIBELLE.`as`("OLDEB_TYPE_DEBROUSSAILLEMENT"))
            .select(OLDEB_TYPE_ZONE_URBANISME.LIBELLE.`as`("OLDEB_TYPE_ZONE_URBANISME"))
            .select(OLDEB_TYPE_ZONE_URBANISME.LIBELLE.`as`("OLDEB_PARCELLE"))
            .select(OLDEB_TYPE_ZONE_URBANISME.LIBELLE.`as`("OLDEB_SECTION"))
            .select(CADASTRE_PARCELLE.NUMERO)
            .select(CADASTRE_SECTION.NUMERO)
            .from(OLDEB)
            .leftJoin(lastOldebVisiteCte).on(OLDEB.ID.eq(lastOldebVisiteCte.field("OLDEB_ID", UUID::class.java)))
            .leftJoin(OLDEB_TYPE_AVIS).on(OLDEB_TYPE_AVIS.ID.eq(lastOldebVisiteCte.field("OLDEB_TYPE_AVIS_ID", UUID::class.java)))
            .leftJoin(OLDEB_TYPE_DEBROUSSAILLEMENT).on(OLDEB_TYPE_DEBROUSSAILLEMENT.ID.eq(lastOldebVisiteCte.field("OLDEB_TYPE_DEBROUSSAILLEMENT_ID", UUID::class.java)))
            .join(COMMUNE).on(COMMUNE.ID.eq(OLDEB.COMMUNE_ID))
            .join(CADASTRE_PARCELLE).on(CADASTRE_PARCELLE.ID.eq(OLDEB.CADASTRE_PARCELLE_ID))
            .join(CADASTRE_SECTION).on(CADASTRE_SECTION.ID.eq(OLDEB.CADASTRE_SECTION_ID))
            .leftJoin(VOIE).on(OLDEB.VOIE_ID.eq(VOIE.ID))
            .leftJoin(OLDEB_TYPE_ZONE_URBANISME).on(OLDEB_TYPE_ZONE_URBANISME.ID.eq(OLDEB.OLDEB_TYPE_ZONE_URBANISME_ID))
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition())
            .offset(params.offset)
            .limit(params.limit)
            .fetchInto()

    fun getCount(filter: Filter?): Int =
        dsl.with(lastOldebVisiteCte)
            .selectCount()
            .from(OLDEB)
            .leftJoin(lastOldebVisiteCte).on(OLDEB.ID.eq(lastOldebVisiteCte.field("OLDEB_ID", UUID::class.java)))
            .leftJoin(OLDEB_TYPE_AVIS).on(OLDEB_TYPE_AVIS.ID.eq(lastOldebVisiteCte.field("OLDEB_TYPE_AVIS_ID", UUID::class.java)))
            .leftJoin(OLDEB_TYPE_DEBROUSSAILLEMENT).on(OLDEB_TYPE_DEBROUSSAILLEMENT.ID.eq(lastOldebVisiteCte.field("OLDEB_TYPE_DEBROUSSAILLEMENT_ID", UUID::class.java)))
            .leftJoin(CADASTRE_SECTION).on(CADASTRE_SECTION.ID.eq(OLDEB.CADASTRE_SECTION_ID))
            .leftJoin(CADASTRE_PARCELLE).on(CADASTRE_PARCELLE.ID.eq(OLDEB.CADASTRE_PARCELLE_ID))
            .join(COMMUNE).on(COMMUNE.ID.eq(OLDEB.COMMUNE_ID))
            .where(filter?.toCondition())
            .fetchSingleInto()

    data class Filter(
        val oldebCommune: UUID?,
        val oldebSection: String?,
        val oldebParcelle: String?,
        val oldebTypeZoneUrbanisme: UUID?,
        val oldebTypeDebroussaillement: UUID?,
        val oldebTypeAvis: UUID?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    oldebCommune?.let { OLDEB.COMMUNE_ID.eq(it) },
                    oldebSection?.let { CADASTRE_SECTION.NUMERO.containsIgnoreCaseUnaccent(it) },
                    oldebParcelle?.let { CADASTRE_PARCELLE.NUMERO.containsIgnoreCaseUnaccent(it) },
                    oldebTypeZoneUrbanisme?.let { OLDEB.OLDEB_TYPE_ZONE_URBANISME_ID.eq(it) },
                    oldebTypeDebroussaillement?.let { lastOldebVisiteCte.field("OLDEB_TYPE_DEBROUSSAILLEMENT_ID", UUID::class.java)?.eq(it) },
                    oldebTypeAvis?.let { lastOldebVisiteCte.field("OLDEB_TYPE_AVIS_ID", UUID::class.java)?.eq(it) },
                ),
            )
    }

    data class Sort(
        val oldebCommune: Int?,
        val oldebSection: Int?,
        val oldebParcelle: Int?,
        val oldebTypeZoneUrbanisme: Int?,
        val oldebDateDerniereVisite: Int?,
        val oldebTyoeDebroussaillement: Int?,
        val oldebTypeAvis: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            COMMUNE.LIBELLE.getSortField(oldebSection),
            CADASTRE_SECTION.NUMERO.getSortField(oldebSection),
            CADASTRE_PARCELLE.NUMERO.getSortField(oldebParcelle),
            OLDEB_TYPE_ZONE_URBANISME.LIBELLE.getSortField(oldebTypeZoneUrbanisme),
            lastOldebVisiteCte.field("DATE_DERNIERE_VISITE")?.getSortField(oldebDateDerniereVisite),
            OLDEB_TYPE_DEBROUSSAILLEMENT.LIBELLE.getSortField(oldebTyoeDebroussaillement),
            OLDEB_TYPE_AVIS.LIBELLE.getSortField(oldebTypeAvis),
        )
    }

    fun getTypeAction(): Map<UUID, OldebTypeAction> =
        dsl.selectFrom(OLDEB_TYPE_ACTION).fetchInto<OldebTypeAction>().associateBy { it.oldebTypeActionId }

    fun getTypeAvisMap(): Map<UUID, OldebTypeAvis> =
        dsl.selectFrom(OLDEB_TYPE_AVIS).fetchInto<OldebTypeAvis>().associateBy { it.oldebTypeAvisId }

    fun getTypeDebroussaillementMap(): Map<UUID, OldebTypeDebroussaillement> =
        dsl.selectFrom(OLDEB_TYPE_DEBROUSSAILLEMENT).fetchInto<OldebTypeDebroussaillement>().associateBy { it.oldebTypeDebroussaillementId }

    fun getTypeAnomalie(): Map<UUID, OldebTypeAnomalie> =
        dsl.selectFrom(OLDEB_TYPE_ANOMALIE).fetchInto<OldebTypeAnomalie>().associateBy { it.oldebTypeAnomalieId }

    fun getTypeCategorieAnomalie(): Map<UUID, OldebTypeCategorieAnomalie> =
        dsl.selectFrom(OLDEB_TYPE_CATEGORIE_ANOMALIE).fetchInto<OldebTypeCategorieAnomalie>().associateBy { it.oldebTypeCategorieAnomalieId }

    fun getTypeAcces(): Map<UUID, OldebTypeAcces> =
        dsl.selectFrom(OLDEB_TYPE_ACCES).fetchInto<OldebTypeAcces>().associateBy { it.oldebTypeAccesId }

    fun getTypeResidence(): Map<UUID, OldebTypeResidence> =
        dsl.selectFrom(OLDEB_TYPE_RESIDENCE).fetchInto<OldebTypeResidence>().associateBy { it.oldebTypeResidenceId }

    fun getTypeSuite(): Map<UUID, OldebTypeSuite> =
        dsl.selectFrom(OLDEB_TYPE_SUITE).fetchInto<OldebTypeSuite>().associateBy { it.oldebTypeSuiteId }

    fun getTypeZoneUrbanismeMap(): Map<UUID, OldebTypeZoneUrbanisme> =
        dsl.selectFrom(OLDEB_TYPE_ZONE_URBANISME).fetchInto<OldebTypeZoneUrbanisme>().associateBy { it.oldebTypeZoneUrbanismeId }

    fun getTypeCaracteristiqueMap(): Map<UUID, OldebTypeCaracteristique> =
        dsl.selectFrom(OLDEB_TYPE_CARACTERISTIQUE).fetchInto<OldebTypeCaracteristique>().associateBy { it.oldebTypeCaracteristiqueId }

    fun getTypeCategorieCaracteristiqueMap(): Map<UUID, OldebTypeCategorieCaracteristique> =
        dsl.selectFrom(OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE).fetchInto<OldebTypeCategorieCaracteristique>().associateBy { it.oldebTypeCategorieCaracteristiqueId }
    fun getLinkedTypeCaracteristiqueList(): List<GlobalData.IdCodeLibelleLienData> =
        dsl.select(
            OLDEB_TYPE_CARACTERISTIQUE.ID.`as`("id"),
            OLDEB_TYPE_CARACTERISTIQUE.CODE.`as`("code"),
            OLDEB_TYPE_CARACTERISTIQUE.LIBELLE.`as`("libelle"),
            OLDEB_TYPE_CARACTERISTIQUE.OLDEB_TYPE_CATEGORIE_ID.`as`("lienId"),
        )
            .from(OLDEB_TYPE_CARACTERISTIQUE)
            .fetchInto()

    fun getLinkedTypeAnomalieList(): List<GlobalData.IdCodeLibelleLienData> =
        dsl.select(
            OLDEB_TYPE_ANOMALIE.ID.`as`("id"),
            OLDEB_TYPE_ANOMALIE.CODE.`as`("code"),
            OLDEB_TYPE_ANOMALIE.LIBELLE.`as`("libelle"),
            OLDEB_TYPE_ANOMALIE.OLDEB_TYPE_CATEGORIE_ANOMALIE_ID.`as`("lienId"),
        )
            .from(OLDEB_TYPE_ANOMALIE)
            .fetchInto()

    fun checkLocataireExists(oldebId: UUID): Boolean =
        dsl.fetchExists(
            DSL.selectFrom(OLDEB_LOCATAIRE)
                .where(OLDEB_LOCATAIRE.OLDEB_ID.eq(oldebId)),
        )

    fun checkVisiteExists(visiteId: UUID): Boolean =
        dsl.fetchExists(
            DSL.selectFrom(OLDEB_VISITE)
                .where(OLDEB_VISITE.ID.eq(visiteId)),
        )

    fun checkVisiteSuiteExists(visiteSuiteId: UUID): Boolean =
        dsl.fetchExists(
            DSL.selectFrom(OLDEB_VISITE_SUITE)
                .where(OLDEB_VISITE_SUITE.ID.eq(visiteSuiteId)),
        )

    /**
     * SELECT
     */
    fun selectOldeb(oldebId: UUID): Oldeb =
        dsl.selectFrom(OLDEB).where(OLDEB.ID.eq(oldebId)).fetchSingleInto()

    fun selectCaracteristique(oldebId: UUID): List<UUID> =
        dsl.select(OLDEB_CARACTERISTIQUE.OLDEB_TYPE_CARACTERISTIQUE_ID).from(OLDEB_CARACTERISTIQUE).where(OLDEB_CARACTERISTIQUE.OLDEB_ID.eq(oldebId)).fetchInto()

    fun selectProprietaire(oldebId: UUID): OldebProprietaire? =
        dsl.select(*OLDEB_PROPRIETAIRE.fields()).from(OLDEB_PROPRIETAIRE).join(OLDEB_PROPRIETE).on(OLDEB_PROPRIETE.OLDEB_PROPRIETAIRE_ID.eq(OLDEB_PROPRIETAIRE.ID)).where(OLDEB_PROPRIETE.OLDEB_ID.eq(oldebId)).fetchOneInto()

    fun selectLocataire(oldebId: UUID): OldebLocataire? =
        dsl.selectFrom(OLDEB_LOCATAIRE).where(OLDEB_LOCATAIRE.OLDEB_ID.eq(oldebId)).fetchOneInto()

    fun selectPropriete(oldebId: UUID): OldebPropriete? =
        dsl.selectFrom(OLDEB_PROPRIETE).where(OLDEB_PROPRIETE.OLDEB_ID.eq(oldebId)).fetchOneInto()

    fun selectVisite(oldebId: UUID): List<OldebVisite> =
        dsl.selectFrom(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)).fetchInto()

    fun selectVisiteDocument(oldebVisiteId: UUID): List<Document> =
        dsl.select(*DOCUMENT.fields()).from(DOCUMENT).join(OLDEB_VISITE_DOCUMENT).on(OLDEB_VISITE_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID)).where(OLDEB_VISITE_DOCUMENT.OLDEB_VISITE_ID.eq(oldebVisiteId)).fetchInto()

    fun selectVisiteAnomalie(oldebVisiteId: UUID): List<UUID> =
        dsl.select(OLDEB_VISITE_ANOMALIE.OLDEB_TYPE_ANOMALIE_ID).from(OLDEB_VISITE_ANOMALIE).where(OLDEB_VISITE_ANOMALIE.OLDEB_VISITE_ID.eq(oldebVisiteId)).fetchInto()

    fun selectVisiteSuite(oldebVisiteId: UUID): List<OldebVisiteSuite> =
        dsl.selectFrom(OLDEB_VISITE_SUITE).where(OLDEB_VISITE_SUITE.OLDEB_VISITE_ID.eq(oldebVisiteId)).fetchInto()

    fun selectMissingVisite(oldebId: UUID, toKeep: List<UUID>?): List<OldebVisite> =
        dsl.selectFrom(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)).and(OLDEB_VISITE.ID.notIn(toKeep)).fetchInto()

    fun selectMissingVisiteDocument(oldebVisiteId: UUID, toKeep: List<UUID>? = listOf()): List<OldebVisiteDocument> =
        dsl.selectFrom(OLDEB_VISITE_DOCUMENT).where(OLDEB_VISITE_DOCUMENT.OLDEB_VISITE_ID.eq(oldebVisiteId)).and(OLDEB_VISITE_DOCUMENT.DOCUMENT_ID.notIn(toKeep)).fetchInto()

    fun selectOldebDocument(oldebId: UUID): List<UUID> =
        dsl.select(OLDEB_VISITE_DOCUMENT.DOCUMENT_ID).from(OLDEB_VISITE_DOCUMENT).join(OLDEB_VISITE).on(OLDEB_VISITE.ID.eq(OLDEB_VISITE_DOCUMENT.OLDEB_VISITE_ID)).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)).fetchInto()

    /**
     * CREATE
     */
    fun insertOldeb(oldeb: Oldeb): Int =
        dsl.insertInto(OLDEB).set(dsl.newRecord(OLDEB, oldeb)).execute()

    fun insertCaracteristique(oldebCaracteristique: OldebCaracteristique): Int =
        dsl.insertInto(OLDEB_CARACTERISTIQUE).set(dsl.newRecord(OLDEB_CARACTERISTIQUE, oldebCaracteristique)).execute()

    fun insertProprietaire(oldebProprietaire: OldebProprietaire): Int =
        dsl.insertInto(OLDEB_PROPRIETAIRE).set(dsl.newRecord(OLDEB_PROPRIETAIRE, oldebProprietaire)).execute()

    fun insertLocataire(oldebLocataire: OldebLocataire): Int =
        dsl.insertInto(OLDEB_LOCATAIRE).set(dsl.newRecord(OLDEB_LOCATAIRE, oldebLocataire)).execute()

    fun insertPropriete(oldebPropriete: OldebPropriete): Int =
        dsl.insertInto(OLDEB_PROPRIETE).set(dsl.newRecord(OLDEB_PROPRIETE, oldebPropriete)).execute()

    fun insertVisite(oldebVisite: OldebVisite): Int =
        dsl.insertInto(OLDEB_VISITE).set(dsl.newRecord(OLDEB_VISITE, oldebVisite)).execute()

    fun insertVisiteAnomalie(oldebVisiteAnomalie: OldebVisiteAnomalie): Int =
        dsl.insertInto(OLDEB_VISITE_ANOMALIE).set(dsl.newRecord(OLDEB_VISITE_ANOMALIE, oldebVisiteAnomalie)).execute()

    fun insertVisiteSuite(oldebVisiteSuite: OldebVisiteSuite): Int =
        dsl.insertInto(OLDEB_VISITE_SUITE).set(dsl.newRecord(OLDEB_VISITE_SUITE, oldebVisiteSuite)).execute()

    fun insertDocumentVisite(id: UUID, oldebVisiteId: UUID, documentId: UUID): Int =
        dsl.insertInto(OLDEB_VISITE_DOCUMENT)
            .set(OLDEB_VISITE_DOCUMENT.ID, id)
            .set(OLDEB_VISITE_DOCUMENT.OLDEB_VISITE_ID, oldebVisiteId)
            .set(OLDEB_VISITE_DOCUMENT.DOCUMENT_ID, documentId)
            .execute()

    /**
     * UPDATE
     */
    fun updateOldebGeometry(oldebId: UUID, geometry: org.locationtech.jts.geom.Geometry): Int =
        dsl.update(OLDEB).set(OLDEB.GEOMETRIE, geometry).where(OLDEB.ID.eq(oldebId)).execute()

    fun updateOldeb(oldeb: Oldeb): Int =
        dsl.update(OLDEB).set(dsl.newRecord(OLDEB, oldeb)).where(OLDEB.ID.eq(oldeb.oldebId)).execute()

    // /!\ On se base sur l'ID de l'OLD car NOT NULL UNIQUE
    fun updateLocataire(oldebLocataire: OldebLocataire): Int =
        dsl.update(OLDEB_LOCATAIRE).set(dsl.newRecord(OLDEB_LOCATAIRE, oldebLocataire)).where(OLDEB_LOCATAIRE.OLDEB_ID.eq(oldebLocataire.oldebLocataireOldebId)).execute()

    fun updateVisite(oldebVisite: OldebVisite): Int =
        dsl.update(OLDEB_VISITE).set(dsl.newRecord(OLDEB_VISITE, oldebVisite)).where(OLDEB_VISITE.ID.eq(oldebVisite.oldebVisiteId)).execute()

    fun updateVisiteSuite(oldebVisiteSuite: OldebVisiteSuite): Int =
        dsl.update(OLDEB_VISITE_SUITE).set(dsl.newRecord(OLDEB_VISITE_SUITE, oldebVisiteSuite)).where(OLDEB_VISITE_SUITE.ID.eq(oldebVisiteSuite.oldebVisiteSuiteId)).execute()

    /**
     * DELETE
     */
    fun deleteMissingSuite(oldebId: UUID, toKeep: List<UUID>?): Int =
        dsl.deleteFrom(OLDEB_VISITE_SUITE).where(OLDEB_VISITE_SUITE.OLDEB_VISITE_ID.`in`(dsl.select(OLDEB_VISITE.ID).from(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)))).and(OLDEB_VISITE_SUITE.ID.notIn(toKeep)).execute()

    fun deleteMissingVisite(oldebId: UUID, toKeep: List<UUID>?): Int =
        dsl.deleteFrom(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)).and(OLDEB_VISITE.ID.notIn(toKeep)).execute()

    fun deleteVisiteDocument(oldebVisiteId: UUID): Int =
        dsl.deleteFrom(OLDEB_VISITE_DOCUMENT).where(OLDEB_VISITE_DOCUMENT.OLDEB_VISITE_ID.`in`(oldebVisiteId)).execute()

    fun deleteMissingVisiteDocument(toDelete: List<UUID>?): Int =
        dsl.deleteFrom(OLDEB_VISITE_DOCUMENT).where(OLDEB_VISITE_DOCUMENT.DOCUMENT_ID.`in`(toDelete)).execute()

    fun deletePropriete(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_PROPRIETE).where(OLDEB_PROPRIETE.OLDEB_ID.eq(oldebId)).execute()

    fun deleteLocataire(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_LOCATAIRE).where(OLDEB_LOCATAIRE.OLDEB_ID.eq(oldebId)).execute()

    fun deleteSuite(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_VISITE_SUITE).where(OLDEB_VISITE_SUITE.OLDEB_VISITE_ID.`in`(dsl.select(OLDEB_VISITE.ID).from(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)))).execute()

    fun deleteAnomalie(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_VISITE_ANOMALIE).where(OLDEB_VISITE_ANOMALIE.OLDEB_VISITE_ID.`in`(dsl.select(OLDEB_VISITE.ID).from(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)))).execute()

    fun deleteOldebVisiteDocument(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_VISITE_DOCUMENT).where(OLDEB_VISITE_DOCUMENT.OLDEB_VISITE_ID.`in`(dsl.select(OLDEB_VISITE.ID).from(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)))).execute()

    fun deleteVisite(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_VISITE).where(OLDEB_VISITE.OLDEB_ID.eq(oldebId)).execute()

    fun deleteCaracteristique(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB_CARACTERISTIQUE).where(OLDEB_CARACTERISTIQUE.OLDEB_ID.eq(oldebId)).execute()

    fun deleteOldeb(oldebId: UUID): Int =
        dsl.deleteFrom(OLDEB).where(OLDEB.ID.eq(oldebId)).execute()
}
