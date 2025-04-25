package remocra.usecase.couverturehydraulique

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PeiProjetData
import remocra.data.enums.ErrorType
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreatePeiProjetUseCase : AbstractCUDUseCase<PeiProjetData>(TypeOperation.INSERT) {
    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: PeiProjetData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiProjetId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI_PROJET,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: PeiProjetData): PeiProjetData {
        when (element.peiProjetTypePeiProjet) {
            TypePeiProjet.PA -> {
                if (element.peiProjetDebit == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DEBIT_MANQUANT)
                }
                couvertureHydrauliqueRepository.insertPeiProjetPA(
                    element.peiProjetEtudeId,
                    element.peiProjetId,
                    element.peiProjetDebit,
                    element.peiProjetGeometrie,
                    element.peiProjetNatureDeciId,
                )
            }
            TypePeiProjet.PIBI -> {
                if (element.peiProjetDiametreId == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DIAMETRE_MANQUANT)
                }
                if (element.peiProjetDiametreCanalisation == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DIAMETRE_CANALISATION_MANQUANT)
                }
                couvertureHydrauliqueRepository.insertPeiProjetPibi(
                    element.peiProjetEtudeId,
                    element.peiProjetId,
                    element.peiProjetDiametreId,
                    element.peiProjetDiametreCanalisation,
                    element.peiProjetGeometrie,
                    element.peiProjetNatureDeciId,
                )
            }
            TypePeiProjet.RESERVE -> {
                if (element.peiProjetCapacite == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_CAPACITE_MANQUANTE)
                }
                if (element.peiProjetDebit == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DEBIT_MANQUANT)
                }
                couvertureHydrauliqueRepository.insertPeiProjetReserve(
                    element.peiProjetEtudeId,
                    element.peiProjetId,
                    element.peiProjetDebit,
                    element.peiProjetCapacite,
                    element.peiProjetGeometrie,
                    element.peiProjetNatureDeciId,
                )
            }
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: PeiProjetData) {
        // Les contraintes sont vérifiées directement dans le execute
    }
}
