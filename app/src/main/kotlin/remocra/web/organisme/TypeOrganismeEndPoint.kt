package remocra.web.organisme

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.data.GlobalData
import remocra.db.TypeOrganismeRepository

@Path("/type-organisme")
@Produces(MediaType.APPLICATION_JSON)
class TypeOrganismeEndPoint {

    @Inject
    lateinit var typeOrganismeRepository: TypeOrganismeRepository

    @GET
    @Path("/get")
    @Public("Les types organismes ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getTypeOrganismeForSelect(): Response {
        return Response.ok(
            typeOrganismeRepository.getAll().map {
                GlobalData.IdCodeLibelleData(
                    id = it.typeOrganismeId,
                    code = it.typeOrganismeCode,
                    libelle = it.typeOrganismeLibelle,
                )
            },
        )
            .build()
    }
}
