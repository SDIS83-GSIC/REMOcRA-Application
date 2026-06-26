package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.DfciTableString
import remocra.data.enums.ErrorType
import remocra.db.DfciAiresRepository
import remocra.db.DfciConflitRepository
import remocra.db.DfciDebRepository
import remocra.db.DfciPanneauRepository
import remocra.db.DfciPistesRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class ResolveDfciConflitUseCase @Inject constructor(
    private val dfciConflitRepository: DfciConflitRepository,
    private val dfciPistesRepository: DfciPistesRepository,
    private val dfciDebRepository: DfciDebRepository,
    private val dfciAiresRepository: DfciAiresRepository,
    private val dfciPannneauRepository: DfciPanneauRepository,
) : AbstractCUDUseCase<DfciConflit>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DFCI_GESTION_CONFLITS_A)) {
            throw RemocraResponseException(ErrorType.DFCI_GESTION_CONFLIT_A_FORBIDDEN)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: DfciConflit,
    ) {
        if (element.dfciConflitValeurRemocra != element.dfciConflitValeurSig) {
            throw RemocraResponseException(errorType = ErrorType.DFCI_RESOLVE_CONFLIT_NOT_SAME_VALUE)
        }
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: DfciConflit,
    ): DfciConflit {
        val value: String? = element.dfciConflitValeurRemocra
        when (element.dfciConflitTable) {
            DfciTableString.DFCI_PISTE.nomTable ->
                dfciPistesRepository.updateDfciPisteColumn(
                    element.dfciConflitChamp,
                    value,
                    element.dfciConflitElementId,
                )
            DfciTableString.DFCI_DEB.nomTable -> dfciDebRepository.updateDfciDebColumn(
                element.dfciConflitChamp,
                value,
                element.dfciConflitElementId,
            )
            DfciTableString.DFCI_AIRE.nomTable -> dfciAiresRepository.updateDfciAireColumn(
                element.dfciConflitChamp,
                value,
                element.dfciConflitElementId,
            )
            DfciTableString.DFCI_PANNEAU.nomTable -> dfciPannneauRepository.updateDfciPanneauColumn(
                element.dfciConflitChamp,
                value,
                element.dfciConflitElementId,
            )
        }
        dfciConflitRepository.dropDfciConflit(element)
        return element
    }

    override fun postEvent(
        element: DfciConflit,
        userInfo: WrappedUserInfo,
    ) {
        eventBus.post(
            TracabiliteEvent(
                element,
                element.dfciConflitId,
                typeOperation,
                TypeObjet.DFCI_CONFLIT,
                userInfo.getInfosTracabilite(),
                dateUtils.now(),
            ),
        )
    }
}
