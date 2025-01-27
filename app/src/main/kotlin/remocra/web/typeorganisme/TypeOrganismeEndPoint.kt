package remocra.web.typeorganisme

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.TypeOrganismeRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.tables.pojos.TypeOrganisme
import remocra.security.NoCsrf
import remocra.usecase.typeorganisme.UpdateTypeOrganismeDroitApiUseCase
import remocra.web.AbstractEndpoint

@Path("/type-organisme")
@Produces(MediaType.APPLICATION_JSON)
class TypeOrganismeEndPoint : AbstractEndpoint() {
    @Inject
    lateinit var typeOrganismeRepository: TypeOrganismeRepository

    @Inject
    lateinit var updateTypeOrganismeDroitApiUseCase: UpdateTypeOrganismeDroitApiUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/get-active")
    @NoCsrf("")
    @Public("Les types organisme ne sont pas liés à un droit")
    fun getActive(): Response {
        return Response.ok(typeOrganismeRepository.getAll()).build()
    }

    @GET
    @Path("/droits-api")
    @RequireDroits([Droit.ADMIN_API])
    fun getTypeOrganismeWithDroitApi(): Response {
        return Response.ok(
            TypeOrganismeDroitApi(
                listTypeOrganisme = typeOrganismeRepository.getAll(),
                listTypeDroitApi = DroitApi.entries,
            ),
        ).build()
    }

    data class TypeOrganismeDroitApi(
        val listTypeOrganisme: Collection<TypeOrganisme>,
        val listTypeDroitApi: Collection<DroitApi>,
    )

    @PUT
    @Path("/droits-api/update")
    @RequireDroits([Droit.ADMIN_API])
    fun updateTypeOrganismeWithDroitApi(listTypeOrganisme: Collection<TypeOrganisme>): Response =
        updateTypeOrganismeDroitApiUseCase.execute(
            securityContext.userInfo,
            listTypeOrganisme,
        ).wrap()
}
