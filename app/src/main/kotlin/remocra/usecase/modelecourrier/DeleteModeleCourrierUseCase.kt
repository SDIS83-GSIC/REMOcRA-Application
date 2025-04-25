package remocra.usecase.modelecourrier

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.ModeleCourrierData
import remocra.data.enums.ErrorType
import remocra.db.DocumentRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteModeleCourrierUseCase : AbstractCUDUseCase<ModeleCourrierData>(TypeOperation.DELETE) {

    @Inject
    private lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    private lateinit var documentRepository: DocumentRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ModeleCourrierData) {
        // no-op
    }

    override fun postEvent(element: ModeleCourrierData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(part = null),
                pojoId = element.modeleCourrierId!!,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.MODELE_COURRIER,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: ModeleCourrierData,
    ): ModeleCourrierData {
        modeleCourrierRepository.deleteLProfilDroit(element.modeleCourrierId!!)

        val repertoire = GlobalConstants.DOSSIER_MODELES_COURRIERS + "${element.modeleCourrierId}"
        documentUtils.deleteDirectory(repertoire)

        // Puis on supprime les paramètres
        modeleCourrierRepository.deleteModeleCourrierParametre(element.modeleCourrierId)

        // Le modèle de courrier
        modeleCourrierRepository.deleteModeleCourrier(element.modeleCourrierId)

        // Et enfin les documents
        documentRepository.deleteDocumentByIds(listOf(element.documentId!!))

        return element.copy(part = null)
    }
}
