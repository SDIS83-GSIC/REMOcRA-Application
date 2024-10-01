package remocra.auth

import jakarta.annotation.Priority
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import java.io.IOException
import java.util.UUID

/**
 * Permet de désactiver l'authent pour certaines resources JAX-RS
 * La justification est obligatoire, permet de garantir que ce n'est pas un reliquat de tests !
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Public(val justification: String)

val SecurityContext.userInfo: UserInfo?
    get() = (userPrincipal as? UserPrincipal)?.userInfo

val SecurityContext.organismeUserId: UUID?
    get() = (userPrincipal as? UserPrincipal)?.userInfo?.organismeId

@Priority(Priorities.AUTHENTICATION)
class AuthenticationFilter : ContainerRequestFilter {
    @Context lateinit var resourceInfo: ResourceInfo

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext) {
        if (resourceInfo.resourceMethod.isAnnotationPresent(Public::class.java)) {
            return
        }

        if (requestContext.securityContext.userPrincipal == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        }
    }
}
