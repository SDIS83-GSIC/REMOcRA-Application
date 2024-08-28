package remocra.authn

import jakarta.annotation.Priority
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import java.io.IOException

/**
 * Permet de désactiver l'authent pour certaines resources JAX-RS
 * La justification est obligatoire, permet de garantir que ce n'est pas un reliquat de tests !
 * */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Public(val justification: String)

val HttpServletRequest.userInfo: UserInfo?
    get() = getSession(false)?.userInfo

val SecurityContext.userInfo: UserInfo?
    get() = (userPrincipal as? UserPrincipal)?.userInfo

private const val SESSION_ATTR_NAME = "remocra.current_user.user_info"

var HttpSession.userInfo: UserInfo?
    get() = getAttribute(SESSION_ATTR_NAME) as? UserInfo
    private set(value) = setAttribute(SESSION_ATTR_NAME, value)

@Priority(Priorities.AUTHENTICATION)
class AuthenticationFilter : ContainerRequestFilter {
    @Context
    lateinit var httpServletRequest: HttpServletRequest

    @Context lateinit var resourceInfo: ResourceInfo

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext) {
        if (isAnnotatedWith(resourceInfo, Public::class.java)) {
            return
        }

        if (requestContext.securityContext.userPrincipal == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        }
    }
}

fun isAnnotatedWith(resourceInfo: ResourceInfo, annotationClass: Class<out Annotation>): Boolean =
    resourceInfo.resourceClass.isAnnotationPresent(annotationClass) ||
        resourceInfo.resourceMethod.isAnnotationPresent(annotationClass)
