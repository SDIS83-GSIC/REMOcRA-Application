package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Table
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.EvenementData
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.references.CRISE
import remocra.db.jooq.remocra.tables.references.CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.L_EVENEMENT_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE_CATEGORIE
import java.time.LocalDate
import java.util.UUID

class EvenementRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    private val criseCat: Table<*> = CRISE_CATEGORIE.`as`("criseCat")

    data class TypeEvenement(
        val typeEvenementId: UUID,
        val typeEvenementCode: String?,
        val typeEvenementLibelle: String,
        val typeEvenementGeometrie: TypeGeometry?,
    )

    data class SousTypeForMap(
        val criseCategorieId: UUID?,
        val criseCategorieCode: String?,
        val criseCategorieLibelle: String?,
        val listSousType: List<TypeEvenementForMap>?,
    )

    data class TypeEvenementForMap(
        val typeCriseCategorieId: UUID?,
        val typeCriseCategorieCode: String?,
        val typeCriseCategorieLibelle: String?,
        val typeCriseCategorieGeometrie: TypeGeometry?,
    )

    fun getAllEvents(criseId: UUID): Collection<Evenement> =
        dsl.select(
            EVENEMENT.ID,
            EVENEMENT.CRISE_ID,
            EVENEMENT.TAGS,
            EVENEMENT.ORIGINE,
            EVENEMENT.LIBELLE,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.IS_CLOSED,
            EVENEMENT.DESCRIPTION,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.TYPE_CRISE_CATEGORIE_ID.`as`("evenementTypeCriseId"),
            EVENEMENT.DATE_CONSTAT.`as`("evenementDateDebut"),
            EVENEMENT.STATUT,
            multiset(
                selectDistinct(
                    L_EVENEMENT_DOCUMENT.DOCUMENT_ID,
                    DOCUMENT.NOM_FICHIER,
                )
                    .from(L_EVENEMENT_DOCUMENT)
                    .join(DOCUMENT)
                    .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
                    .where(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(EVENEMENT.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    DocumentEvenementData(
                        documentId = r.value1() as UUID,
                        documentNomFichier = r.value2().toString(),
                    )
                }
            }.`as`("documents"),
        )
            .from(EVENEMENT)
            .where(EVENEMENT.CRISE_ID.eq(criseId))
            .fetchInto()

    fun getTypeAndSousType(criseId: UUID): Collection<SousTypeForMap> =
        dsl.select(
            CRISE_CATEGORIE.ID,
            CRISE_CATEGORIE.CODE,
            CRISE_CATEGORIE.LIBELLE,

            multiset(
                dsl.select(
                    TYPE_CRISE_CATEGORIE.ID,
                    TYPE_CRISE_CATEGORIE.CODE,
                    TYPE_CRISE_CATEGORIE.LIBELLE,
                    TYPE_CRISE_CATEGORIE.TYPE_GEOMETRIE,
                )
                    .from(TYPE_CRISE_CATEGORIE)
                    .where(
                        TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID.eq(
                            dsl.select(CRISE_CATEGORIE.ID)
                                .from(criseCat)
                                .where(criseCat.field(CRISE_CATEGORIE.LIBELLE)?.eq(CRISE_CATEGORIE.LIBELLE)),
                        ),
                    ),

            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()?.let {
                        TypeEvenementForMap(
                            typeCriseCategorieId = r.value1(),
                            typeCriseCategorieCode = r.value2(),
                            typeCriseCategorieLibelle = r.value3(),
                            typeCriseCategorieGeometrie = r.value4(),
                        )
                    }
                }
            }.`as`("listSousType"),

        )
            .from(CRISE_CATEGORIE)
            .join(L_TYPE_CRISE_CATEGORIE)
            .on(CRISE_CATEGORIE.ID.eq(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID))
            .join(CRISE)
            .on(CRISE.TYPE_CRISE_ID.eq(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID))
            .where(CRISE.ID.eq(criseId))
            .fetchInto()

    fun insertEvenementDocument(documentId: UUID, evenementId: UUID) =
        dsl.insertInto(L_EVENEMENT_DOCUMENT)
            .set(L_EVENEMENT_DOCUMENT.DOCUMENT_ID, documentId)
            .set(L_EVENEMENT_DOCUMENT.EVENEMENT_ID, evenementId)
            .execute()

    fun deleteEvenementDocument(documentsId: Collection<UUID>) =
        dsl.deleteFrom(L_EVENEMENT_DOCUMENT)
            .where(L_EVENEMENT_DOCUMENT.DOCUMENT_ID.`in`(documentsId))
            .execute()

    fun checkNumeroExists(evenementNumero: UUID): Boolean =
        dsl.fetchExists(dsl.select(EVENEMENT.ID).from(EVENEMENT).where(EVENEMENT.ID.eq(evenementNumero)))

    fun getTypeEventForSelect(): Collection<TypeEvenement> =
        dsl.select(
            TYPE_CRISE_CATEGORIE.ID.`as`("typeEvenementId"),
            TYPE_CRISE_CATEGORIE.CODE.`as`("typeEvenementCode"),
            TYPE_CRISE_CATEGORIE.LIBELLE.`as`("typeEvenementLibelle"),
            TYPE_CRISE_CATEGORIE.TYPE_GEOMETRIE.`as`("typeEvenementGeometrie"),
        )
            .from(TYPE_CRISE_CATEGORIE)
            .fetchInto()

    data class Evenement(
        val evenementId: UUID?,
        val evenementCriseId: UUID?,
        val evenementTag: String?,
        val evenementOrigine: String?,
        val evenementLibelle: String?,
        val evenementImportance: Int?,
        val evenementIsClosed: Boolean?,
        val evenementDescription: String?,
        val evenementDateCloture: LocalDate?,
        val evenementTypeCriseId: UUID?,
        val evenementDateDebut: LocalDate?,
        val documents: Collection<DocumentEvenementData>?,
        val evenementStatut: EvenementStatut,
    )

    data class DocumentEvenementData(
        val documentId: UUID,
        val documentNomFichier: String,
    )

    fun getEvenement(evenementId: UUID): Evenement =
        dsl.select(
            EVENEMENT.ID,
            EVENEMENT.CRISE_ID,
            EVENEMENT.TAGS,
            EVENEMENT.ORIGINE,
            EVENEMENT.LIBELLE,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.IS_CLOSED.`as`("evenementIsClosed"),
            EVENEMENT.DESCRIPTION,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.TYPE_CRISE_CATEGORIE_ID.`as`("evenementTypeCriseId"),
            EVENEMENT.DATE_CONSTAT.`as`("evenementDateDebut"),
            EVENEMENT.STATUT,
            multiset(
                selectDistinct(
                    L_EVENEMENT_DOCUMENT.DOCUMENT_ID,
                    DOCUMENT.NOM_FICHIER,
                )
                    .from(L_EVENEMENT_DOCUMENT)
                    .join(DOCUMENT)
                    .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
                    .where(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(EVENEMENT.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    DocumentEvenementData(
                        documentId = r.value1() as UUID,
                        documentNomFichier = r.value2().toString(),
                    )
                }
            }.`as`("documents"),
        )
            .from(EVENEMENT)
            .where(EVENEMENT.ID.eq(evenementId))
            .fetchSingleInto()

    fun add(evenementData: EvenementData): Int =
        // insérer dans les crises
        dsl.insertInto(
            EVENEMENT,
            EVENEMENT.ID,
            EVENEMENT.TYPE_CRISE_CATEGORIE_ID,
            EVENEMENT.LIBELLE,
            EVENEMENT.DESCRIPTION,
            EVENEMENT.ORIGINE,
            EVENEMENT.DATE_CONSTAT,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.TAGS,
            EVENEMENT.IS_CLOSED,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.GEOMETRIE,
            EVENEMENT.CRISE_ID,
        ).values(
            evenementData.evenementId,
            evenementData.evenementTypeId,
            evenementData.evenementLibelle,
            evenementData.evenementDescription,
            evenementData.evenementOrigine,
            evenementData.evenementDateConstat,
            evenementData.evenementImportance,
            evenementData.evenementTag,
            evenementData.evenementEstFerme,
            evenementData.evenementDateCloture,
            evenementData.evenementGeometrie,
            evenementData.evenementCriseId,
        ).execute()

    fun getDocumentByEvenementId(evenementId: UUID): Map<UUID?, Document> =
        dsl.select(DOCUMENT.ID, DOCUMENT)
            .from(DOCUMENT)
            .join(L_EVENEMENT_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
            .where(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(evenementId))
            .fetch { r -> r.get(DOCUMENT.ID) to r.into(Document::class.java) }
            .toMap()

    fun updateEvenement(
        element: EvenementData,
    ) =
        dsl.update(EVENEMENT)
            .set(EVENEMENT.TYPE_CRISE_CATEGORIE_ID, element.evenementTypeId)
            .set(EVENEMENT.LIBELLE, element.evenementLibelle)
            .set(EVENEMENT.DESCRIPTION, element.evenementDescription)
            .set(EVENEMENT.ORIGINE, element.evenementOrigine)
            .set(EVENEMENT.DATE_CONSTAT, element.evenementDateConstat)
            .set(EVENEMENT.IMPORTANCE, element.evenementImportance)
            .set(EVENEMENT.TAGS, element.evenementTag)
            .set(EVENEMENT.IS_CLOSED, element.evenementEstFerme)
            .set(EVENEMENT.DATE_CLOTURE, element.evenementDateCloture)
            .set(EVENEMENT.GEOMETRIE, element.evenementGeometrie)
            .set(EVENEMENT.CRISE_ID, element.evenementCriseId)
            .where(EVENEMENT.ID.eq(element.evenementId))
            .execute()
}
