package remocra.usecase.oldeb

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DocumentRepository
import remocra.db.OldebRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Oldeb
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteOldebUseCase @Inject constructor(
    private val oldebRepository: OldebRepository,
    private val documentRepository: DocumentRepository,
    private val documentUtils: DocumentUtils,
) : AbstractCUDUseCase<Oldeb>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.OLDEB_D)) {
            throw RemocraResponseException(ErrorType.OLDEB_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: Oldeb, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.oldebId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.OLDEB,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: Oldeb): Oldeb {
        // Suppression des suites
        oldebRepository.deleteSuite(element.oldebId)

        // Suppression des anomalies
        oldebRepository.deleteAnomalie(element.oldebId)

        // Suppression des documents
        oldebRepository.selectOldebDocument(element.oldebId).let {
            oldebRepository.deleteOldebVisiteDocument(element.oldebId)
            documentRepository.deleteDocumentByIds(it)
        }
        documentUtils.deleteDirectory("${GlobalConstants.DOSSIER_DOCUMENT_OLD}$element.oldebId")

        // Suppression des visites
        oldebRepository.deleteVisite(element.oldebId)

        // Suppression des caractéristiques absentes
        oldebRepository.deleteCaracteristique(element.oldebId)

        // Suppression de la propriété
        oldebRepository.deletePropriete(element.oldebId)

        // Suppression du locataire
        oldebRepository.deleteLocataire(element.oldebId)

        // Suppression de l'OLD
        oldebRepository.deleteOldeb(element.oldebId)

        // That's all folks !
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Oldeb) {}
}
