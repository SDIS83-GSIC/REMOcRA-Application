package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.EvenementData
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.db.jooq.remocra.enums.EvenementStatutMode
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.references.CRISE
import remocra.db.jooq.remocra.tables.references.CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.L_EVENEMENT_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.time.ZonedDateTime
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

    data class Filter(
        val filterType: Set<UUID>? = null,
        val filterAuthor: Set<UUID>? = null,
        val filterStatut: EvenementStatut? = null,
        val filterImportance: Int? = null,
        val filterMessage: String? = null,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    filterType?.let { DSL.and(EVENEMENT.TYPE_CRISE_CATEGORIE_ID.`in`(it)) },
                    filterAuthor?.let { DSL.and(EVENEMENT.UTILISATEUR_ID.`in`(it)) },
                    filterStatut?.let { DSL.and(EVENEMENT.STATUT.eq(it)) },
                    filterImportance?.let { DSL.and(EVENEMENT.IMPORTANCE.eq(it)) },
                    filterMessage?.let { DSL.and(buildDateEventFilter(it)) },
                ),
            )

        private fun buildDateEventFilter(durationString: String): Condition {
            val now = ZonedDateTime.now()

            // Calculer la date limite en fonction de la durée passée en paramètre
            val filterDate = when (durationString) {
                "DIX_MINUTES" -> now.minusMinutes(10)
                "TRENTE_MINUTES" -> now.minusMinutes(30)
                "UNE_HEURE" -> now.minusHours(1)
                "VINGT_QUATRE_HEURES" -> now.minusDays(1)
                else -> return DSL.trueCondition()
            }

            return EVENEMENT.DATE_CONSTAT.greaterOrEqual(filterDate)
        }
    }

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

    data class UtilisateurFilter(
        val id: UUID?,
        val libelle: String?,
    )

    data class EvenementFilter(
        val evenementStatut: EvenementStatut?,
        val evenementTags: List<String>,
    )

    data class TypeEvenementFilter(
        val id: UUID?,
        val libelle: String?,
    )

    data class FilterEvent(
        val typeEvenement: Collection<TypeEvenementFilter>?,
        val evenement: Collection<EvenementFilter>?, // select all
        val utilisateur: Collection<UtilisateurFilter>?,
    )

    fun getTypeEventFromCrise(criseId: UUID, statut: EvenementStatutMode): Collection<FilterEvent> =
        dsl.select(
            multiset(
                selectDistinct(
                    TYPE_CRISE_CATEGORIE.ID.`as`("id"),
                    TYPE_CRISE_CATEGORIE.LIBELLE.`as`("libelle"),
                )
                    .from(TYPE_CRISE_CATEGORIE)
                    .join(EVENEMENT)
                    .on(EVENEMENT.TYPE_CRISE_CATEGORIE_ID.eq(TYPE_CRISE_CATEGORIE.ID))
                    .where(EVENEMENT.CRISE_ID.eq(criseId), EVENEMENT.STATUT_MODE.eq(statut)),
            ).`as`("typeEvenement").convertFrom { record ->
                record?.map { r ->
                    TypeEvenementFilter(
                        id = r.value1(),
                        libelle = r.value2(),
                    )
                }
            },
            multiset(
                selectDistinct(
                    EVENEMENT.STATUT,
                    EVENEMENT.TAGS,
                )
                    .from(EVENEMENT)
                    .where(
                        EVENEMENT.CRISE_ID.eq(criseId),
                        EVENEMENT.STATUT_MODE.eq(statut),
                    ),

            ).`as`("evenement").convertFrom { record ->
                record?.map { r ->
                    EvenementFilter(
                        evenementStatut = r.value1(),
                        evenementTags = r.value2().toString().split(",").map { it.trim() },
                    )
                }
            },
            multiset(
                selectDistinct(
                    UTILISATEUR.ID.`as`("id"),
                    UTILISATEUR.NOM.`as`("libelle"),
                )
                    .from(UTILISATEUR)
                    .join(EVENEMENT)
                    .on(EVENEMENT.UTILISATEUR_ID.eq(UTILISATEUR.ID))
                    .where(EVENEMENT.CRISE_ID.eq(criseId), EVENEMENT.STATUT_MODE.eq(statut)),
            ).`as`("utilisateur").convertFrom { record ->
                record?.map { r ->
                    UtilisateurFilter(
                        id = r.value1(),
                        libelle = r.value2(),
                    )
                }
            },
        ).fetchInto()

    fun getAllEvents(criseId: UUID, params: Filter? = null, dateDebExtraction: ZonedDateTime? = null, dateFinExtraction: ZonedDateTime? = null, state: EvenementStatutMode? = null): Collection<EvenementData> =
        dsl.select(
            EVENEMENT.ID,
            EVENEMENT.CRISE_ID,
            EVENEMENT.TAGS.`as`("evenementTag"),
            EVENEMENT.ORIGINE,
            EVENEMENT.LIBELLE,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.IS_CLOSED,
            EVENEMENT.DESCRIPTION,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.TYPE_CRISE_CATEGORIE_ID.`as`("evenementTypeId"),
            EVENEMENT.DATE_CONSTAT,
            EVENEMENT.STATUT,
            EVENEMENT.GEOMETRIE,
            EVENEMENT.UTILISATEUR_ID,
            EVENEMENT.STATUT_MODE,
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
            .and(params?.toCondition())
            .let {
                if (dateDebExtraction != null && dateFinExtraction != null) {
                    it.and(EVENEMENT.DATE_CONSTAT.between(dateDebExtraction, dateFinExtraction))
                } else {
                    it
                }
            }
            .let {
                if (state != null) {
                    if (state == EvenementStatutMode.ANTICIPATION) {
                        it.and(EVENEMENT.STATUT_MODE.`in`(EvenementStatutMode.OPERATIONNEL, EvenementStatutMode.ANTICIPATION))
                    } else {
                        it.and(EVENEMENT.STATUT_MODE.eq(state))
                    }
                } else {
                    it
                }
            }
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

    data class DocumentEvenementData(
        val documentId: UUID,
        val documentNomFichier: String,
    )

    fun getEventIdByCriseId(criseId: UUID): Collection<UUID> =
        dsl.select(
            EVENEMENT.ID,
        ).from(EVENEMENT)
            .where(EVENEMENT.CRISE_ID.eq(criseId))
            .fetchInto()

    fun getEvenement(evenementId: UUID): EvenementData =
        dsl.select(
            EVENEMENT.ID,
            EVENEMENT.CRISE_ID,
            EVENEMENT.TAGS.`as`("evenementTag"),
            EVENEMENT.ORIGINE,
            EVENEMENT.LIBELLE,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.IS_CLOSED.`as`("evenementEstFerme"),
            EVENEMENT.DESCRIPTION,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.TYPE_CRISE_CATEGORIE_ID.`as`("evenementTypeId"),
            EVENEMENT.DATE_CONSTAT,
            EVENEMENT.STATUT,
            EVENEMENT.GEOMETRIE,
            EVENEMENT.UTILISATEUR_ID,
            EVENEMENT.STATUT_MODE,
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
            EVENEMENT.UTILISATEUR_ID,
            EVENEMENT.STATUT,
            EVENEMENT.STATUT_MODE,
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
            evenementData.evenementUtilisateurId,
            evenementData.evenementStatut,
            evenementData.evenementStatutMode,
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
            .set(EVENEMENT.STATUT, element.evenementStatut)
            .set(EVENEMENT.UTILISATEUR_ID, element.evenementUtilisateurId)
            .set(EVENEMENT.STATUT_MODE, element.evenementStatutMode)
            .where(EVENEMENT.ID.eq(element.evenementId))
            .execute()
}
