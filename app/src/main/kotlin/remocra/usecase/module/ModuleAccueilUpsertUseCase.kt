package remocra.usecase.module

import jakarta.inject.Inject
import org.owasp.html.PolicyFactory
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.ListModuleWithImage
import remocra.data.enums.ErrorType
import remocra.db.ModuleRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.Module
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.nio.file.Paths
import java.util.UUID

class ModuleAccueilUpsertUseCase @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val documentUtils: DocumentUtils,
    private var policyFactory: PolicyFactory,
) :
    AbstractCUDUseCase<ListModuleWithImage>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODULE_RESUME_FORBIDDEN)
        }
    }

    override fun postEvent(element: ListModuleWithImage, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listeImage = null),
                // Ici on n'a pas d'id, la page d'accueil étant commune à toute l'appli
                pojoId = UUID.randomUUID(),
                typeOperation = typeOperation,
                typeObjet = TypeObjet.MODULE_ACCUEIL,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: ListModuleWithImage,
    ): ListModuleWithImage {
        val modulesAvant = moduleRepository.getModules()

        moduleRepository.deleteLThematiqueModule()

        val listeDelete = modulesAvant.filter { !element.listeModuleAccueilData.map { it.moduleId }.contains(it.moduleId) }

        listeDelete.forEach {
            if (it.moduleImage != null) {
                // On supprime sur le disque
                documentUtils.deleteFile(it.moduleImage!!, GlobalConstants.DOSSIER_IMAGE_MODULE)
                documentUtils.deleteDirectory(GlobalConstants.DOSSIER_IMAGE_MODULE.resolve("${it.moduleId}"))
            }

            moduleRepository.delete(it.moduleId)
        }

        element.listeModuleAccueilData.forEach {
            val moduleId = it.moduleId ?: UUID.randomUUID()
            val imageAvant = modulesAvant.firstOrNull { f -> f.moduleId == it.moduleId }?.moduleImage
            // Si on a une image
            if (it.imageName != null && element.listeImage != null) {
                val repertoire = GlobalConstants.DOSSIER_IMAGE_MODULE.resolve("$moduleId")
                if (imageAvant != null) {
                    // On supprime sur le disque
                    documentUtils.deleteFile(it.imageName, repertoire)
                }

                // On sauvegarde l'image sur le disque
                element.listeImage.first { f -> f.submittedFileName == it.imageName }
                    .inputStream.use { inputStream ->
                        documentUtils.saveFile(
                            inputStream,
                            it.imageName,
                            repertoire,
                        )
                    }
            }

            if (it.moduleId == null) {
                moduleRepository.insertModule(
                    Module(
                        moduleId = moduleId,
                        moduleType = it.moduleType,
                        moduleTitre = it.moduleTitre,
                        moduleImage = if (it.imageName != null) Paths.get(moduleId.toString(), it.imageName).toString() else null,
                        moduleContenuHtml = if (it.moduleType == TypeModule.PERSONNALISE) policyFactory.sanitize(it.moduleContenuHtml) else null,
                        moduleColonne = it.moduleColonne,
                        moduleLigne = it.moduleLigne,
                        moduleNbDocument = if (it.moduleType == TypeModule.DOCUMENT || it.moduleType == TypeModule.COURRIER) {
                            it.moduleNbDocument
                        } else {
                            null
                        },
                        moduleProtected = false,
                    ),
                )
            } else {
                moduleRepository.updateModule(
                    moduleId = moduleId,
                    moduleColonne = it.moduleColonne,
                    moduleLigne = it.moduleLigne,
                    moduleTitre = it.moduleTitre,
                    moduleContenuHtml = if (it.moduleType == TypeModule.PERSONNALISE) it.moduleContenuHtml else null,
                    moduleType = it.moduleType,
                    moduleNbDocument = it.moduleNbDocument,
                    moduleImage = if (it.imageName != null) Paths.get(moduleId.toString(), it.imageName).toString() else imageAvant,
                )
            }

            it.listeThematiqueId?.forEach { thematiqueId ->
                moduleRepository.insertLThematiqueModule(moduleId = moduleId, thematiqueId = thematiqueId)
            }
        }

        return element.copy(listeImage = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ListModuleWithImage) {
        // pas de contraintes
    }
}
