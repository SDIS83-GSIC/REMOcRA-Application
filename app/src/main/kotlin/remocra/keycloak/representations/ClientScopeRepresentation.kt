package remocra.keycloak.representations

data class ClientScopeRepresentation(
    val id: String? = null,
    val name: String,
    val protocol: String? = null,
)
