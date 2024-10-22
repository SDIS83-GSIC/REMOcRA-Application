package remocra.usecase.couverturehydraulique

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.PeiProjetData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdatePeiProjetUseCase : AbstractCUDUseCase<PeiProjetData>(TypeOperation.UPDATE) {
    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: PeiProjetData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiProjetId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI_PROJET,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: PeiProjetData): PeiProjetData {
        couvertureHydrauliqueRepository.updatePeiProjet(
            peiProjetId = element.peiProjetId,
            peiTypePeiProjet = element.peiProjetTypePeiProjet,
            diametreCanalisation = element.peiProjetDiametreCanalisation,
            debit = element.peiProjetDebit,
            capacite = element.peiProjetCapacite,
            geometrie = element.peiProjetGeometrie,
            natureDeciId = element.peiProjetNatureDeciId,
            diametreId = element.peiProjetDiametreId,
        )

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: PeiProjetData) {
        when (element.peiProjetTypePeiProjet) {
            TypePeiProjet.PA -> {
                if (element.peiProjetDebit == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DEBIT_MANQUANT)
                }
            }
            TypePeiProjet.PIBI -> {
                if (element.peiProjetDiametreId == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DIAMETRE_MANQUANT)
                }
                if (element.peiProjetDiametreCanalisation == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DIAMETRE_CANALISATION_MANQUANT)
                }
            }
            TypePeiProjet.RESERVE -> {
                if (element.peiProjetCapacite == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_CAPACITE_MANQUANTE)
                }
                if (element.peiProjetDebit == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DEBIT_MANQUANT)
                }
            }
        }
    }
}
