package remocra.usecase.courrier

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.ModeleCourrierRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.BuildDynamicForm
import java.util.UUID

/**
 * Permet de récupérer tous les modèles de courrier et les paramètres de ces derniers
 * Ce usecase permettra de fabriquer les formulaires dynamiquement dans le front
 */
class BuildFormCourrierUseCase
@Inject
constructor(
    private val modeleCourrierRepository: ModeleCourrierRepository,
    private val buildDynamicForm: BuildDynamicForm,
) :
    AbstractUseCase() {

    fun execute(userInfo: WrappedUserInfo, modeleCourrierId: UUID) = buildDynamicForm.executeForModeleCourrier(
        userInfo,
        modeleCourrierRepository.getModeleCourrierParametre(modeleCourrierId),
    )
}
