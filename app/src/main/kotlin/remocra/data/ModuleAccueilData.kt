package remocra.data

import jakarta.servlet.http.Part
import remocra.db.jooq.remocra.enums.TypeModule
import java.util.UUID

data class ModuleAccueilData(
    val moduleId: UUID?,
    val moduleType: TypeModule,
    val moduleTitre: String,
    val moduleContenuHtml: String?,
    val moduleColonne: Int,
    val moduleLigne: Int,
    val imageName: String?,
)
data class ListModuleWithImage(
    val listeModuleAccueilData: Collection<ModuleAccueilData>,
    val listeImage: List<Part>? = null,
)
