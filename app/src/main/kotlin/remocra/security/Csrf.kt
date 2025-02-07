package remocra.security

import com.google.common.net.HttpHeaders
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.DynamicFeature
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.FeatureContext
import org.eclipse.jetty.util.URIUtil
import remocra.utils.forbidden
import remocra.utils.text
import java.security.SecureRandom
import java.util.Base64

private const val COOKIE_NAME = "xt"

private const val HEADER_NAME = "X-XTok"

/**
 * Permet de désactiver le filtre CSRF pour certaines resources JAX-RS.
 * La justification est obligatoire, permet de garantir que ce n'est pas un reliquat de tests !
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoCsrf(val justification: String)

class CsrfFeature : DynamicFeature {
    override fun configure(resourceInfo: ResourceInfo, context: FeatureContext) {
        if (resourceInfo.resourceMethod.isAnnotationPresent(NoCsrf::class.java)) {
            return
        }
        if ("${resourceInfo.resourceClass.packageName}.".run {
                startsWith("remocra.api.") || startsWith("remocra.apimobile.")
            }
        ) {
            // On n'applique pas la protection CSRF aux API points d'eau et API mobile
            return
        }
        context.register(CsrfFilter)
    }
}

/**
 * Un filtre qui vérifie que toutes les requêtes sont correctement protégées des CSRF.
 *
 * See
 * [OWASP CSRF Prevention Cheat Sheet](https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.md)
 */
private object CsrfFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        verifyCsrf(requestContext)?.let {
            requestContext.abortWith(forbidden().text(it).build())
            return
        }
    }
}

class CsrfServletFilter : Filter {

    override fun init(filterConfig: FilterConfig?) = Unit

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val secure = request.isSecure
        val cookieName = getCookieName(COOKIE_NAME, secure)

        val token = generateToken()
        (response as HttpServletResponse).addHeader(
            HttpHeaders.SET_COOKIE,
            // On n'utilise pas javax.servlet.http.Cookie pour pouvoir utiliser SameSite
            // XXX: Mettre une expiration ?
            "$cookieName=$token; Path=/${if (secure) "; Secure" else ""}; SameSite=strict",
        )
        chain.doFilter(request, response)
    }

    override fun destroy() = Unit

    private fun generateToken() =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(ByteArray(20).also { SecureRandom().nextBytes(it) })
}

private fun getCookieName(cookieName: String, secure: Boolean): String {
    assert(!cookieName.startsWith("__Host-") && !cookieName.startsWith("__Secure-"))
    return if (secure) "__Host-$cookieName" else cookieName
}

fun verifyCsrf(request: ContainerRequestContext): String? {
    return verifyOrigin(request) ?: verifyToken(request)
}

private fun verifyOrigin(request: ContainerRequestContext): String? {
    val actualOrigin =
        request.getHeaderString(HttpHeaders.ORIGIN)
            ?: request.getHeaderString(HttpHeaders.REFERER) ?: return "No Origin or Referrer"
    val expectedOrigin =
        request.uriInfo.baseUri.run { URIUtil.newURIBuilder(scheme, host, port).toString() }
    return "Origin mismatch".takeIf {
        actualOrigin != expectedOrigin && !actualOrigin.startsWith("$expectedOrigin/")
    }
}

private fun verifyToken(request: ContainerRequestContext): String? {
    val cookieName = getCookieName(COOKIE_NAME, request.securityContext.isSecure)
    val tokenFromCookie = request.cookies[cookieName]?.value
    val tokenFromHeader = request.getHeaderString(HEADER_NAME)
    return when {
        tokenFromCookie.isNullOrEmpty() -> "No CSRF token cookie"
        tokenFromHeader.isNullOrEmpty() -> "No CSRF token request header"
        tokenFromHeader != tokenFromCookie -> "CSRF token mismatch"
        else -> null
    }
}
