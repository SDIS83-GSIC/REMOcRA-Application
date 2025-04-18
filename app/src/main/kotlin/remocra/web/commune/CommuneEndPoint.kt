package remocra.web.commune

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.db.CommuneRepository
import remocra.db.LieuDitRepository
import remocra.db.VoieRepository
import remocra.usecase.commune.CommuneUseCase
import java.util.UUID

@Path("/commune")
@Produces(MediaType.APPLICATION_JSON)
class CommuneEndPoint {

    @Inject
    lateinit var communeUseCase: CommuneUseCase

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var voieRepository: VoieRepository

    @Inject lateinit var lieuDitRepository: LieuDitRepository

    @Context lateinit var securityContext: SecurityContext

    @GET
    @Path("/")
    @Public("Les communes ne sont pas liées à un droit")
    fun getCommuneByZoneIntegrationShortData(): Response =
        Response.ok().entity(communeRepository.getCommuneByZoneIntegrationShortData(securityContext.userInfo!!)).build()

    @GET
    @Path("/acces-rapide")
    @Public("Les communes ne sont pas liées à un droit")
    fun getCommuneForAccesRapide(
        @QueryParam("motifLibelle") motifLibelle: String,
    ): Response =
        Response.ok().entity(communeRepository.getCommuneIdLibelleByMotif(securityContext.userInfo!!, motifLibelle)).build()

    @GET
    @Path("/get-libelle-commune")
    @Public("Les communes ne sont pas liées à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getCommuneForSelect(): Response {
        return Response.ok(
            communeUseCase.getCommuneForSelect(securityContext.userInfo),
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

    @GET
    @Path("/{communeId}/geometrie")
    @Public("Les communes ne sont pas liées à un droit")
    fun getGeometrieById(@PathParam("communeId") communeId: UUID): Response {
        return Response.ok(communeRepository.getGeometrieCommune(communeId)).build()
    }
}
