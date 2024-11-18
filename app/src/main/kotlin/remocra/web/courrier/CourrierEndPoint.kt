package remocra.web.courrier

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import org.slf4j.LoggerFactory
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.CourrierRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.usecase.courrier.CourrierGenerator
import remocra.usecase.courrier.CourrierRopGenerator
import remocra.usecase.courrier.GetCourriersWithParametresUseCase
import remocra.usecase.document.DocumentUtils
import java.io.File
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.pathString
import kotlin.reflect.jvm.javaMethod

@Path("/courriers")
class CourrierEndPoint {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject lateinit var courrierRopGenerator: CourrierRopGenerator

    @Inject lateinit var courrierGenerator: CourrierGenerator

    @Inject lateinit var getCourriersWithParametresUseCase: GetCourriersWithParametresUseCase

    @Inject lateinit var courrierRepository: CourrierRepository

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Context lateinit var securityContext: SecurityContext

    @Context lateinit var uriInfo: UriInfo

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.COURRIER_C])
    fun genererCourrier(
        parametreCourrierInput: ParametreCourrierInput,
    ): Response {
        return Response.ok()
            .entity(
                courrierGenerator.execute(
                    parametreCourrierInput,
                    securityContext.userInfo,
                    uriInfo.baseUriBuilder
                        .path(CourrierEndPoint::class.java)
                        .path(CourrierEndPoint::getUriCourrier.javaMethod),
                ),
            )
            .build()
    }

    @GET
    @Path("/parametres")
    @RequireDroits([Droit.COURRIER_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun getParametreByCourrier(): Response {
        return Response.ok(getCourriersWithParametresUseCase.execute(securityContext.userInfo)).build()
    }

    @GET
    @Path("/get-courrier")
    @RequireDroits([Droit.COURRIER_C])
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    fun getUriCourrier(@QueryParam("courrierPath") courrierPath: String?): Response {
        return Response.ok(
            courrierPath?.let { File(it) },
        )
            .build()
    }

    /**
     * Télécharge le courrier.
     * @param courrierId l'identifiant du courrier à télécharger
     * @return La réponse HTTP contenant le fichier à télécharger.
     */
    @GET
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @Public("Le téléchargement des courriers n'est pas dépendant d'un droit particulier")
    @Path("/telecharger/{courrierId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun telechargerCourrier(@PathParam("courrierId") courrierId: UUID): Response {
        val document = courrierRepository.getDocumentByCourrier(courrierId)

        return documentUtils.checkFile(
            File(Paths.get(document.documentRepertoire, document.documentNomFichier).pathString),
        )
    }
}
