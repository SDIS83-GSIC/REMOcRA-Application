package remocra.usecases.module

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriInfo
import remocra.GlobalConstants
import remocra.db.ModuleRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.web.module.ModuleEndPoint
import java.nio.file.Paths
import kotlin.reflect.jvm.javaMethod

class ModuleUseCase {
    @Inject lateinit var moduleRepository: ModuleRepository

    fun execute(uriInfo: UriInfo): List<ModuleWithImageLink> {
        val listeModule = moduleRepository.getModules()

        return listeModule.map {
            ModuleWithImageLink(
                moduleType = it.moduleType,
                moduleTitre = it.moduleTitre,
                moduleLinkImage = it.moduleImage?.let {
                    uriInfo
                        .baseUriBuilder
                        .path(ModuleEndPoint::class.java)
                        .path(ModuleEndPoint::getUriImage.javaMethod)
                        .queryParam("imagePath", Paths.get(GlobalConstants.DOSSIER_IMAGE_MODULE, it).toString())
                        .build()
                        .toString()
                },
                moduleContenuHtml = it.moduleContenuHtml,
                moduleColonne = it.moduleColonne,
                moduleLigne = it.moduleLigne,
            )
        }
    }

    data class ModuleWithImageLink(
        val moduleType: TypeModule,
        val moduleTitre: String?,
        val moduleLinkImage: String?,
        val moduleContenuHtml: String?,
        val moduleColonne: Int,
        val moduleLigne: Int,
    )
}
