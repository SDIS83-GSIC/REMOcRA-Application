package remocra.usecase.admin.organisme

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.OrganismeData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.OrganismeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.representations.ClientRepresentation
import remocra.usecase.AbstractCUDUseCase

class UpdateOrganismeUseCase @Inject constructor(
    private val organismeRepository: OrganismeRepository,
    private val keycloakApi: KeycloakApi,
) :
    AbstractCUDUseCase<OrganismeData>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.ADMIN_ORGANISME_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: OrganismeData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.organismeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ORGANISME,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: OrganismeData): OrganismeData {
        // Si on a un client keycloak, on regarde si l'adresse mail a changé. Si c'est le cas, on envoie l'info à keycloak
        val organisme = organismeRepository.getById(element.organismeId)
        if (organisme.organismeKeycloakId != null && element.organismeEmailContact != organisme.organismeEmailContact) {
            val response = keycloakApi.updateClient(
                authorization = userInfo!!.accessToken.toAuthorizationHeader(),
                techniqueId = organisme.organismeKeycloakId!!,
                client = ClientRepresentation(
                    clientId = element.organismeEmailContact!!,
                    id = organisme.organismeKeycloakId!!,
                    name = organisme.organismeCode,
                    secret = "",
                ),
            ).execute()

            if (!response.isSuccessful) {
                val replacement = "${response.message()} - " +
                    "(${
                        response.errorBody()!!.source()
                    }"
                throw RemocraResponseException(ErrorType.DROIT_API_UPDATE_CLIENT_KEYCLOAK, replacement)
            }
        }

        organismeRepository.edit(element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: OrganismeData) {
        if (organismeRepository.getById(element.organismeId).organismeKeycloakId != null) {
            if (element.organismeEmailContact.isNullOrBlank()) {
                throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_NULL)
            }

            val existEmail = organismeRepository.fetchEmailExists(element.organismeEmailContact, element.organismeId)

            if (existEmail) {
                throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_DOUBLON)
            }
        }
    }
}
