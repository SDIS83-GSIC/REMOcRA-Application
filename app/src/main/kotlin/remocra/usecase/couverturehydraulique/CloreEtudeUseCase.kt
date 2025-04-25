package remocra.usecase.couverturehydraulique

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.EtudeData
import remocra.data.enums.ErrorType
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class CloreEtudeUseCase : AbstractCUDUseCase<UUID>(TypeOperation.UPDATE) {

    @Inject
    lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_C)
        }
    }

    override fun postEvent(element: UUID, userInfo: WrappedUserInfo) {
        val etude = couvertureHydrauliqueRepository.getEtude(element)
        eventBus.post(
            TracabiliteEvent(
                pojo = EtudeData(
                    etudeId = element,
                    typeEtudeId = etude.typeEtudeId,
                    etudeNumero = etude.etudeNumero,
                    etudeLibelle = etude.etudeLibelle,
                    etudeDescription = etude.etudeDescription,
                    listeCommuneId = etude.listeCommuneId,
                    listeDocument = null,
                ),
                pojoId = element,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ETUDE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: UUID): UUID {
        couvertureHydrauliqueRepository.cloreEtude(element)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: UUID) {
        // noop pas de contrainte
    }
}
