package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.DfciConflitRepository
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * UseCase afin de soit mettre à jour ou d'insérer un conflit
 */
class UpsertDfciConflitUseCase @Inject constructor(
    private val dfciConflitRepository: DfciConflitRepository,
) : AbstractUseCase() {

    /**
     * Si le conflit existe ou non, insère un nouveau conflit ou met à jour l'existant
     */
    fun execute(idRemocraElem: UUID, conflit: DfciConflit, userInfo: WrappedUserInfo) {
        if (!dfciConflitRepository.getExistDfciConflit(idRemocraElem, conflit.dfciConflitChamp)) {
            dfciConflitRepository.insertDfciConflit(conflit)
        } else {
            dfciConflitRepository.updateDfciConflit(conflit)
        }
    }
}
