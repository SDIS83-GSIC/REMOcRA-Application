package remocra.usecase.api

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.OrganismeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.representations.ClientRepresentation
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class CreateClientKeycloakApiUseCase @Inject constructor(
    private val keycloakApi: KeycloakApi,
    private val organismeRepository: OrganismeRepository,
) :
    AbstractCUDUseCase<Organisme>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_API)) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Organisme) {
        if (element.organismeEmailContact.isNullOrBlank()) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_NULL)
        }

        val existEmail = organismeRepository.fetchEmailExists(element.organismeEmailContact!!, element.organismeId)

        if (existEmail) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_DOUBLON)
        }

        if (element.organismeKeycloakId != null) {
            throw RemocraResponseException(ErrorType.DROIT_API_DEJA_CLIENT_KEYCLOAK)
        }
    }

    override fun execute(userInfo: UserInfo?, element: Organisme): Organisme {
        val authorization = userInfo!!.accessToken.toAuthorizationHeader()
        val idTechnique = UUID.randomUUID().toString()

        val response = keycloakApi.createClient(
            authorization,
            ClientRepresentation(
                id = idTechnique,
                clientId = element.organismeEmailContact!!, // l'email n'est pas nulle, on l'a vérifié dans le checkContraintes
                name = element.organismeCode,
                secret = null,
            ),
        ).execute()

        if (!response.isSuccessful) {
            val replacement = "${response.message()} - " +
                "(${
                    response.errorBody()?.source()
                }"
            throw RemocraResponseException(ErrorType.DROIT_API_INSERT_CLIENT_KEYCLOAK, replacement)
        }

        // On update l'organisme
        organismeRepository.updateKeycloakClientId(idTechnique, element.organismeId)
        return element
    }

    override fun postEvent(element: Organisme, userInfo: UserInfo) {
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
}
