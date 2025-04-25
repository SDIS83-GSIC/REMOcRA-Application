package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.AdresseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/adresse-sous-type-element")
@Produces(MediaType.APPLICATION_JSON)
class AdresseSousTypeElementEndpoint : AbstractEndpoint() {

    @Inject lateinit var adresseRepository: AdresseRepository

    @POST
    @Path("/get/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getAllSousTypeElement(params: Params<AdresseRepository.Filter, AdresseRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                adresseRepository.getAllForAdmin(params),
                adresseRepository.countAllForAdmin(params),
            ),
        ).build()

    @GET
    @Path("/ref")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getRef() = Response.ok(adresseRepository.getType()).build()
}
