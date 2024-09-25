package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.couverturehydraulique.enums.EtudeStatut
import remocra.db.jooq.couverturehydraulique.tables.pojos.PeiProjet
import remocra.db.jooq.couverturehydraulique.tables.references.BATIMENT
import remocra.db.jooq.couverturehydraulique.tables.references.ETUDE
import remocra.db.jooq.couverturehydraulique.tables.references.L_ETUDE_COMMUNE
import remocra.db.jooq.couverturehydraulique.tables.references.L_ETUDE_DOCUMENT
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.couverturehydraulique.tables.references.RESEAU
import remocra.db.jooq.couverturehydraulique.tables.references.TYPE_ETUDE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.usecases.couverturehydraulique.ImportDataCouvertureHydrauliqueUseCase
import java.time.Clock
import java.time.ZonedDateTime
import java.util.UUID

class CouvertureHydrauliqueRepository @Inject constructor(
    private val dsl: DSLContext,
    private val clock: Clock,
) {

    fun getEtudes(params: Params<Filter, Sort>, affiliatedOrganismeIds: Set<UUID>): Collection<EtudeComplete> =
        dsl.select(
            ETUDE.ID,
            TYPE_ETUDE.LIBELLE,
            TYPE_ETUDE.ID,
            ETUDE.NUMERO,
            ETUDE.LIBELLE,
            ETUDE.DESCRIPTION,
            ETUDE.STATUT,
            multiset(
                selectDistinct(COMMUNE.ID, COMMUNE.CODE_INSEE, COMMUNE.LIBELLE)
                    .from(COMMUNE)
                    .join(L_ETUDE_COMMUNE)
                    .on(L_ETUDE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
                    .where(L_ETUDE_COMMUNE.ETUDE_ID.eq(ETUDE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    CommuneSansGeometrie(
                        r.value1().let { it as UUID },
                        r.value2().toString(),
                        r.value3().toString(),
                    )
                }
            }.`as`("listeCommune"),
            ETUDE.DATE_MAJ,
        )
            .from(ETUDE)
            .join(TYPE_ETUDE)
            .on(ETUDE.TYPE_ETUDE_ID.eq(TYPE_ETUDE.ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .and(ETUDE.ORGANISME_ID.`in`(affiliatedOrganismeIds))
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(ETUDE.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getCountEtudes(filterBy: Filter?, affiliatedOrganismeIds: Set<UUID>): Int =
        dsl.selectCount()
            .from(ETUDE)
            .where(filterBy?.toCondition() ?: DSL.trueCondition())
            .and(ETUDE.ORGANISME_ID.`in`(affiliatedOrganismeIds))
            .fetchSingleInto()

    data class EtudeComplete(
        val etudeId: UUID,
        val typeEtudeLibelle: String,
        val typeEtudeId: UUID,
        val etudeNumero: String,
        val etudeLibelle: String,
        val etudeDescription: String?,
        val listeCommune: Collection<CommuneSansGeometrie>,
        val etudeStatut: EtudeStatut,
        val etudeDateMaj: ZonedDateTime?,
    )
    data class CommuneSansGeometrie(
        val communeId: UUID,
        val communeCodeInsee: String,
        val communeLibelle: String,
    )

    data class Filter(
        val typeEtudeId: UUID?,
        val etudeNumero: String?,
        val etudeLibelle: String?,
        val etudeDescription: String?,
        val etudeStatut: EtudeStatut?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    typeEtudeId?.let { DSL.and(ETUDE.TYPE_ETUDE_ID.eq(it)) },
                    etudeNumero?.let { DSL.and(ETUDE.NUMERO.contains(it)) },
                    etudeLibelle?.let { DSL.and(ETUDE.LIBELLE.contains(it)) },
                    etudeDescription?.let { DSL.and(ETUDE.DESCRIPTION.contains(it)) },
                    etudeStatut?.let { DSL.and(ETUDE.STATUT.eq(it)) },
                ),
            )
    }

    data class Sort(
        val typeEtudeLibelle: Int?,
        val etudeNumero: Int?,
        val etudeLibelle: Int?,
        val etudeDescription: Int?,
        val etudeStatut: Int?,
        val etudeDateMaj: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            TYPE_ETUDE.LIBELLE.getSortField(typeEtudeLibelle),
            ETUDE.NUMERO.getSortField(etudeNumero),
            ETUDE.LIBELLE.getSortField(etudeLibelle),
            ETUDE.DESCRIPTION.getSortField(etudeDescription),
            ETUDE.STATUT.getSortField(etudeStatut),
            ETUDE.DATE_MAJ.getSortField(etudeDateMaj),
        )
    }

    fun getTypeEtudes(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            TYPE_ETUDE.ID.`as`("id"),
            TYPE_ETUDE.CODE.`as`("code"),
            TYPE_ETUDE.LIBELLE.`as`("libelle"),
        ).from(TYPE_ETUDE).fetchInto()

    fun getEtude(etudeId: UUID): EtudeUpsert =
        dsl.select(
            ETUDE.ID,
            TYPE_ETUDE.ID,
            ETUDE.NUMERO,
            ETUDE.LIBELLE,
            ETUDE.DESCRIPTION,
            multiset(
                selectDistinct(L_ETUDE_COMMUNE.COMMUNE_ID)
                    .from(L_ETUDE_COMMUNE)
                    .where(L_ETUDE_COMMUNE.ETUDE_ID.eq(ETUDE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1().let { it as UUID }
                }
            }.`as`("listeCommuneId"),
            multiset(
                selectDistinct(
                    L_ETUDE_DOCUMENT.DOCUMENT_ID,
                    DOCUMENT.NOM_FICHIER,
                    L_ETUDE_DOCUMENT.LIBELLE,
                )
                    .from(L_ETUDE_DOCUMENT)
                    .join(DOCUMENT)
                    .on(DOCUMENT.ID.eq(L_ETUDE_DOCUMENT.DOCUMENT_ID))
                    .where(L_ETUDE_DOCUMENT.ETUDE_ID.eq(ETUDE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    DocumentEtudeData(
                        documentId = r.value1() as UUID,
                        documentNomFichier = r.value2().toString(),
                        etudeDocumentLibelle = r.value3().toString(),
                    )
                }
            }.`as`("documents"),
        ).from(ETUDE)
            .join(TYPE_ETUDE)
            .on(TYPE_ETUDE.ID.eq(ETUDE.TYPE_ETUDE_ID))
            .where(ETUDE.ID.eq(etudeId))
            .fetchSingleInto()

    data class EtudeUpsert(
        val etudeId: UUID,
        val typeEtudeId: UUID,
        val etudeNumero: String,
        val etudeLibelle: String,
        val etudeDescription: String?,
        val listeCommuneId: Collection<UUID>,
        val documents: Collection<DocumentEtudeData>?,
    )

    data class DocumentEtudeData(
        val documentId: UUID,
        val documentNomFichier: String,
        val etudeDocumentLibelle: String,
    )

    fun insertEtudeDocument(documentId: UUID, etudeId: UUID, lEtudeDocumentLibelle: String?) =
        dsl.insertInto(L_ETUDE_DOCUMENT)
            .set(L_ETUDE_DOCUMENT.DOCUMENT_ID, documentId)
            .set(L_ETUDE_DOCUMENT.ETUDE_ID, etudeId)
            .set(L_ETUDE_DOCUMENT.LIBELLE, lEtudeDocumentLibelle)
            .execute()

    fun deleteEtudeDocument(documentsId: Collection<UUID>) =
        dsl.deleteFrom(L_ETUDE_DOCUMENT)
            .where(L_ETUDE_DOCUMENT.DOCUMENT_ID.`in`(documentsId))
            .execute()

    fun updateEtudeDocument(documentId: UUID, lEtudeDocumentLibelle: String?) =
        dsl.update(L_ETUDE_DOCUMENT)
            .set(L_ETUDE_DOCUMENT.LIBELLE, lEtudeDocumentLibelle)
            .where(L_ETUDE_DOCUMENT.DOCUMENT_ID.eq(documentId))
            .execute()

    fun updateEtude(etudeId: UUID, typeEtudeId: UUID, etudeNumero: String, etudeLibelle: String, etudeDescription: String?) =
        dsl.update(ETUDE)
            .set(ETUDE.TYPE_ETUDE_ID, typeEtudeId)
            .set(ETUDE.NUMERO, etudeNumero)
            .set(ETUDE.LIBELLE, etudeLibelle)
            .set(ETUDE.DESCRIPTION, etudeDescription)
            .set(ETUDE.DATE_MAJ, ZonedDateTime.now(clock))
            .where(ETUDE.ID.eq(etudeId))
            .execute()

    fun deleteLEtudeCommune(etudeId: UUID) =
        dsl.deleteFrom(L_ETUDE_COMMUNE)
            .where(L_ETUDE_COMMUNE.ETUDE_ID.eq(etudeId))
            .execute()

    fun insertLEtudeCommune(etudeId: UUID, listeCommuneId: Collection<UUID>) =
        dsl.batch(
            listeCommuneId.map {
                DSL.insertInto(L_ETUDE_COMMUNE)
                    .set(L_ETUDE_COMMUNE.ETUDE_ID, etudeId)
                    .set(L_ETUDE_COMMUNE.COMMUNE_ID, it)
            },
        )
            .execute()

    fun insertEtude(
        etudeId: UUID,
        typeEtudeId: UUID,
        etudeNumero: String,
        etudeLibelle: String,
        etudeDescription: String?,
        etudeOrganismeId: UUID,
    ) =
        dsl.insertInto(ETUDE)
            .set(ETUDE.ID, etudeId)
            .set(ETUDE.TYPE_ETUDE_ID, typeEtudeId)
            .set(ETUDE.NUMERO, etudeNumero)
            .set(ETUDE.LIBELLE, etudeLibelle)
            .set(ETUDE.DESCRIPTION, etudeDescription)
            .set(ETUDE.ORGANISME_ID, etudeOrganismeId)
            .set(ETUDE.DATE_MAJ, ZonedDateTime.now(clock))
            .set(ETUDE.STATUT, EtudeStatut.EN_COURS)
            .execute()

    fun deleteReseauByEtudeId(etudeId: UUID) =
        dsl.deleteFrom(RESEAU)
            .where(RESEAU.ETUDE_ID.eq(etudeId))
            .execute()

    fun insertReseau(etudeId: UUID, listReseau: List<ImportDataCouvertureHydrauliqueUseCase.Reseau>) =
        dsl.batch(
            listReseau.map {
                dsl.insertInto(RESEAU)
                    .set(RESEAU.ID, UUID.randomUUID())
                    .set(RESEAU.ETUDE_ID, etudeId)
                    .set(RESEAU.GEOMETRIE, it.reseauGeometrie)
                    .set(RESEAU.TRAVERSABLE, it.reseauTraversable)
                    .set(RESEAU.SENS_UNIQUE, it.reseauSensUnique)
                    .set(RESEAU.NIVEAU, it.reseauNiveau)
            },
        )
            .execute()

    fun deleteBatimentByEtudeId(etudeId: UUID) =
        dsl.deleteFrom(BATIMENT)
            .where(BATIMENT.ETUDE_ID.eq(etudeId))
            .execute()

    fun insertBatiment(etudeId: UUID, listBatiment: List<ImportDataCouvertureHydrauliqueUseCase.Batiment>) =
        dsl.batch(
            listBatiment.map {
                dsl.insertInto(BATIMENT)
                    .set(BATIMENT.ID, UUID.randomUUID())
                    .set(BATIMENT.ETUDE_ID, etudeId)
                    .set(BATIMENT.GEOMETRIE, it.batimentGeometrie)
            },
        )
            .execute()

    fun deletePeiProjetByEtudeId(etudeId: UUID) =
        dsl.deleteFrom(PEI_PROJET)
            .where(PEI_PROJET.ETUDE_ID.eq(etudeId))
            .execute()

    fun insertPeiProjet(listPeiProjet: List<PeiProjet>) =
        dsl.batch(
            listPeiProjet.map { dsl.insertInto(PEI_PROJET).set(dsl.newRecord(PEI_PROJET, it)) },
        )
            .execute()

    fun cloreEtude(etudeId: UUID) =
        dsl.update(ETUDE)
            .set(ETUDE.STATUT, EtudeStatut.TERMINEE)
            .where(ETUDE.ID.eq(etudeId))
            .execute()
}
