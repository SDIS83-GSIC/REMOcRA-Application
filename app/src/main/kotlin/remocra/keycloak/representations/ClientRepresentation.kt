package remocra.keycloak.representations

@JvmRecord
data class ClientRepresentation(
    val id: String, // Identifiant de keycloak
    val clientId: String, // L'adresse mail de l'organisme
    val name: String?, // Le code de l'organisme
    val secret: String?,
    val serviceAccountsEnabled: Boolean = true,
    val implicitFlowEnabled: Boolean = false,
    val standardFlowEnabled: Boolean = false,
    val redirectUris: List<String> = listOf(),
    val attributes: Map<String, String> = mapOf(),
)
