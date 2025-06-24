package remocra.web.typecrisecategorie

import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.TypeCriseCatagorieRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/type-crise-categorie")
@Produces(MediaType.APPLICATION_JSON)
class TypeCriseCategorieEndpoint : AbstractEndpoint() {

    @Inject lateinit var typeCriseCatagorieRepository: TypeCriseCatagorieRepository

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(params: Params<TypeCriseCatagorieRepository.Filter, TypeCriseCatagorieRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                typeCriseCatagorieRepository.getAllForAdmin(params),
                typeCriseCatagorieRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
    }
}
