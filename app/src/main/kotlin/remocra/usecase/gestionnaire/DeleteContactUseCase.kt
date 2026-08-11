package remocra.usecase.gestionnaire

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.ContactData
import remocra.data.enums.ErrorType
import remocra.db.ContactRepository
import remocra.db.CourrierRepository
import remocra.db.DocumentRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteContactUseCase
@Inject
constructor(
    private val contactRepository: ContactRepository,
    private val courrierRepository: CourrierRepository,
    private val documentUtils: DocumentUtils,
    private val documentRepository: DocumentRepository,
) :
    AbstractCUDUseCase<ContactData>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        // Les droits sont gérés dans le checkContraintes puisqu'on a besoin de savoir si c'est un gestionnaire ou organisme
    }

    override fun postEvent(element: ContactData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.contactId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CONTACT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: ContactData): ContactData {
        val contactId = element.contactId

        // Suppression des liens Contact <-> Role
        contactRepository.deleteLContactRole(contactId)

        // Suppression des liens Contact <-> {Gestionnaire|Organisme}
        when {
            element.isGestionnaire -> contactRepository.deleteLContactGestionnaire(contactId)
            else -> contactRepository.deleteLContactOrganisme(contactId)
        }

        // On est obligé de supprimer dans les deux tables, car un bug en amont a induit des données pourries
        // Ça n'a pas d'impact, parce que les deux tables pointes sur les mêmes primary key ; contact_id
        // On supprime dans les deux pour garantir que le contact n'est plus lié à un courrier, qu'il s'agisse d'un gestionnaire ou organisme
        val courriersIds = (
            contactRepository.deleteLCourrierContactGestionnaireReturningCourrierId(contactId) +
                contactRepository.deleteLCourrierContactOrganismeReturningCourrierId(contactId)
            ).distinct()

        // courriers dont le document n'est pas utilisé par un PEI
        val courriersNonPeiIds = courrierRepository.getCourriersNonReferencesDansPei(courriersIds)

        /**
         * Suppression des courriers
         * Pour chaque courrier qui était lié au contact supprimé:
         * 1. Vérifie s'il possède toujours d'autres destinataires (via isCourrierStillReferenced)
         * 2. Si OUI : le courrier est encore référencé, ne rien supprimer
         * 3. Si NON : le courrier n'a plus aucun destinataire, le supprimer complètement
         *    - Supprime le lien Thematique <-> Courrier
         *    - Supprime l'objet Courrier en récupèrant le documentId
         *    - Si le document du courrier n'est référencé par aucun PEI,
         *          alors je peux supprimer le document et son dossier
         *    - Supprime l'objet Document en base
         *    - Supprime le répertoire du document sur le disque
         */
        // Suppression des courriers sans autres destinataires
        courriersIds
            .filterNot { courrierRepository.isCourrierStillReferenced(it) }
            .forEach { courrierId ->
                courrierRepository.deleteLCourrierThematique(courrierId)
                val documentId = courrierRepository.deleteCourrierById(courrierId)
                if (courriersNonPeiIds.contains(courrierId)) {
                    documentRepository.deleteDocumentByIds(listOf(documentId))
                    documentUtils.deleteDirectory(GlobalConstants.DOSSIER_DOCUMENT_COURRIER.resolve(documentId.toString()))
                }
            }

        // Suppression du contact
        contactRepository.deleteContact(contactId)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ContactData) {
        if ((!element.isGestionnaire && !userInfo.hasDroit(droitWeb = Droit.ORGANISME_CONTACT_A)) || (element.isGestionnaire && !userInfo.hasDroit(droitWeb = Droit.GEST_CONTACT_A))) {
            throw RemocraResponseException(ErrorType.CONTACT_FORBIDDEN_DELETE)
        }
    }
}
