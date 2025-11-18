package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.RapportPersonnaliseRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.BuildDynamicForm
import java.util.UUID

/**
 * Permet de récupérer tous les rapports personnalisés et les paramètres de ces derniers
 * Ce usecase permettra de fabriquer les formulaires dynamiquement dans le front
 */
class BuildFormRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var buildDynamicForm: BuildDynamicForm

    fun execute(userInfo: WrappedUserInfo, rapportPersonnaliseId: UUID) = buildDynamicForm.executeForRapportPerso(
        userInfo,
        rapportPersonnaliseRepository.getRapportPersonnaliseForm(rapportPersonnaliseId),
    )
}
