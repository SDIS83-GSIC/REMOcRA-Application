package remocra.auth

import jakarta.annotation.Priority
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi

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
@Priority(Priorities.AUTHORIZATION)
class AuthorizationFilter : ContainerRequestFilter {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Context
    lateinit var resourceInfo: ResourceInfo

    override fun filter(requestContext: ContainerRequestContext) {
        val isPublic = resourceInfo.resourceMethod.isAnnotationPresent(Public::class.java)
        val isRequireDroitPresent = resourceInfo.resourceMethod.isAnnotationPresent(RequireDroits::class.java) || resourceInfo.resourceMethod.isAnnotationPresent(RequireDroitsApi::class.java)
        if (!isPublic && !isRequireDroitPresent) {
            // Si l'annotation n'existe pas sur une ressource non publique, on refuse de la servir
            logger.error("Pas de contexte d'autorisation défini pour la méthode ${resourceInfo.resourceMethod}")

            requestContext.abortWith(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur technique est survenue")
                    .build(),
            )
            return
        }

        if (isPublic && isRequireDroitPresent) {
            logger.error("@Public et @RequireDroit définis pour la méthode ${resourceInfo.resourceMethod}")
            requestContext.abortWith(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur technique est survenue")
                    .build(),
            )
            return
        }

        if (!isPublic) {
            // Il faut vérifier le(s) droit(s)
            // L'annotation permet de définir chacun des droits donnant accès à la ressource (il en faut donc UN parmi ceux-ci)
            val setDroitsPossibles = resourceInfo.resourceMethod.getAnnotation(RequireDroits::class.java).droits.toSet()

            if (setDroitsPossibles.intersect(requestContext.securityContext.userInfo!!.droits.toSet()).isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build())
            }
        }
    }
}
