package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Geometry
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.locationtech.jts.geom.MultiPolygon
import remocra.data.CouchesData
import remocra.data.CouchesWms
import remocra.data.CriseData
import remocra.data.Params
import remocra.data.TypeToponymies
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.EvenementSousCategorie
import remocra.db.jooq.remocra.tables.references.CADASTRE_SECTION
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.CRISE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT_SOUS_CATEGORIE
import remocra.db.jooq.remocra.tables.references.LIEU_DIT
import remocra.db.jooq.remocra.tables.references.L_COUCHE_CRISE
import remocra.db.jooq.remocra.tables.references.L_COUCHE_MODULE
import remocra.db.jooq.remocra.tables.references.L_CRISE_COMMUNE
import remocra.db.jooq.remocra.tables.references.L_CRISE_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_EVENEMENT_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_TOPONYMIE_CRISE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.TOPONYMIE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE
import remocra.db.jooq.remocra.tables.references.TYPE_TOPONYMIE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.utils.ST_Multi
import remocra.utils.ST_Union
import remocra.utils.ST_Within
import java.time.ZonedDateTime
import java.util.UUID

class CriseRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getCouchesWms(): Collection<CouchesWms> =
        dsl.select(
            COUCHE.ID,
            COUCHE.CODE,
            COUCHE.LIBELLE,
        )
            .from(COUCHE)
            .join(L_COUCHE_MODULE)
            .on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID))
            .where(L_COUCHE_MODULE.MODULE_TYPE.eq(TypeModule.CRISE))
            .and(COUCHE.ACTIVE.eq(true))
            .fetchInto()

    fun deleteCriseDocuments(documentsId: UUID) {
        // Première suppression
        dsl.deleteFrom(L_CRISE_DOCUMENT)
            .where(L_CRISE_DOCUMENT.DOCUMENT_ID.eq(documentsId))
            .execute()

        // Deuxième suppression
        dsl.deleteFrom(L_EVENEMENT_DOCUMENT)
            .where(L_EVENEMENT_DOCUMENT.DOCUMENT_ID.eq(documentsId))
            .execute()
    }

    fun insertCriseDocument(documentId: UUID, criseId: UUID, geometry: org.locationtech.jts.geom.Geometry? = null) =
        dsl.insertInto(L_CRISE_DOCUMENT)
            .set(L_CRISE_DOCUMENT.DOCUMENT_ID, documentId)
            .set(L_CRISE_DOCUMENT.CRISE_ID, criseId)
            .set(L_CRISE_DOCUMENT.DOCUMENT_GEOMETRIE, geometry)
            .execute()

    fun getCrises(params: Params<FilterCrise, SortCrise>): Collection<CriseComplete> =
        dsl.select(
            CRISE.ID,
            CRISE.LIBELLE,
            CRISE.DESCRIPTION,
            CRISE.DATE_DEBUT,
            CRISE.DATE_FIN,
            CRISE.STATUT_TYPE,
            TYPE_CRISE.LIBELLE.`as`("typeCriseLibelle"),
            multiset(
                selectDistinct(COMMUNE.LIBELLE)
                    .from(COMMUNE)
                    .join(L_CRISE_COMMUNE)
                    .on(L_CRISE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
                    .where(L_CRISE_COMMUNE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1().toString()
                }
            }.`as`("listeCommune"),
        )
            .from(CRISE)
            .join(TYPE_CRISE)
            .on(CRISE.TYPE_CRISE_ID.eq(TYPE_CRISE.ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(CRISE.STATUT_TYPE.sortAsc(TypeCriseStatut.EN_COURS), CRISE.DATE_DEBUT.desc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getCountDocumentFromCrise(criseId: UUID, filterBy: FilterCrise?): Int {
        val countFromCrise = dsl.selectCount()
            .from(DOCUMENT)
            .join(L_CRISE_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_CRISE_DOCUMENT.DOCUMENT_ID))
            .where(L_CRISE_DOCUMENT.CRISE_ID.eq(criseId))
            .and(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto<Int>()

        val countFromEvenement = dsl.selectCount()
            .from(DOCUMENT)
            .join(L_EVENEMENT_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
            .join(EVENEMENT)
            .on(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(EVENEMENT.ID))
            .where(EVENEMENT.CRISE_ID.eq(criseId))
            .fetchSingleInto<Int>()

        return countFromCrise + countFromEvenement
    }

    fun getCountCrises(filterBy: FilterCrise?): Int =
        dsl.selectCount()
            .from(CRISE)
            .where(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto()

    data class CriseComplete(
        val criseId: UUID,
        val criseLibelle: String?,
        val criseDescription: String?,
        val criseDateDebut: ZonedDateTime?,
        val criseDateFin: ZonedDateTime?,
        val criseStatutType: String?,
        val typeCriseLibelle: String?,
        var listeCommune: Collection<String>?,
    )

    data class TypeCriseComplete(
        val criseId: String?,
        val criseNom: String?,
    )

    data class FilterCrise(
        val criseLibelle: String?,
        val criseDescription: String?,
        val criseStatutType: TypeCriseStatut?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    criseLibelle?.let { DSL.and(CRISE.LIBELLE.contains(it)) },
                    criseDescription?.let { DSL.and(CRISE.DESCRIPTION.contains(it)) },
                    criseStatutType?.let { DSL.and(CRISE.STATUT_TYPE.eq(it)) },
                ),
            )
    }

    data class SortDocs(
        val documentDate: Int?,
        val documentNomFichier: Int,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            DOCUMENT.DATE.getSortField(documentDate),
            DOCUMENT.NOM_FICHIER.getSortField(documentNomFichier),
        )
    }

    data class SortCrise(
        val criseLibelle: Int?,
        val criseDescription: Int?,
        val criseDateDebut: Int?,
        val criseDateFin: Int?,
        val criseStatutType: Int?,
        val typeCriseLibelle: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            TYPE_CRISE.LIBELLE.getSortField(typeCriseLibelle),
            CRISE.LIBELLE.getSortField(criseLibelle),
            CRISE.DESCRIPTION.getSortField(criseDescription),
            CRISE.DATE_DEBUT.getSortField(criseDateDebut),
            CRISE.DATE_FIN.getSortField(criseDateFin),
            CRISE.STATUT_TYPE.getSortField(criseStatutType),

        )
    }

    data class CriseMerge(
        val criseId: UUID,
        val criseLibelle: String?,
        val criseDateDebut: ZonedDateTime?,
    )

    fun getCriseForMerge(): Collection<CriseMerge> =
        dsl.select(CRISE.ID, CRISE.LIBELLE, CRISE.DATE_DEBUT)
            .from(CRISE)
            .where(CRISE.STATUT_TYPE.eq(TypeCriseStatut.EN_COURS))
            .orderBy(CRISE.LIBELLE)
            .fetchInto()

    fun getCriseForSelect(): Collection<TypeCriseComplete> =
        dsl.select(TYPE_CRISE.ID.`as`("criseId"), TYPE_CRISE.LIBELLE.`as`("criseNom"))
            .from(TYPE_CRISE)
            .orderBy(TYPE_CRISE.LIBELLE)
            .fetchInto()

    fun createCrise(criseData: CriseData): Int =
        // insérer dans les crises
        dsl.insertInto(
            CRISE,
            CRISE.ID,
            CRISE.LIBELLE,
            CRISE.DESCRIPTION,
            CRISE.DATE_DEBUT,
            CRISE.DATE_FIN,
            CRISE.TYPE_CRISE_ID,
            CRISE.STATUT_TYPE,
        ).values(
            criseData.criseId,
            criseData.criseLibelle,
            criseData.criseDescription,
            criseData.criseDateDebut,
            criseData.criseDateFin,
            criseData.criseTypeCriseId,
            criseData.criseStatutType,
        ).execute()

    fun insertLCriseCommune(criseId: UUID, listeCommuneId: Collection<UUID>?) =
        dsl.batch(
            listeCommuneId?.map {
                DSL.insertInto(L_CRISE_COMMUNE)
                    .set(L_CRISE_COMMUNE.CRISE_ID, criseId)
                    .set(L_CRISE_COMMUNE.COMMUNE_ID, it)
            },
        )
            .execute()

    fun insertLCoucheCrise(criseId: UUID, couchesWms: Collection<CouchesData>?) =
        dsl.batch(
            couchesWms?.mapNotNull {
                it.coucheId?.let { coucheId ->
                    DSL.insertInto(L_COUCHE_CRISE)
                        .set(L_COUCHE_CRISE.COUCHE_ID, coucheId)
                        .set(L_COUCHE_CRISE.CRISE_ID, criseId)
                        .set(L_COUCHE_CRISE.OPERATIONNEL, it.operationnel)
                        .set(L_COUCHE_CRISE.ANTICIPATION, it.anticipation)
                }
            },
        ).execute()

    data class CriseUpsert(
        val criseId: UUID,
        val criseLibelle: String?,
        val criseDescription: String?,
        val criseDateDebut: ZonedDateTime,
        val criseDateFin: ZonedDateTime?,
        val criseStatutType: TypeCriseStatut,
        val typeCriseId: UUID,
        var listeCommuneId: Collection<UUID>?,
        var listeToponymieId: Collection<UUID>?,
        val couchesWMS: Collection<CouchesData>?,
    )

    fun getCriseGeometryUnion(criseId: UUID): Geometry? =
        dsl.select(
            ST_Union(COMMUNE.GEOMETRIE),
        )
            .from(CRISE)
            .join(L_CRISE_COMMUNE)
            .on(CRISE.ID.eq(L_CRISE_COMMUNE.CRISE_ID))
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(L_CRISE_COMMUNE.COMMUNE_ID))
            .where(CRISE.ID.eq(criseId))
            .fetchSingleInto()

    fun getCrise(criseId: UUID): CriseUpsert =
        dsl.select(
            CRISE.ID,
            CRISE.LIBELLE,
            CRISE.DESCRIPTION,
            CRISE.DATE_DEBUT,
            CRISE.DATE_FIN,
            CRISE.STATUT_TYPE,
            TYPE_CRISE.ID,
            multiset(
                selectDistinct(COMMUNE.ID)
                    .from(COMMUNE)
                    .join(L_CRISE_COMMUNE)
                    .on(L_CRISE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
                    .where(L_CRISE_COMMUNE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1() as UUID
                }
            }.`as`("listeCommuneId"),
            multiset(
                selectDistinct(
                    COUCHE.ID,
                    L_COUCHE_CRISE.ANTICIPATION.`as`("anticipation"),
                    L_COUCHE_CRISE.OPERATIONNEL.`as`("operationnel"),
                    COUCHE.LIBELLE.`as`("libelle"),
                    COUCHE.CODE.`as`("code"),
                )
                    .from(COUCHE)
                    .join(L_COUCHE_CRISE)
                    .on(L_COUCHE_CRISE.COUCHE_ID.eq(COUCHE.ID))
                    .where(L_COUCHE_CRISE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    CouchesData(
                        coucheId = r.value1(),
                        anticipation = r.value2() as Boolean,
                        operationnel = r.value3() as Boolean,
                        libelle = r.value4(),
                        code = r.value5(),
                    )
                }
            }.`as`("couchesWMS"),
            multiset(
                selectDistinct(TYPE_TOPONYMIE.ID)
                    .from(TYPE_TOPONYMIE)
                    .join(L_TOPONYMIE_CRISE)
                    .on(L_TOPONYMIE_CRISE.TYPE_TOPONYMIE_ID.eq(TYPE_TOPONYMIE.ID))
                    .where(L_TOPONYMIE_CRISE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1() as UUID
                }
            }.`as`("listeToponymieId"),
        ).from(CRISE)
            .join(TYPE_CRISE)
            .on(CRISE.TYPE_CRISE_ID.eq(TYPE_CRISE.ID))
            .where(CRISE.ID.eq(criseId))
            .fetchSingleInto()

    fun updateCrise(
        criseId: UUID,
        criseLibelle: String?,
        criseDescription: String?,
        criseDateDebut: ZonedDateTime?,
        criseDateFin: ZonedDateTime?,
        criseTypeCriseId: UUID?,
        criseStatutType: TypeCriseStatut,
    ) =
        dsl.update(CRISE)
            .set(CRISE.LIBELLE, criseLibelle)
            .set(CRISE.DESCRIPTION, criseDescription)
            .set(CRISE.DATE_DEBUT, criseDateDebut)
            .set(CRISE.DATE_FIN, criseDateFin)
            .set(CRISE.TYPE_CRISE_ID, criseTypeCriseId)
            .set(CRISE.STATUT_TYPE, criseStatutType)
            .where(CRISE.ID.eq(criseId))
            .execute()

    fun deleleteLCriseCommune(criseId: UUID) =
        dsl.deleteFrom(L_CRISE_COMMUNE)
            .where(L_CRISE_COMMUNE.CRISE_ID.eq(criseId))
            .execute()

    fun deleteLToponymieCrise(criseId: UUID) =
        dsl.deleteFrom(L_TOPONYMIE_CRISE)
            .where(L_TOPONYMIE_CRISE.CRISE_ID.eq(criseId))
            .execute()

    fun deleteLCoucheCrise(criseId: UUID) =
        dsl.deleteFrom(L_COUCHE_CRISE)
            .where(L_COUCHE_CRISE.CRISE_ID.eq(criseId))
            .execute()

    fun cloreCrise(criseId: UUID, criseDateFin: ZonedDateTime?) =
        dsl.update(CRISE)
            .set(CRISE.STATUT_TYPE, TypeCriseStatut.TERMINEE)
            .set(CRISE.DATE_FIN, criseDateFin)
            .where(CRISE.ID.eq(criseId))
            .execute()

    data class CriseDocs(
        val documentId: UUID,
        val documentDate: ZonedDateTime?,
        val documentRepertoire: String?,
        val documentNomFichier: String?,
        val documentGeometrie: org.locationtech.jts.geom.Geometry?,
        val type: String?,
    )

    /**
     * Récupère les documents associés à une crise ou à un événement avec un type d'origine qui permet de différencier les documents issus d'une crise ou d'un événement.
     *
     * @param criseId L'ID de la crise pour filtrer les documents associés.
     * @return Une liste de documents
     */
    fun getAllDocumentsFromCrise(criseId: UUID, params: Params<FilterCrise, SortDocs>?): Collection<CriseDocs> =
        dsl.select(
            DOCUMENT.ID,
            DOCUMENT.NOM_FICHIER,
            DOCUMENT.DATE,
            DOCUMENT.REPERTOIRE,
            L_CRISE_DOCUMENT.DOCUMENT_GEOMETRIE.`as`("documentGeometrie"),
            DSL.case_()
                .`when`(L_CRISE_DOCUMENT.CRISE_ID.isNotNull(), DSL.`val`("Crise"))
                .`when`(EVENEMENT.CRISE_ID.isNotNull(), DSL.`val`("Évènement"))
                .`as`("type"),
        )
            .from(DOCUMENT)
            .leftJoin(L_CRISE_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_CRISE_DOCUMENT.DOCUMENT_ID))
            .leftJoin(L_EVENEMENT_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
            .leftJoin(EVENEMENT)
            .on(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(EVENEMENT.ID))
            .where(
                L_CRISE_DOCUMENT.CRISE_ID.eq(criseId)
                    .or(EVENEMENT.CRISE_ID.eq(criseId)),
            )
            .and(params?.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params?.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(DOCUMENT.DATE))
            .limit(params?.limit)
            .offset(params?.offset)
            .fetchInto()

    fun getToponymiesByCrise(criseId: UUID): Collection<TypeToponymies> =
        dsl.select(
            TYPE_TOPONYMIE.ID,
            TYPE_TOPONYMIE.LIBELLE,
            TYPE_TOPONYMIE.CODE,
        )
            .from(TYPE_TOPONYMIE)
            .join(L_TOPONYMIE_CRISE)
            .on(TOPONYMIE.ID.eq(L_TOPONYMIE_CRISE.TYPE_TOPONYMIE_ID))
            .where(L_TOPONYMIE_CRISE.CRISE_ID.eq(criseId))
            .fetchInto()

    /**
     * listIdToponymiesCrise : types de toponymie que l'utilisateur a sélectionnés.
     * Récupère les types sélectionnés par l'utilisateur et les classe en protégés / non protégés
     */
    fun getSelectedTypes(listIdToponymiesCrise: Collection<UUID>?, protege: Boolean): Collection<SelectedToponymieTypes> =
        dsl.select(
            TYPE_TOPONYMIE.ID,
            TYPE_TOPONYMIE.CODE,
            TYPE_TOPONYMIE.PROTECTED,
            TYPE_TOPONYMIE.ACTIF,
        )
            .from(TYPE_TOPONYMIE)
            .where((TYPE_TOPONYMIE.ID.`in`(listIdToponymiesCrise)), TYPE_TOPONYMIE.PROTECTED.eq(protege))
            .fetchInto()

    /**
     * Génère la requête pour les types non protégés dans la table `toponymie`
     */
    fun getToponymiesNonProtegesQuery(nonProteges: Collection<SelectedToponymieTypes>, globalGeometry: Field<org.locationtech.jts.geom.Geometry?>, libelle: String): Collection<ToponymieResult> =
        dsl.select(TOPONYMIE.ID, TOPONYMIE.LIBELLE, TOPONYMIE.GEOMETRIE)
            .from(TOPONYMIE)
            .where(
                TOPONYMIE.TYPE_TOPONYMIE_ID.`in`(nonProteges.map { it.typeToponymieId })
                    .and(ST_Within(TOPONYMIE.GEOMETRIE, globalGeometry)),
            )
            .and(TOPONYMIE.LIBELLE.likeIgnoreCase("%$libelle%"))
            .fetchInto()

    /**
     * Génère la requête pour les types protégés
     */
    fun getToponymiesProtegesQuery(proteges: Collection<SelectedToponymieTypes>, globalGeometry: Field<org.locationtech.jts.geom.Geometry?>, libelleName: String): Collection<ToponymieResult> {
        val typeToTableMapping = mapOf(
            "COMMUNE" to Triple(COMMUNE.ID, COMMUNE.LIBELLE, COMMUNE.GEOMETRIE), // permet de regrouper trois valeurs ensemble
            "LIEU_DIT" to Triple(LIEU_DIT.ID, LIEU_DIT.LIBELLE, LIEU_DIT.GEOMETRIE),
            "PEI" to Triple(PEI.ID, PEI.COMPLEMENT_ADRESSE, PEI.GEOMETRIE),
            "CADASTRE" to Triple(CADASTRE_SECTION.ID, CADASTRE_SECTION.NUMERO, CADASTRE_SECTION.GEOMETRIE),
            "ROUTE" to Triple(VOIE.ID, VOIE.LIBELLE, VOIE.GEOMETRIE),
        )

        return proteges
            .filter { it.typeToponymieActif == true }
            .mapNotNull { typeToTableMapping[it.typeToponymieCode] }
            .flatMap { (id, libelle, geometrie) ->
                dsl.select(id.`as`("toponymieId"), libelle.`as`("toponymieLibelle"), geometrie.`as`("toponymieGeometrie"))
                    .from(id.table)
                    .where(ST_Within(geometrie, globalGeometry))
                    .and(libelle.likeIgnoreCase("%$libelleName%"))
                    .fetchInto()
            } // flatMap → retourne liste unique des élèments
    }

    data class ToponymieResult(
        val toponymieId: UUID,
        val toponymieLibelle: String?,
        val toponymieGeometrie: org.locationtech.jts.geom.Geometry?,
    )

    data class SelectedToponymieTypes(
        val typeToponymieId: UUID,
        val typeToponymieCode: String?,
        val typeToponymieProtected: Boolean?,
        val typeToponymieActif: Boolean?,
    )

    fun getCouchesByCrise(criseId: UUID): Collection<CouchesData> =
        dsl.select(
            COUCHE.ID,
            L_COUCHE_CRISE.ANTICIPATION.`as`("anticipation"),
            L_COUCHE_CRISE.OPERATIONNEL.`as`("operationnel"),
            COUCHE.LIBELLE.`as`("libelle"),
            COUCHE.CODE.`as`("code"),
        )
            .from(COUCHE)
            .join(L_COUCHE_CRISE)
            .on(L_COUCHE_CRISE.COUCHE_ID.eq(COUCHE.ID))
            .where(L_COUCHE_CRISE.CRISE_ID.eq(criseId))
            .fetchInto()

    fun getCriseCommuneGeometrie(criseId: UUID): Collection<MultiPolygon> =
        dsl.select(ST_Multi(COMMUNE.GEOMETRIE))
            .from(L_CRISE_COMMUNE)
            .join(COMMUNE).on(L_CRISE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
            .where(L_CRISE_COMMUNE.CRISE_ID.eq(criseId))
            .fetchInto()

    fun getEvenementSousCategories(): Collection<EvenementSousCategorie> =
        dsl.selectFrom(EVENEMENT_SOUS_CATEGORIE).fetchInto()
}
