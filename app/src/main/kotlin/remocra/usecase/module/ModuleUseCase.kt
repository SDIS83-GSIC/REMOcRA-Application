package remocra.usecase.module

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.UserInfo
import remocra.data.DocumentCourrierData
import remocra.db.ModuleRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.usecase.AbstractUseCase
import java.util.UUID

class ModuleUseCase : AbstractUseCase() {
    @Inject lateinit var moduleRepository: ModuleRepository

    @Inject lateinit var moduleDocumentUseCase: ModuleDocumentUseCase

    fun execute(uriInfo: UriBuilder, userInfo: UserInfo?): List<ModuleWithImageLink> {
        val listeModule = moduleRepository.getModules()

        val listeLThematiqueModule = moduleRepository.getModuleThematique()

        return listeModule.map {
            ModuleWithImageLink(
                moduleId = it.moduleId,
                moduleType = it.moduleType,
                moduleTitre = it.moduleTitre,
                moduleProtected = it.moduleProtected,
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
                listeDocument = moduleDocumentUseCase.execute(
                    moduleId = it.moduleId,
                    params = null,
                    userInfo = userInfo,
                ),
                listeThematiqueId = listeLThematiqueModule.filter { l -> l.moduleId == it.moduleId }.map { it.thematiqueId },

            )
        }
    }

    data class ModuleWithImageLink(
        val moduleId: UUID,
        val moduleType: TypeModule,
        val moduleTitre: String?,
        val moduleProtected: Boolean? = false,
        val moduleLinkImage: String?,
        val moduleContenuHtml: String?,
        val moduleColonne: Int,
        val moduleLigne: Int,
        val moduleNbDocument: Int?,
        val listeDocument: Collection<DocumentCourrierData>,
        val listeThematiqueId: Collection<UUID>?,

    )
}
