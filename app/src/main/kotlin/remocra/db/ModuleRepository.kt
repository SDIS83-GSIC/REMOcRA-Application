package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.LThematiqueModule
import remocra.db.jooq.remocra.tables.pojos.Module
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_MODULE
import remocra.db.jooq.remocra.tables.references.MODULE
import java.util.UUID

class ModuleRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getModules(): Collection<Module> =
        dsl.selectFrom(MODULE)
            .orderBy(MODULE.COLONNE, MODULE.LIGNE).fetchInto()

    fun getModuleThematique(): Collection<LThematiqueModule> =
        dsl.selectFrom(L_THEMATIQUE_MODULE).fetchInto()

    fun getModuleThematiqueByModuleId(moduleId: UUID): Collection<LThematiqueModule> =
        dsl.selectFrom(L_THEMATIQUE_MODULE).where(L_THEMATIQUE_MODULE.MODULE_ID.eq(moduleId)).fetchInto()

    fun getById(moduleId: UUID): Module =
        dsl.selectFrom(MODULE)
            .where(MODULE.ID.eq(moduleId))
            .fetchSingleInto()

    fun updateModule(
        moduleId: UUID,
        moduleColonne: Int,
        moduleLigne: Int,
        moduleTitre: String,
        moduleContenuHtml: String?,
        moduleType: TypeModule,
        moduleImage: String?,
        moduleNbDocument: Int?,
    ) =
        dsl.update(MODULE)
            .set(MODULE.COLONNE, moduleColonne)
            .set(MODULE.LIGNE, moduleLigne)
            .set(MODULE.TYPE, moduleType)
            .set(MODULE.TITRE, moduleTitre)
            .set(MODULE.CONTENU_HTML, moduleContenuHtml)
            .set(MODULE.IMAGE, moduleImage)
            .set(MODULE.NB_DOCUMENT, moduleNbDocument)
            .where(MODULE.ID.eq(moduleId))
            .execute()

    fun insertModule(module: Module) =
        dsl.insertInto(MODULE)
            .set(dsl.newRecord(MODULE, module))
            .execute()

    fun delete(moduleId: UUID) =
        dsl.deleteFrom(MODULE)
            .where(MODULE.ID.eq(moduleId))
            .execute()

    fun deleteLThematiqueModule() =
        dsl.deleteFrom(L_THEMATIQUE_MODULE).execute()

    fun insertLThematiqueModule(moduleId: UUID, thematiqueId: UUID) =
        dsl.insertInto(L_THEMATIQUE_MODULE)
            .set(L_THEMATIQUE_MODULE.MODULE_ID, moduleId)
            .set(L_THEMATIQUE_MODULE.THEMATIQUE_ID, thematiqueId)
            .execute()
}
