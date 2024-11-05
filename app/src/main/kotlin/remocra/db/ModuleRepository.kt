package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.Module
import remocra.db.jooq.remocra.tables.references.MODULE
import java.util.UUID

class ModuleRepository @Inject constructor(private val dsl: DSLContext) {
    fun getModules(): Collection<Module> =
        dsl.selectFrom(MODULE)
            .orderBy(MODULE.COLONNE, MODULE.LIGNE).fetchInto()

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
    ) =
        dsl.update(MODULE)
            .set(MODULE.COLONNE, moduleColonne)
            .set(MODULE.LIGNE, moduleLigne)
            .set(MODULE.TYPE, moduleType)
            .set(MODULE.TITRE, moduleTitre)
            .set(MODULE.CONTENU_HTML, moduleContenuHtml)
            .set(MODULE.IMAGE, moduleImage)
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
}
