package remocra.auth

import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.DynamicFeature
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.FeatureContext
import jakarta.ws.rs.core.SecurityContext
import net.ltgt.oauth.common.TokenIntrospector
import net.ltgt.oauth.common.TokenPrincipalProvider
import net.ltgt.oauth.rs.TokenFilter
import java.util.UUID
import net.ltgt.oidc.servlet.rs.IsAuthenticatedFilter as OidcIsAuthenticatedFilter

/**
 * Permet de désactiver l'authent pour certaines resources JAX-RS
 * La justification est obligatoire, permet de garantir que ce n'est pas un reliquat de tests !
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Public(val justification: String)

val SecurityContext.userInfo: UserInfo?
    get() = (userPrincipal as? RemocraUserPrincipal)?.userInfo

val SecurityContext.organismeUserId: UUID?
    get() = (userPrincipal as? RemocraUserPrincipal)?.userInfo?.organismeId

val SecurityContext.organismeInfo: OrganismeInfo?
    get() = (userPrincipal as? OrganismePrincipal)?.organismeInfo

class AuthenticationFeature : DynamicFeature {
    override fun configure(resourceInfo: ResourceInfo, context: FeatureContext) {
        if (resourceInfo.resourceMethod.isAnnotationPresent(Public::class.java)) {
            return
        }
        val pkg = "${resourceInfo.resourceClass.packageName}."
        when {
            pkg.startsWith("remocra.api.") -> context.register(ApiAuthenticationFilter::class.java)
            pkg.startsWith("remocra.apimobile.") -> context.register(ApiMobileAuthenticationFilter::class.java)
            else -> context.register(OidcIsAuthenticatedFilter::class.java)
        }
    }
}

@Priority(Priorities.AUTHENTICATION)
private class ApiAuthenticationFilter : TokenFilter() {
    @Inject lateinit var tokenIntrospector_: TokenIntrospector

    @Inject lateinit var organismePrincipalProvider: OrganismePrincipalProvider

    override fun getTokenIntrospector(): TokenIntrospector {
        return tokenIntrospector_
    }

    override fun getTokenPrincipalProvider(): TokenPrincipalProvider {
        return organismePrincipalProvider
    }
}

@Priority(Priorities.AUTHENTICATION)
private class ApiMobileAuthenticationFilter : TokenFilter() {
    @Inject lateinit var tokenIntrospector_: TokenIntrospector

    @Inject lateinit var mobileUserPrincipalProvider: MobileUserPrincipalProvider

    override fun getTokenIntrospector(): TokenIntrospector {
        return tokenIntrospector_
    }

    override fun getTokenPrincipalProvider(): TokenPrincipalProvider {
        return mobileUserPrincipalProvider
    }
}
