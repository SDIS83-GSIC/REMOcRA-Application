package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.auth.AuthModule
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.KeycloakUri
import remocra.usecase.AbstractUseCase
import remocra.utils.DateUtils

class CheckUrlUseCase @Inject constructor(
    private val parametresProvider: ParametresProvider,
    private val keycloakUri: KeycloakUri,
    private val keycloakApi: KeycloakApi,
    private val keycloakClient: AuthModule.KeycloakClient,
    private val keycloakToken: KeycloakToken,
) : AbstractUseCase() {

    private val LOGIN = "fr.sdis83.remocra.mobile:/login"
    private val LOGOUT = "fr.sdis83.remocra.mobile:/logout"

    fun execute(): MobileData {
        // On vérifie si le mode "déconnexion" est à true, on envoie la date de la prochaine déconnexion
        val accepteModeDeconnecte =
            parametresProvider.getParametreBoolean(GlobalConstants.PARAMETRE_MODE_DECONNECTE)
        val dureeSession = parametresProvider.getParametreInt(GlobalConstants.PARAMETRE_DUREE_VALIDITE_TOKEN)

        // On envoie le mot de passe Admin s'il existe
        val mdpAdmin = parametresProvider.getParametreString(GlobalConstants.PARAMETRE_MDP_ADMINISTRATEUR)

        // Puis les informations de keycloak
        // Note : le client ne devrait pas changer mais on ne peut pas le garantir
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!

        val mobileData: MobileData

        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"
            val client = keycloakApi.getClientRemocraMobile(token).execute().body()?.firstOrNull()

            if (client == null) {
                throw IllegalArgumentException("Le client remocra-mobile n'existe pas dans keyclaok")
            }

            // On vérifie ensuite si les informations n'ont pas changé
            if (client.redirectUris.first() != LOGIN || client.attributes["post.logout.redirect.uris"] != LOGOUT) {
                throw IllegalArgumentException("Les informations ont été changée !")
            }

            mobileData = MobileData(
                dateProchaineConnexion = dureeSession.takeIf { accepteModeDeconnecte == true }?.let {
                    dateUtils.format(
                        dateUtils.now().plusHours(it.toLong()),
                        DateUtils.Companion.PATTERN_MINUTE,
                    ) // Format attendu par l'appli mobile
                }?.toString(),
                mdpAdmin = mdpAdmin,
                keycloakConfig = KeycloakConfig(
                    url = keycloakUri.baseUri,
                    clientId = client.clientId,
                    login = LOGIN,
                    logout = LOGOUT,
                ),
            )
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }

        return mobileData
    }

    data class MobileData(
        val dateProchaineConnexion: String?,
        val mdpAdmin: String?,
        val keycloakConfig: KeycloakConfig,
    )

    data class KeycloakConfig(
        val url: String,
        val clientId: String,
        val login: String,
        val logout: String,
    )
}
