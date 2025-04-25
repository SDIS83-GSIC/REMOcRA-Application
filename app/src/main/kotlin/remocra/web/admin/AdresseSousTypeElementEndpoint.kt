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
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.AdresseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.AdresseSousTypeElement
import remocra.usecase.adresseSousTypeElement.CreateAdresseSousTypeElementUseCase
import remocra.usecase.adresseSousTypeElement.UpdateAdresseSousTypeElementUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/adresse-sous-type-element")
@Produces(MediaType.APPLICATION_JSON)
class AdresseSousTypeElementEndpoint : AbstractEndpoint() {

    @Inject lateinit var adresseRepository: AdresseRepository

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var createAdresseSousTypeElementUseCase: CreateAdresseSousTypeElementUseCase

    @Inject lateinit var updateAdresseSousTypeElementUseCase: UpdateAdresseSousTypeElementUseCase

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

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun create(adresseSousTypeElementInput: AdresseSousTypeElementInput): Response =
        createAdresseSousTypeElementUseCase.execute(
            userInfo = securityContext.userInfo,
            AdresseSousTypeElement(
                adresseSousTypeElementId = UUID.randomUUID(),
                adresseSousTypeElementCode = adresseSousTypeElementInput.adresseSousTypeElementCode,
                adresseSousTypeElementLibelle = adresseSousTypeElementInput.adresseSousTypeElementLibelle,
                adresseSousTypeElementActif = adresseSousTypeElementInput.adresseSousTypeElementActif,
                adresseSousTypeElementTypeElement = adresseSousTypeElementInput.adresseSousTypeElementTypeElement,
                adresseSousTypeElementTypeGeometrie = adresseSousTypeElementInput.adresseSousTypeElementTypeGeometrie,
            ),
        ).wrap()

    class AdresseSousTypeElementInput {
        @FormParam("adresseSousTypeElementCode")
        lateinit var adresseSousTypeElementCode: String

        @FormParam("adresseSousTypeElementLibelle")
        lateinit var adresseSousTypeElementLibelle: String

        @FormParam("adresseSousTypeElementActif")
        val adresseSousTypeElementActif: Boolean = true

        @FormParam("adresseSousTypeElementTypeElement")
        val adresseSousTypeElementTypeElement: UUID? = null

        @FormParam("adresseSousTypeElementTypeGeometrie")
        lateinit var adresseSousTypeElementTypeGeometrie: TypeGeometry
    }

    @PUT
    @Path("/update/{adresseSousTypeElementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun update(@PathParam("adresseSousTypeElementId") adresseSousTypeElementId: UUID, adresseSousTypeElementInput: AdresseSousTypeElementInput): Response =
        updateAdresseSousTypeElementUseCase.execute(
            securityContext.userInfo,
            AdresseSousTypeElement(
                adresseSousTypeElementId = adresseSousTypeElementId,
                adresseSousTypeElementCode = adresseSousTypeElementInput.adresseSousTypeElementCode,
                adresseSousTypeElementLibelle = adresseSousTypeElementInput.adresseSousTypeElementLibelle,
                adresseSousTypeElementActif = adresseSousTypeElementInput.adresseSousTypeElementActif,
                adresseSousTypeElementTypeElement = adresseSousTypeElementInput.adresseSousTypeElementTypeElement,
                adresseSousTypeElementTypeGeometrie = adresseSousTypeElementInput.adresseSousTypeElementTypeGeometrie,
            ),
        ).wrap()

    @GET
    @Path("/get/{adresseSousTypeElementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getSousTypeById(@PathParam("adresseSousTypeElementId") adresseSousTypeElementId: UUID) =
        Response.ok(adresseRepository.getById(adresseSousTypeElementId)).build()
}
