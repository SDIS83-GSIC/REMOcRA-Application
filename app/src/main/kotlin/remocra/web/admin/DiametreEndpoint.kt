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
import remocra.data.Params
import remocra.db.DiametreRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.usecase.admin.diametre.CreateDiametreUseCase
import remocra.usecase.admin.diametre.DeleteDiametreUseCase
import remocra.usecase.admin.diametre.UpdateDiametreUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/diametre")
class DiametreEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var diametreRepository: DiametreRepository

    @Inject
    lateinit var deleteDiametreUseCase: DeleteDiametreUseCase

    @Inject
    lateinit var createDiametreUseCase: CreateDiametreUseCase

    @Inject
    lateinit var updateDiametreUseCase: UpdateDiametreUseCase

    @Context
    lateinit var securityContext: SecurityContext

    class DiametreInput {
        @FormParam("code")
        lateinit var code: String

        @FormParam("libelle")
        lateinit var libelle: String

        @FormParam("actif")
        val actif: Boolean = true

        @FormParam("protected")
        val protected: Boolean = false
    }

    @POST
    @RequireDroits([Droit.ADMIN_DROITS])
    @Path("/create")
    fun createDiametre(diametreInput: DiametreInput): Response {
        return createDiametreUseCase.execute(
            securityContext.userInfo,
            Diametre(
                diametreId = UUID.randomUUID(),
                diametreCode = diametreInput.code,
                diametreLibelle = diametreInput.libelle,
                diametreActif = diametreInput.actif,
                diametreProtected = false,
            ),
        ).wrap()
    }

    @DELETE
    @RequireDroits([Droit.ADMIN_DROITS])
    @Path("/delete/{id}")
    fun deleteDiametre(@PathParam("id") id: UUID): Response {
        return deleteDiametreUseCase.execute(securityContext.userInfo, deleteDiametreUseCase.getDiametre(id)).wrap()
    }

    @PUT
    @RequireDroits([Droit.ADMIN_DROITS])
    @Path("/update/{id}")
    fun edit(@PathParam("id") id: UUID, diametreInput: DiametreInput): Response {
        return updateDiametreUseCase.execute(
            securityContext.userInfo,
            Diametre(
                diametreId = id,
                diametreCode = diametreInput.code,
                diametreLibelle = diametreInput.libelle,
                diametreActif = diametreInput.actif,
                diametreProtected = diametreInput.protected,
            ),
        ).wrap()
    }

    @POST
    @RequireDroits([Droit.ADMIN_DROITS])
    @Path("/get")
    fun get(params: Params<DiametreRepository.Filter, DiametreRepository.Sort>): Response {
        return Response.ok(DataTableau(diametreRepository.get(params), diametreRepository.getCount(params))).build()
    }

    @GET
    @RequireDroits([Droit.ADMIN_DROITS])
    @Path("/{id}")
    fun id(@PathParam("id") id: UUID): Response {
        return Response.ok(diametreRepository.getById(id)).build()
    }
}
