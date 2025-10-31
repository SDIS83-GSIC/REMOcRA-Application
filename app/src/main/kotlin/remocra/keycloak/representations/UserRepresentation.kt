package remocra.keycloak.representations

@JvmRecord
data class UserRepresentation(
    val id: String,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val emailVerified: Boolean = false,
    val enabled: Boolean = true,
    val requiredActions: List<String>,
)
