package remocra.tasks

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PenaData
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
            val result = getDisponibilitePeiUseCase.execute(it)

            // On update la disponibilité terrestre
            if (result.terrestre != it.peiDisponibiliteTerrestre) {
                logManager.info("Le PEI d'id '${it.peiId}' [${it.peiNumeroComplet}] a été mis à jour avec une nouvelle disponibilité : ${result.terrestre.name}")
                peiRepository.updateDisponibilite(it.peiId, result.terrestre)

                if (parameters?.eventTracabilite == true) {
                    eventBus.post(
                        TracabiliteEvent(
                            pojo = it.apply { peiDisponibiliteTerrestre = result.terrestre },
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

            // Si c'est un PENA, vérifier aussi la dispo HBE
            if (it is PenaData && result.hbe != null && result.hbe != it.penaDisponibiliteHbe) {
                logManager.info("Le PENA d'id '${it.peiId}' [${it.peiNumeroComplet}] a été mis à jour avec une nouvelle disponibilité HBE : ${result.hbe.name}")
                penaRepository.updateDisponibiliteHbe(it.peiId, result.hbe)

                if (parameters?.eventTracabilite == true) {
                    eventBus.post(
                        TracabiliteEvent(
                            pojo = it.apply { penaDisponibiliteHbe = result.hbe },
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
