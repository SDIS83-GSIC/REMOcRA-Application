package remocra.usecase.utilisateur

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.AuthModule
import remocra.auth.WrappedUserInfo
import remocra.data.UtilisateurImportData
import remocra.data.enums.ErrorType
import remocra.db.OrganismeRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.RequiredAction
import remocra.keycloak.representations.UserRepresentation
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class CreateUtilisateurUseCase
@Inject
constructor(
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
    private val keycloakApi: KeycloakApi,
    private val utilisateurRepository: UtilisateurRepository,
    private val organismeRepository: OrganismeRepository,
) :
    AbstractCUDUseCase<UtilisateurImportData>(TypeOperation.INSERT) {

    private val logger = LoggerFactory.getLogger(CreateUtilisateurUseCase::class.java)

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if ((
                !userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A) &&
                    !userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_ORGA_A)
                ) || (!userInfo.hasDroit(droitWeb = Droit.IMPORT_UTILISATEUR_A))
        ) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_FORBIDDEN)
        }
    }

    override fun postEvent(element: UtilisateurImportData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.utilisateurData,
                pojoId = element.utilisateurData.utilisateurId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.UTILISATEUR,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: UtilisateurImportData): UtilisateurImportData {
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!

        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

            // Remonte le résultat de la recherche de l'identifiant dans keycloak
            // soit la map contient un élément, c'est notre utilisateur qui existe déjà dans keycloak
            // soit elle est vide, l'utilisateur n'existe pas dans keycloak
            val listeUtilisateurKeycloak = keycloakApi.getUsersByUsername(token, element.utilisateurData.utilisateurUsername).execute().body()

            // Si l'utilisateur n'est pas dans keycloak, on le crée
            if (listeUtilisateurKeycloak?.map { it.username }?.contains(element.utilisateurData.utilisateurUsername) != true) {
                val response = keycloakApi.createUser(
                    token,
                    UserRepresentation(
                        id = "",
                        username = element.utilisateurData.utilisateurUsername,
                        email = element.utilisateurData.utilisateurEmail,
                        lastName = element.utilisateurData.utilisateurNom,
                        firstName = element.utilisateurData.utilisateurPrenom,
                        requiredActions = listOf(RequiredAction.VERIFY_EMAIL.name),
                    ),
                ).execute()

                if (response.errorBody() != null) {
                    val replacement = "${response.message()} - " +
                        "(${
                            response.errorBody()!!.source()
                        }"
                    throw RemocraResponseException(ErrorType.UTILISATEUR_ERROR_INSERT, replacement)
                }

                logger.info("Utilisateur ${element.utilisateurData.utilisateurUsername} inséré dans keycloak")
            }

            val keycloakId = keycloakApi.getUsersByUsername(token, element.utilisateurData.utilisateurUsername).execute().body()!!.first().id

            val responseMailKeycloak = keycloakApi.executeActionsEmail(
                token,
                actions = setOf(RequiredAction.VERIFY_EMAIL.name, RequiredAction.UPDATE_PASSWORD.name),
                userId = keycloakId,
                clientId = keycloakClient.clientId,
            ).execute()

            if (!responseMailKeycloak.isSuccessful) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_ACTION_EMAIL)
            }

            utilisateurRepository.insertUtilisateur(
                element.utilisateurData.copy(utilisateurId = UUID.randomUUID()),
                keycloakId,
            )

            return element
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: UtilisateurImportData) {
        if (element.utilisateurData.utilisateurUsername.trim().length < 3) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_LENGTH)
        }
        if (utilisateurRepository.checkExistsUsername(element.utilisateurData.utilisateurUsername, null)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_EXISTS)
        }

        if (element.isImported) {
            return // si je suis dans un import, pas de PB d'organisme enfant
        }
        if ((!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A) && (!userInfo.isSuperAdmin))) {
            userInfo.affiliatedOrganismeIds?.contains(element.utilisateurData.utilisateurOrganismeId)?.let {
                if (!(it)) {
                    throw RemocraResponseException(ErrorType.UTILISATEUR_FORBIDDEN)
                }
            }
        }
    }
}
