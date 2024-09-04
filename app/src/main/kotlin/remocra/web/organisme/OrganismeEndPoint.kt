package remocra.web.organisme

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.usecases.organisme.OrganismeUseCase

@Path("/organisme")
@Produces(MediaType.APPLICATION_JSON)
class OrganismeEndPoint {

    @Inject
    lateinit var organismeUseCase: OrganismeUseCase

    @GET
    @Path("/get-libelle-organisme")
    @Public("Les organismes ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getOrganismeForSelect(): Response {
        return Response.ok(
            organismeUseCase.getOrganismeForSelect(),
        )
            .build()
    }

    @GET
    @Path("/get-list-autoriteDeci")
    @Public("Les organismes ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAutoriteDeciForSelect(): Response {
        return Response.ok(
            organismeUseCase.getAutoriteDeciForSelect(),
        )
            .build()
    }

    @GET
    @Path("/get-list-servicePublicDeci")
    @Public("Les organismes ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getServicePublicForSelect(): Response {
        return Response.ok(
            organismeUseCase.getServicePublicForSelect(),
        )
            .build()
    }
}
