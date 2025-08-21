package remocra.tasks

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.eventbus.pei.PeiModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecase.pei.GetNumerotationPeiUseCase

class RelanceNumerotationTask @Inject constructor(
    private val pibiRepository: PibiRepository,
    private val penaRepository: PenaRepository,
    private val getNumerotationPeiUseCase: GetNumerotationPeiUseCase,
    private val peiRepository: PeiRepository,
) : SimpleTask<RelanceNumerotationParameters, JobResults>() {

    override fun execute(parameters: RelanceNumerotationParameters?, userInfo: WrappedUserInfo): JobResults {
        val listePibiData = pibiRepository.getListPibiData()
        val listePenaData = penaRepository.getListPenaData()

        listePibiData.plus(listePenaData).forEach {
            val pairNumeros = getNumerotationPeiUseCase.execute(it)
            if (pairNumeros.first != it.peiNumeroComplet || pairNumeros.second != it.peiNumeroInterne) {
                logManager.info("Le PEI d'id '${it.peiId}' [${it.peiNumeroComplet}] a été mis à jour avec une nouvelle numérotation : ${pairNumeros.first} | ${pairNumeros.second}")
                peiRepository.updateNumeros(
                    peiId = it.peiId,
                    numeroComplet = pairNumeros.first,
                    numeroInterne = pairNumeros.second,
                )

                if (parameters?.eventTracabilite == true) {
                    eventBus.post(
                        TracabiliteEvent(
                            pojo = it.apply { peiNumeroInterne = pairNumeros.second; peiNumeroComplet = pairNumeros.first },
                            pojoId = it.peiId,
                            typeOperation = TypeOperation.UPDATE,
                            typeObjet = TypeObjet.PEI,
                            auteurTracabilite = userInfo.getInfosTracabilite(),
                            date = dateUtils.now(),
                        ),
                    )
                }

                if (parameters?.eventNexSis == true) {
                    eventBus.post(PeiModifiedEvent(it.peiId, TypeOperation.UPDATE))
                }
            }
        }

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: RelanceNumerotationParameters?) {
        // no-op
    }

    override fun getType(): TypeTask {
        return TypeTask.RELANCER_CALCUL_NUMEROTATION
    }

    override fun getTaskParametersClass(): Class<RelanceNumerotationParameters> {
        return RelanceNumerotationParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class RelanceNumerotationParameters() : TaskParameters(notification = null) {
    var eventTracabilite: Boolean = true
    var eventNexSis: Boolean = true
}
