package remocra.web.pei

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.db.PeiRepository
import remocra.usecases.pei.PeiUseCase
import java.util.UUID

@Path("/pei")
@Produces(MediaType.APPLICATION_JSON)
class PeiEndPoint {

    @Inject lateinit var peiUseCase: PeiUseCase

    @Inject lateinit var peiRepository: PeiRepository

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiWithFilter(params: Params): Response {
        val listPei = peiUseCase.getPeiWithFilter(params)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilter(params)),
        )
            .build()
    }

    data class DataTableau(

        val list: List<PeiRepository.PeiForTableau>?,
        val count: Int,

    )
    data class Params(
        @QueryParam("limit")
        val limit: Int? = 10,
        @QueryParam("offset")
        val offset: Int? = 0,
        @QueryParam("filterBy")
        val filterBy: PeiRepository.Filter?,
        @QueryParam("sortBy")
        val sortBy: PeiRepository.Sort?,
    )

    @GET
    @Path("/{idPei}")
    fun getInfoPei(
        @PathParam("idPei") idPei: UUID,
    ): Response {
        return Response.ok(peiUseCase.getInfoPei(idPei)).build()
    }

    @GET
    @Path("/referentiel-for-update-pei") // TODO idPei
    fun getReferentielUpdatePei() =
        Response.ok(peiUseCase.getInfoForUpdate()).build()
}
