package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.locationtech.jts.geom.Geometry
import remocra.data.EvenementData
import remocra.data.Params
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.db.jooq.remocra.enums.EvenementStatutMode
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.references.CRISE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT_CATEGORIE
import remocra.db.jooq.remocra.tables.references.EVENEMENT_SOUS_CATEGORIE
import remocra.db.jooq.remocra.tables.references.L_EVENEMENT_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.collections.isNullOrEmpty

class EvenementRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    private val criseCat: Table<*> = EVENEMENT_CATEGORIE.`as`("criseCat")

    data class EvenementSousCategorie(
        val evenementSousCategorieId: UUID,
        val evenementSousCategorieCode: String?,
        val evenementSousCategorieLibelle: String,
        val evenementSousCategorieGeometrie: TypeGeometry?,
    )

    data class Filter(
        val filterType: Set<UUID>? = null,
        val filterAuthor: Set<UUID>? = null,
        val filterStatut: EvenementStatut? = null,
        val filterImportance: Int? = null,
        val filterMessage: String? = null,
        val filterTags: Set<String>? = null,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    filterType?.takeIf { it.isNotEmpty() }?.let { DSL.and(EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID.`in`(it)) },
                    filterAuthor?.takeIf { it.isNotEmpty() }?.let { DSL.and(EVENEMENT.UTILISATEUR_ID.`in`(it)) },
                    filterStatut?.let { DSL.and(EVENEMENT.STATUT.eq(it)) },
                    filterImportance?.let { DSL.and(EVENEMENT.IMPORTANCE.eq(it)) },
                    filterMessage?.takeIf { it.isNotEmpty() }?.let { DSL.and(buildDateEventFilter(it)) },
                    filterTags?.takeIf { it.isNotEmpty() }?.let { tags ->
                        DSL.and(tags.map { tag -> EVENEMENT.TAGS.containsIgnoreCaseUnaccent(tag) })
                    },
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
        val evenementCategorieId: UUID?,
        val evenementCategorieCode: String?,
        val evenementCategorieLibelle: String?,
        val listSousType: List<TypeEvenementForMap>?,
    )

    data class TypeEvenementForMap(
        val evenementSousCategorieId: UUID?,
        val evenementSousCategorieCode: String?,
        val evenementSousCategorieLibelle: String?,
        val evenementSousCategorieGeometrie: TypeGeometry?,
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

    data class TypeSousType(
        val evenementSousCategorieId: UUID?,
        val evenementSousCategorieCode: String?,
        val evenementSousCategorieLibelle: String?,
        val evenementSousCategorieTypeGeometrie: TypeGeometry?,
        val evenementCategorieLibelle: String?,
        val evenementSousCategorieActif: Boolean,
        val evenementsDependants: Boolean,
    )

    fun getTypeEventFromCrise(criseId: UUID, statut: EvenementStatutMode): Collection<FilterEvent> =
        dsl.select(
            multiset(
                selectDistinct(
                    EVENEMENT_SOUS_CATEGORIE.ID.`as`("id"),
                    EVENEMENT_SOUS_CATEGORIE.LIBELLE.`as`("libelle"),
                )
                    .from(EVENEMENT_SOUS_CATEGORIE)
                    .join(EVENEMENT)
                    .on(EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID.eq(EVENEMENT_SOUS_CATEGORIE.ID))
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

    fun updateGeometry(evenementId: UUID, evenementGeometrie: Geometry): Int =
        dsl.update(EVENEMENT).set(EVENEMENT.GEOMETRIE, evenementGeometrie).where(EVENEMENT.ID.eq(evenementId)).execute()

    fun getAllEvents(criseId: UUID, params: Filter? = null, dateDebExtraction: ZonedDateTime? = null, dateFinExtraction: ZonedDateTime? = null, state: EvenementStatutMode? = null): Collection<EvenementData> =
        dsl.select(
            EVENEMENT.ID,
            EVENEMENT.CRISE_ID,
            EVENEMENT.TAGS.convertFrom { it?.split(",")?.map { tag -> tag.trim() } ?: "" }.`as`("evenementTags"),
            EVENEMENT.ORIGINE,
            EVENEMENT.LIBELLE,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.IS_CLOSED,
            EVENEMENT.DESCRIPTION,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID,
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
            EVENEMENT_CATEGORIE.ID,
            EVENEMENT_CATEGORIE.CODE,
            EVENEMENT_CATEGORIE.LIBELLE,

            multiset(
                dsl.select(
                    EVENEMENT_SOUS_CATEGORIE.ID,
                    EVENEMENT_SOUS_CATEGORIE.CODE,
                    EVENEMENT_SOUS_CATEGORIE.LIBELLE,
                    EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE,
                )
                    .from(EVENEMENT_SOUS_CATEGORIE)
                    .where(
                        EVENEMENT_SOUS_CATEGORIE.EVENEMENT_CATEGORIE_ID.eq(
                            dsl.select(EVENEMENT_CATEGORIE.ID)
                                .from(criseCat)
                                .where(criseCat.field(EVENEMENT_CATEGORIE.LIBELLE)?.eq(EVENEMENT_CATEGORIE.LIBELLE)),
                        ),
                    ),

            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()?.let {
                        TypeEvenementForMap(
                            evenementSousCategorieId = r.value1(),
                            evenementSousCategorieCode = r.value2(),
                            evenementSousCategorieLibelle = r.value3(),
                            evenementSousCategorieGeometrie = r.value4(),
                        )
                    }
                }
            }.`as`("listSousType"),

        )
            .from(EVENEMENT_CATEGORIE)
            .join(L_TYPE_CRISE_CATEGORIE)
            .on(EVENEMENT_CATEGORIE.ID.eq(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID))
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

    fun getTypeEventForSelect(): Collection<EvenementSousCategorie> =
        dsl.select(
            EVENEMENT_SOUS_CATEGORIE.ID.`as`("evenementSousCategorieId"),
            EVENEMENT_SOUS_CATEGORIE.CODE.`as`("evenementSousCategorieCode"),
            EVENEMENT_SOUS_CATEGORIE.LIBELLE.`as`("evenementSousCategorieLibelle"),
            EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE.`as`("evenementSousCategorieGeometrie"),
        )
            .from(EVENEMENT_SOUS_CATEGORIE)
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
            EVENEMENT.TAGS.convertFrom { it?.split(",")?.map { tag -> tag.trim() } ?: "" }.`as`("evenementTags"),
            EVENEMENT.ORIGINE,
            EVENEMENT.LIBELLE,
            EVENEMENT.IMPORTANCE,
            EVENEMENT.IS_CLOSED.`as`("evenementEstFerme"),
            EVENEMENT.DESCRIPTION,
            EVENEMENT.DATE_CLOTURE,
            EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID.`as`("evenementSousCategorieId"),
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
            EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID,
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
            evenementData.evenementSousCategorieId,
            evenementData.evenementLibelle,
            evenementData.evenementDescription,
            evenementData.evenementOrigine,
            evenementData.evenementDateConstat,
            evenementData.evenementImportance,
            evenementData.evenementTags.joinToString(),
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
            .set(EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID, element.evenementSousCategorieId)
            .set(EVENEMENT.LIBELLE, element.evenementLibelle)
            .set(EVENEMENT.DESCRIPTION, element.evenementDescription)
            .set(EVENEMENT.ORIGINE, element.evenementOrigine)
            .set(EVENEMENT.DATE_CONSTAT, element.evenementDateConstat)
            .set(EVENEMENT.IMPORTANCE, element.evenementImportance)
            .set(EVENEMENT.TAGS, element.evenementTags.joinToString())
            .set(EVENEMENT.IS_CLOSED, element.evenementEstFerme)
            .set(EVENEMENT.DATE_CLOTURE, element.evenementDateCloture)
            .set(EVENEMENT.GEOMETRIE, element.evenementGeometrie)
            .set(EVENEMENT.CRISE_ID, element.evenementCriseId)
            .set(EVENEMENT.STATUT, element.evenementStatut)
            .set(EVENEMENT.UTILISATEUR_ID, element.evenementUtilisateurId)
            .set(EVENEMENT.STATUT_MODE, element.evenementStatutMode)
            .where(EVENEMENT.ID.eq(element.evenementId))
            .execute()

    data class FilterEvenementSousCategorie(
        val evenementSousCategorieLibelle: String?,
        val evenementSousCategorieCode: String?,
        val evenementSousCategorieTypeGeometrie: TypeGeometry?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    evenementSousCategorieLibelle?.let { DSL.and(EVENEMENT_SOUS_CATEGORIE.LIBELLE.contains(it)) },
                    evenementSousCategorieCode?.let { DSL.and(EVENEMENT_SOUS_CATEGORIE.CODE.contains(it)) },
                    evenementSousCategorieTypeGeometrie?.let { DSL.and(EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE.eq(it)) },
                ),
            )
    }

    data class SortEvenementSousCategorie(
        val evenementSousCategorieLibelle: Int?,
        val evenementSousCategorieCode: Int?,
        val evenementSousCategorieTypeGeometrie: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            EVENEMENT_SOUS_CATEGORIE.LIBELLE.getSortField(evenementSousCategorieLibelle),
            EVENEMENT_SOUS_CATEGORIE.CODE.getSortField(evenementSousCategorieCode),
            EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE.getSortField(evenementSousCategorieTypeGeometrie),
        )
    }

    fun getAllEvenementSousCategorie(params: Params<FilterEvenementSousCategorie, SortEvenementSousCategorie>): Collection<TypeSousType> =
        dsl.select(
            EVENEMENT_SOUS_CATEGORIE.ID,
            EVENEMENT_SOUS_CATEGORIE.CODE,
            EVENEMENT_SOUS_CATEGORIE.LIBELLE,
            EVENEMENT_SOUS_CATEGORIE.ACTIF.`as`("evenementSousCategorieActif"),
            EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE,
            EVENEMENT_SOUS_CATEGORIE.LIBELLE.`as`("evenementCategorieLibelle"),

            DSL.`when`(
                DSL.exists(
                    dsl.selectOne()
                        .from(EVENEMENT)
                        .where(EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID.eq(EVENEMENT_SOUS_CATEGORIE.ID)),
                ),
                true,
            ).otherwise(false).`as`("evenementsDependants"),
        )
            .from(EVENEMENT_SOUS_CATEGORIE)
            .leftJoin(EVENEMENT_CATEGORIE)
            .on(EVENEMENT_SOUS_CATEGORIE.EVENEMENT_CATEGORIE_ID.eq(EVENEMENT_CATEGORIE.ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(EVENEMENT_SOUS_CATEGORIE.CODE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto<TypeSousType>()

    fun getCountAllEvenementSousCategorie(filterBy: FilterEvenementSousCategorie?): Int =
        dsl.selectCount()
            .from(EVENEMENT_SOUS_CATEGORIE)
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .fetchSingleInto()
}
