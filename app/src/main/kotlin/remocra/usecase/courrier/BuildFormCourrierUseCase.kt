package remocra.usecase.courrier

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.usecase.AbstractUseCase
import remocra.utils.BuildDynamicForm

/**
 * Permet de récupérer tous les modèles de courrier et les paramètres de ces derniers
 * Ce usecase permettra de fabriquer les formulaires dynamiquement dans le front
 */
class BuildFormCourrierUseCase : AbstractUseCase() {

    @Inject
    private lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    private lateinit var buildDynamicForm: BuildDynamicForm

    fun execute(userInfo: UserInfo?, typeModule: TypeModule) = buildDynamicForm.executeForModeleCourrier(
        userInfo,
        modeleCourrierRepository.getListeModeleCourrier(userInfo!!.utilisateurId, userInfo.isSuperAdmin, typeModule),
    )
}
