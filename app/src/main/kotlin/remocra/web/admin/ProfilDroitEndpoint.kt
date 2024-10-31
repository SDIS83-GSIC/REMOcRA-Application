package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.ProfilDroitData
import remocra.db.ProfilDroitRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.profildroit.CreateProfilDroitUseCase
import remocra.usecase.admin.profildroit.UpdateProfilDroitUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/profil-droit")
class ProfilDroitEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var profilDroitRepository: ProfilDroitRepository

    @Path("/")
    @POST
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(params: Params<ProfilDroitRepository.Filter, ProfilDroitRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                profilDroitRepository.getAll(params),
                profilDroitRepository.getCountAll(params),
            ),
        ).build()

    @Path("/{profilDroitId}")
    @GET
    @RequireDroits([Droit.ADMIN_DROITS])
    fun get(@PathParam("profilDroitId") profilDroitId: UUID): Response =
        Response.ok(profilDroitRepository.getById(profilDroitId)).build()

    @Inject lateinit var createProfilDroitUseCase: CreateProfilDroitUseCase

    @Path("/create")
    @POST
    @RequireDroits([Droit.ADMIN_DROITS])
    fun post(element: ProfilDroitData): Response =
        createProfilDroitUseCase.execute(securityContext.userInfo, element).wrap()

    @Inject lateinit var updateProfilDroitUseCase: UpdateProfilDroitUseCase

    @Path("/update/{profilDroitId}")
    @PUT
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(@PathParam("profilDroitId") profilDroitId: UUID, element: ProfilDroitData): Response =
        updateProfilDroitUseCase.execute(securityContext.userInfo, element.copy(profilDroitId = profilDroitId)).wrap()
}
