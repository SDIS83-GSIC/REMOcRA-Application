package remocra.auth

import jakarta.annotation.Priority
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.DynamicFeature
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.FeatureContext
import jakarta.ws.rs.core.SecurityContext
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi
import net.ltgt.oauth.rs.AbstractAuthorizationFilter as AbstractOauthAuthorizationFilter
import net.ltgt.oidc.servlet.rs.AbstractAuthorizationFilter as AbstractOidcAuthorizationFilter

/**
 * Annotation permettant de définir les droits d'accès que l'utilisateur connecté doit posséder pour exécuter cette fonction.
 * Son traitement se fait dans le [AuthorizationFilter]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireDroits(val droits: Array<Droit>)

/**
 * Annotation permettant de définir les droits d'accès à l'API
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireDroitsApi(val droitsApi: Array<DroitApi>)

/**
 * Permet de vérifier que la ressource ciblée est bien accessible au demandeur.
 * Toute méthode définie dans un Endpoint doit
 * * soit avoir une annotation @Public [remocra.auth.Public] sur la méthode ou la classe (à utiliser avec Modération ou Parcimonie, au choix)
 * * soit définir une liste des droits autorisés à utiliser cette méthode
 */
class AuthorizationFeature : DynamicFeature {
    override fun configure(resourceInfo: ResourceInfo, context: FeatureContext) {
        val isPublic = resourceInfo.resourceMethod.isAnnotationPresent(Public::class.java)
        val isRequireDroitPresent = resourceInfo.resourceMethod.isAnnotationPresent(RequireDroits::class.java)
        val isRequireDroitApiPresent = resourceInfo.resourceMethod.isAnnotationPresent(RequireDroitsApi::class.java)

        // Normalement déjà vérifié par les tests ArchUnit, mais ça coûte pas grand chose de le vérifier aussi au runtime
        // surtout que c'est fait une seule fois au démarrage.
        if (!isPublic && !isRequireDroitPresent && !isRequireDroitApiPresent) {
            throw RuntimeException("Pas de contexte d'autorisation défini pour la méthode ${resourceInfo.resourceMethod}")
        }
        if (!(isPublic xor isRequireDroitPresent xor isRequireDroitApiPresent)) {
            throw RuntimeException("@Public et @RequireDroit/@RequireDroitApi définis pour la méthode ${resourceInfo.resourceMethod}")
        }

        when {
            isRequireDroitPresent -> {
                val droitsPossibles = resourceInfo.resourceMethod.getAnnotation(RequireDroits::class.java)!!.droits.toSet()
                val pkg = "${resourceInfo.resourceClass.packageName}."
                when {
                    pkg.startsWith("remocra.apimobile.") -> context.register(ApiMobileAuthorizationFilter(droitsPossibles))
                    else -> context.register(AuthorizationFilter(droitsPossibles))
                }
            }
            isRequireDroitApiPresent -> {
                context.register(ApiAuthorizationFilter(resourceInfo.resourceMethod.getAnnotation(RequireDroitsApi::class.java)!!.droitsApi.toSet()))
            }
            else -> assert(isPublic)
        }
    }
}

@Priority(Priorities.AUTHORIZATION)
private class AuthorizationFilter(
    private val droitsPossibles: Set<Droit>,
) : AbstractOidcAuthorizationFilter() {
    // L'annotation permet de définir chacun des droits donnant accès à la ressource (il en faut donc UN parmi ceux-ci)
    override fun isAuthorized(securityContext: SecurityContext): Boolean =
        securityContext.userInfo?.let { droitsPossibles.intersect(it.droits).isNotEmpty() } ?: false
}

@Priority(Priorities.AUTHORIZATION)
private class ApiMobileAuthorizationFilter(
    private val droitsPossibles: Set<Droit>,
) : AbstractOauthAuthorizationFilter() {
    // L'annotation permet de définir chacun des droits donnant accès à la ressource (il en faut donc UN parmi ceux-ci)
    override fun isAuthorized(securityContext: SecurityContext): Boolean =
        securityContext.userInfo?.let { droitsPossibles.intersect(it.droits).isNotEmpty() } ?: false
}

@Priority(Priorities.AUTHORIZATION)
private class ApiAuthorizationFilter(
    private val droitsPossibles: Set<DroitApi>,
) : AbstractOauthAuthorizationFilter() {
    // L'annotation permet de définir chacun des droits donnant accès à la ressource (il en faut donc UN parmi ceux-ci)
    override fun isAuthorized(securityContext: SecurityContext): Boolean =
        securityContext.organismeInfo?.let { droitsPossibles.intersect(it.droits).isNotEmpty() } ?: false
}
