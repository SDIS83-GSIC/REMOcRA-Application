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
import remocra.usecase.pei.GetDisponibilitePeiUseCase

class RelanceCalculDispoTask @Inject constructor(
    private val pibiRepository: PibiRepository,
    private val penaRepository: PenaRepository,
    private val getDisponibilitePeiUseCase: GetDisponibilitePeiUseCase,
    private val peiRepository: PeiRepository,
) : SimpleTask<RelanceCalculDispoParameters, JobResults>() {

    override fun execute(parameters: RelanceCalculDispoParameters?, userInfo: WrappedUserInfo): JobResults? {
        val listePibiData = pibiRepository.getListPibiData()
        val listePenaData = penaRepository.getListPenaData()

        // On update les PEI en relançant le calcul de dispo
        listePibiData.plus(listePenaData).forEach {
            val newDisponibilite = getDisponibilitePeiUseCase.execute(it)
            // On update la disponibiltié
            if (newDisponibilite != it.peiDisponibiliteTerrestre) {
                logManager.info("Le PEI d'id '${it.peiId}' [${it.peiNumeroComplet}] a été mis à jour avec une nouvelle disponibilité : ${newDisponibilite.name}")
                peiRepository.updateDisponibilite(it.peiId, newDisponibilite)

                if (parameters?.eventTracabilite == true) {
                    eventBus.post(
                        TracabiliteEvent(
                            pojo = it.apply { peiDisponibiliteTerrestre = newDisponibilite },
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

    override fun checkParameters(parameters: RelanceCalculDispoParameters?) {
        // no-op
    }

    override fun getType(): TypeTask {
        return TypeTask.RELANCER_CALCUL_DISPONIBILITE
    }

    override fun getTaskParametersClass(): Class<RelanceCalculDispoParameters> {
        return RelanceCalculDispoParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class RelanceCalculDispoParameters() : TaskParameters(notification = null) {
    var eventTracabilite: Boolean = true
    var eventNexSis: Boolean = true
}
