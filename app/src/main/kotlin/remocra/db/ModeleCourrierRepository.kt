package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.Params
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.LModeleCourrierDocument
import remocra.db.jooq.remocra.tables.pojos.LModeleCourrierProfilDroit
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrierParametre
import remocra.db.jooq.remocra.tables.references.L_MODELE_COURRIER_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_MODELE_COURRIER_PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER_PARAMETRE
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class ModeleCourrierRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getByCode(code: String): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.eq(code))
            .fetchSingleInto()

    fun getById(modeleCourrierId: UUID): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.ID.eq(modeleCourrierId))
            .fetchSingleInto()

    fun getAll(utilisateurId: UUID): Collection<ModeleCourrier> =
        dsl.select(*MODELE_COURRIER.fields())
            .from(MODELE_COURRIER)
            .join(L_MODELE_COURRIER_PROFIL_DROIT)
            .on(MODELE_COURRIER.ID.eq(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID))
            .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(L_MODELE_COURRIER_PROFIL_DROIT.PROFIL_DROIT_ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .fetchInto()

    /**
     * Retourne les paramètres groupés par courrier
     */
    fun getParametresByModele(): Map<ModeleCourrier, List<ModeleCourrierParametre>> =
        dsl.select(
            *MODELE_COURRIER.fields(),
            *MODELE_COURRIER_PARAMETRE.fields(),
        )
            .from(MODELE_COURRIER)
            .join(MODELE_COURRIER_PARAMETRE)
            .on(MODELE_COURRIER.ID.eq(MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID))
            .fetchGroups(ModeleCourrier::class.java, ModeleCourrierParametre::class.java)

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<ModeleCourrierComplet> =
        dsl.selectDistinct(
            MODELE_COURRIER.ID,
            MODELE_COURRIER.CODE,
            MODELE_COURRIER.ACTIF,
            MODELE_COURRIER.PROTECTED,
            MODELE_COURRIER.LIBELLE,
            MODELE_COURRIER.DESCRIPTION,
            MODELE_COURRIER.MODULE,
            multiset(
                selectDistinct(PROFIL_DROIT.LIBELLE)
                    .from(PROFIL_DROIT)
                    .join(L_MODELE_COURRIER_PROFIL_DROIT)
                    .on(L_MODELE_COURRIER_PROFIL_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                    .where(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }?.joinToString()
            }.`as`("listeProfilDroit"),
        )
            .from(MODELE_COURRIER)
            .leftJoin(L_MODELE_COURRIER_PROFIL_DROIT)
            .on(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(MODELE_COURRIER.MODULE, MODELE_COURRIER.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.selectDistinct(
            MODELE_COURRIER.ID,
        )
            .from(MODELE_COURRIER)
            .leftJoin(L_MODELE_COURRIER_PROFIL_DROIT)
            .on(L_MODELE_COURRIER_PROFIL_DROIT.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val modeleCourrierCode: String?,
        val modeleCourrierLibelle: String?,
        val modeleCourrierActif: Boolean?,
        val modeleCourrierProtected: Boolean?,
        val modeleCourrierModule: TypeModuleRapportCourrier?,
        val listeProfilDroitId: Collection<UUID>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    modeleCourrierCode?.let { DSL.and(MODELE_COURRIER.CODE.containsIgnoreCase(it)) },
                    modeleCourrierLibelle?.let { DSL.and(MODELE_COURRIER.LIBELLE.containsIgnoreCase(it)) },
                    modeleCourrierActif?.let { DSL.and(MODELE_COURRIER.ACTIF.eq(it)) },
                    modeleCourrierProtected?.let { DSL.and(MODELE_COURRIER.PROTECTED.eq(it)) },
                    modeleCourrierModule?.let { DSL.and(MODELE_COURRIER.MODULE.eq(TypeModule.entries.find { t -> t.name == it.name })) },
                    listeProfilDroitId?.let { DSL.and(L_MODELE_COURRIER_PROFIL_DROIT.PROFIL_DROIT_ID.`in`(it)) },
                ),
            )
    }

    data class Sort(
        val modeleCourrierCode: Int?,
        val modeleCourrierLibelle: Int?,
        val modeleCourrierActif: Int?,
        val modeleCourrierProtected: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            MODELE_COURRIER.CODE.getSortField(modeleCourrierCode),
            MODELE_COURRIER.ACTIF.getSortField(modeleCourrierActif),
            MODELE_COURRIER.LIBELLE.getSortField(modeleCourrierLibelle),
            MODELE_COURRIER.PROTECTED.getSortField(modeleCourrierProtected),
        )
    }

    data class ModeleCourrierComplet(
        val modeleCourrierId: UUID,
        val modeleCourrierActif: Boolean,
        val modeleCourrierCode: String,
        val modeleCourrierLibelle: String,
        val modeleCourrierProtected: Boolean,
        val modeleCourrierDescription: String?,
        val modeleCourrierModule: TypeModuleRapportCourrier,
        val listeProfilDroit: String?,
    )

    fun insertModeleCourrier(modeleCourrier: ModeleCourrier) {
        dsl.insertInto(MODELE_COURRIER)
            .set(dsl.newRecord(MODELE_COURRIER, modeleCourrier))
            .execute()
    }

    fun insertLModeleCourrierProfilDroit(lModeleCourrierProfilDroit: LModeleCourrierProfilDroit) =
        dsl.insertInto(L_MODELE_COURRIER_PROFIL_DROIT)
            .set(dsl.newRecord(L_MODELE_COURRIER_PROFIL_DROIT, lModeleCourrierProfilDroit))
            .execute()

    fun upsertModeleCourrierParametre(modeleCourrierParametre: ModeleCourrierParametre) =
        dsl.insertInto(MODELE_COURRIER_PARAMETRE)
            .set(dsl.newRecord(MODELE_COURRIER_PARAMETRE, modeleCourrierParametre))
            .onConflict(MODELE_COURRIER_PARAMETRE.ID)
            .doUpdate()
            .set(dsl.newRecord(MODELE_COURRIER_PARAMETRE, modeleCourrierParametre))
            .execute()

    /**
     * Vérifie s'il existe déjà un élément avec ce *code*. En modification, on regarde si le code existe pour un autre élément que lui-même
     */
    fun checkCodeExists(modeleCourrierCode: String, modeleCourrierId: UUID?) = dsl.fetchExists(
        dsl.select(MODELE_COURRIER.CODE)
            .from(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.equalIgnoreCase(modeleCourrierCode))
            .and(MODELE_COURRIER.ID.notEqual(modeleCourrierId)),
    )

    fun insertLModeleCourrierDocument(lModeleCourrierDocument: LModeleCourrierDocument) =
        dsl.insertInto(L_MODELE_COURRIER_DOCUMENT)
            .set(dsl.newRecord(L_MODELE_COURRIER_DOCUMENT, lModeleCourrierDocument))
            .execute()

    fun deleteLModeleCourrierDocument(listId: Collection<UUID>) =
        dsl.deleteFrom(L_MODELE_COURRIER_DOCUMENT)
            .where(L_MODELE_COURRIER_DOCUMENT.DOCUMENT_ID.`in`(listId))
            .execute()

    fun updateIsMainReport(listDocumentId: List<UUID>, isMainReport: Boolean) =
        dsl.update(L_MODELE_COURRIER_DOCUMENT)
            .set(L_MODELE_COURRIER_DOCUMENT.IS_MAIN_REPORT, isMainReport)
            .where(L_MODELE_COURRIER_DOCUMENT.DOCUMENT_ID.`in`(listDocumentId))
            .execute()
}
