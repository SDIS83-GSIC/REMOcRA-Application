package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.ParametresAdminDataInput
import remocra.data.enums.PeiCaracteristique
import remocra.data.enums.TypeCaracterique
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.ParametresUseCase
import remocra.usecase.admin.UpdateParametresUseCase
import remocra.web.AbstractEndpoint

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
class AdminEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    private lateinit var parametresUseCase: ParametresUseCase

    @Inject
    private lateinit var updateParametresUseCase: UpdateParametresUseCase

    @GET
    @Path("/parametres")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun getParametresData(): Response {
        return Response.ok(parametresUseCase.getParametresData()).build()
    }

    @GET
    @Path("/pei-caracteristique")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun getPeiCaracteristique(): Response {
        return Response.ok(
            PeiCaracteristique.entries.map {
                PeiCaracteristique(
                    libelle = it.libelle,
                    id = it.name,
                    type = it.typeCaracterique,
                )
            },
        ).build()
    }

    @PUT
    @Path("/parametres")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun updateParametres(parametres: ParametresAdminDataInput): Response {
        return updateParametresUseCase.execute(
            userInfo = securityContext.userInfo,
            element = parametres,
        ).wrap()
    }
}
private data class PeiCaracteristique(
    val libelle: String,
    val id: String,
    val type: TypeCaracterique,
)
