package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.ProfilDroitData
import remocra.db.ProfilDroitRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.usecase.admin.lienprofildroit.UpdateLienProfilDroitUseCase
import remocra.web.AbstractEndpoint

@Produces("application/json; charset=UTF-8")
@Path("/lien-profil-droit")
class LienProfilDroitEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var profilDroitRepository: ProfilDroitRepository

    @Inject lateinit var updateLienDroitUseCase: UpdateLienProfilDroitUseCase

    @Path("/")
    @GET
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(): Response =
        Response.ok(
            object {
                val profilDroitList: Collection<ProfilDroit> = profilDroitRepository.getAllForAdmin().sortedBy { it.profilDroitLibelle }
                val typeDroitList: Collection<Droit> = Droit.entries.sortedBy { it }
            },
        ).build()

    @Path("/update")
    @PUT
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(element: Collection<ProfilDroitData>): Response =
        updateLienDroitUseCase.execute(securityContext.userInfo, element).wrap()
}
