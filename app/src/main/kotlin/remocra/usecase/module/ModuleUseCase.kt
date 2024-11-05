package remocra.usecase.module

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.db.ModuleRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.usecase.AbstractUseCase
import java.util.UUID

class ModuleUseCase : AbstractUseCase() {
    @Inject lateinit var moduleRepository: ModuleRepository

    fun execute(uriInfo: UriBuilder): List<ModuleWithImageLink> {
        val listeModule = moduleRepository.getModules()

        val listeLThematiqueModule = moduleRepository.getModuleThematique()

        return listeModule.map {
            ModuleWithImageLink(
                moduleId = it.moduleId,
                moduleType = it.moduleType,
                moduleTitre = it.moduleTitre,
                moduleLinkImage = it.moduleImage?.let { image ->
                    uriInfo
                        .clone()
                        .queryParam("moduleId", it.moduleId)
                        .build()
                        .toString()
                },
                moduleContenuHtml = it.moduleContenuHtml,
                moduleColonne = it.moduleColonne,
                moduleLigne = it.moduleLigne,
                moduleNbDocument = it.moduleNbDocument,
                listeThematiqueId = listeLThematiqueModule.filter { l -> l.moduleId == it.moduleId }.map { it.thematiqueId },
            )
        }
    }

    data class ModuleWithImageLink(
        val moduleId: UUID,
        val moduleType: TypeModule,
        val moduleTitre: String?,
        val moduleLinkImage: String?,
        val moduleContenuHtml: String?,
        val moduleColonne: Int,
        val moduleLigne: Int,
        val moduleNbDocument: Int?,
        val listeThematiqueId: Collection<UUID>?,
    )
}
