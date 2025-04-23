package remocra.web.organisme

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.OrganismeRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.api.CreateClientKeycloakApiUseCase
import remocra.usecase.organisme.OrganismeUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/organisme")
@Produces(MediaType.APPLICATION_JSON)
class OrganismeEndPoint : AbstractEndpoint() {

    @Inject
    lateinit var organismeUseCase: OrganismeUseCase

    @Inject
    lateinit var organismeRepository: OrganismeRepository

    @Inject
    lateinit var createClientKeycloakApiUseCase: CreateClientKeycloakApiUseCase

    @Context
    lateinit var securityContext: SecurityContext

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

    @GET
    @Path("/get-active")
    @Public("Les organismes ne sont pas liés à un droit")
    fun getActive(): Response {
        return Response.ok(organismeUseCase.getActiveOrganisme()).build()
    }

    @POST
    @Path("/create-client-keycloak/{organismeId}")
    @RequireDroits([Droit.ADMIN_API])
    @Produces(MediaType.APPLICATION_JSON)
    fun createClientKeyclaok(
        @PathParam("organismeId")
        organismeId: UUID,
    ): Response {
        return createClientKeycloakApiUseCase.execute(
            securityContext.userInfo,
            organismeRepository.getById(organismeId),
        ).wrap()
    }
}
