package remocra.web.courrier

import com.google.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecases.courrier.CourrierGenerator
import remocra.usecases.courrier.CourrierRopGenerator
import remocra.usecases.courrier.GetCourriersWithParametresUseCase
import java.io.File
import java.util.UUID

@Path("/courriers")
class CourrierEndPoint {

    @Inject lateinit var courrierRopGenerator: CourrierRopGenerator

    @Inject lateinit var courrierGenerator: CourrierGenerator

    @Inject lateinit var getCourriersWithParametresUseCase: GetCourriersWithParametresUseCase

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
                courrierGenerator.execute(parametreCourrierInput, securityContext.userInfo, uriInfo),
            )
            .build()
    }

    class ParametreCourrierInput(
        @FormParam("modeleCourrierId")
        val modeleCourrierId: UUID,

        @FormParam("listParametres")
        val listParametres: List<NomValue>?,

    )

    data class NomValue(
        val nom: String,
        val valeur: String?,
    )

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
}
