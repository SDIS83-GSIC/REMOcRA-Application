package remocra.usecases.couverturehydraulique

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
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime

class CreatePeiProjetUseCase : AbstractCUDUseCase<PeiProjetData>(TypeOperation.INSERT) {
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
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: PeiProjetData): PeiProjetData {
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

    override fun checkContraintes(userInfo: UserInfo?, element: PeiProjetData) {
        // Les contraintes sont vérifiées directement dans le execute
    }
}
