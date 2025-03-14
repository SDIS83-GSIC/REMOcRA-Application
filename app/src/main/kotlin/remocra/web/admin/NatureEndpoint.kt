package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
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
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.NatureWithDiametres
import remocra.data.Params
import remocra.db.NatureRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.nature.CreateNatureUseCase
import remocra.usecase.nature.DeleteNatureUseCase
import remocra.usecase.nature.UpdateNatureUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/nature")
class NatureEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var natureRepository: NatureRepository

    @Inject
    lateinit var createNatureUseCase: CreateNatureUseCase

    @Inject
    lateinit var updateNatureUseCase: UpdateNatureUseCase

    @Inject
    lateinit var deleteNatureUseCase: DeleteNatureUseCase

    @Context
    lateinit var securityContext: SecurityContext

    class NatureInput {
        @FormParam("actif")
        val actif: Boolean = true

        @FormParam("code")
        lateinit var code: String

        @FormParam("libelle")
        lateinit var libelle: String

        @FormParam("typePei")
        lateinit var typePei: String

        @FormParam("protected")
        val protected: Boolean = false

        @FormParam("diametreIds")
        lateinit var diametreIds: Collection<UUID>
    }

    @POST
    @Path("/get")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun get(params: Params<NatureRepository.Filter, NatureRepository.Sort>): Response {
        return Response.ok(DataTableau(natureRepository.getTable(params), natureRepository.getCount(params))).build()
    }

    @GET
    @Path("/get/{id}")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun id(@PathParam("id") id: UUID): Response {
        return Response.ok(natureRepository.getByIdWithDiametres(id)).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun createNature(natureInput: NatureInput): Response {
        return createNatureUseCase.execute(
            securityContext.userInfo,
            NatureWithDiametres(
                natureId = UUID.randomUUID(),
                natureActif = natureInput.actif,
                natureCode = natureInput.code,
                natureLibelle = natureInput.libelle,
                TypePei.valueOf(natureInput.typePei),
                natureProtected = natureInput.protected,
                diametreIds = natureInput.diametreIds,
            ),
        ).wrap()
    }

    @PUT
    @Path("/update/{id}")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun updateNature(@PathParam("id") id: UUID, natureInput: NatureInput): Response {
        return updateNatureUseCase.execute(
            securityContext.userInfo,
            NatureWithDiametres(
                natureId = id,
                natureActif = natureInput.actif,
                natureCode = natureInput.code,
                natureLibelle = natureInput.libelle,
                TypePei.valueOf(natureInput.typePei),
                natureProtected = natureInput.protected,
                diametreIds = natureInput.diametreIds,
            ),
        ).wrap()
    }

    @DELETE
    @Path("/delete/{id}")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun remove(@PathParam("id") id: UUID): Response {
        return deleteNatureUseCase.execute(securityContext.userInfo, natureRepository.getById(id)!!).wrap()
    }
}
