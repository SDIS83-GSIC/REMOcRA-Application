package remocra.web.commune

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.LieuDitRepository
import remocra.db.VoieRepository
import remocra.usecase.commune.CommuneUseCase
import java.util.UUID

@Path("/commune")
@Produces(MediaType.APPLICATION_JSON)
class CommuneEndPoint {

    @Inject
    lateinit var communeUseCase: CommuneUseCase

    @Inject lateinit var voieRepository: VoieRepository

    @Inject lateinit var lieuDitRepository: LieuDitRepository

    @GET
    @Path("/get-libelle-commune")
    @Public("Les communes ne sont pas liées à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getCommuneForSelect(): Response {
        return Response.ok(
            communeUseCase.getCommuneForSelect(),
        )
            .build()
    }

    @GET
    @Path("/{communeId}/voie")
    @Public("Les communes ne sont pas liées à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getVoieList(@PathParam("communeId") communeId: UUID): Response = Response.ok(voieRepository.getVoieListByCommuneId(communeId)).build()

    @GET
    @Path("/{communeId}/lieu-dit")
    @Public("Les communes ne sont pas liées à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getLieuDitList(@PathParam("communeId") communeId: UUID): Response = Response.ok(lieuDitRepository.getLieuDitListByCommuneId(communeId)).build()
}
