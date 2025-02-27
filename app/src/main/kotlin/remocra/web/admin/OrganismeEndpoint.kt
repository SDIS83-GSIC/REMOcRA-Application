package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.OrganismeData
import remocra.data.Params
import remocra.db.OrganismeRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.organisme.CreateOrganismeUseCase
import remocra.usecase.admin.organisme.UpdateOrganismeUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=utf-8")
@Path("/organisme")
class OrganismeEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var organismeRepository: OrganismeRepository

    @Inject
    lateinit var createOrganismeUseCase: CreateOrganismeUseCase

    @Inject
    lateinit var updateOrganismeUseCase: UpdateOrganismeUseCase

    @Context
    lateinit var securityContext: SecurityContext

    class OrganismeInput {
        @FormParam("actif")
        val actif: Boolean = true

        @FormParam("code")
        lateinit var code: String

        @FormParam("libelle")
        lateinit var libelle: String

        @FormParam("emailContact")
        val emailContact: String? = null

        @FormParam("profilOrganismeId")
        lateinit var profilOrganismeId: UUID

        @FormParam("typeOrganismeId")
        lateinit var typeOrganismeId: UUID

        @FormParam("zoneIntegrationId")
        lateinit var zoneIntegrationId: UUID

        @FormParam("parentId")
        val parentId: UUID? = null
    }

    @POST
    @Path("/get")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A])
    fun get(params: Params<OrganismeRepository.Filter, OrganismeRepository.Sort>): Response {
        return Response.ok(DataTableau(organismeRepository.getAllForAdmin(params), organismeRepository.getCountForAdmin(params)))
            .build()
    }

    @GET
    @Path("/get-all")
    @Public("L'affichage des organismes n'est pas lié à un droit (par exemple : les filtres)")
    fun getAll(): Response {
        return Response.ok(organismeRepository.getAll())
            .build()
    }

    @GET
    @Path("/get/{id}")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A])
    fun id(@PathParam("id") id: UUID): Response {
        return Response.ok(organismeRepository.getById(id)).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A])
    fun add(organismeInput: OrganismeInput): Response {
        return createOrganismeUseCase.execute(
            securityContext.userInfo,
            OrganismeData(
                UUID.randomUUID(),
                organismeInput.actif,
                organismeInput.code,
                organismeInput.libelle,
                organismeInput.emailContact,
                organismeInput.profilOrganismeId,
                organismeInput.typeOrganismeId,
                organismeInput.zoneIntegrationId,
                organismeInput.parentId,
            ),
        ).wrap()
    }

    @PUT
    @Path("/update/{id}")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A])
    fun edit(@PathParam("id") id: UUID, organismeInput: OrganismeInput): Response {
        return updateOrganismeUseCase.execute(
            securityContext.userInfo,
            OrganismeData(
                id,
                organismeInput.actif,
                organismeInput.code,
                organismeInput.libelle,
                organismeInput.emailContact,
                organismeInput.profilOrganismeId,
                organismeInput.typeOrganismeId,
                organismeInput.zoneIntegrationId,
                organismeInput.parentId,
            ),
        ).wrap()
    }
}
