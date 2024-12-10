package remocra.web.oldeb

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
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
import remocra.data.oldeb.OldebProprietaireForm
import remocra.db.ProprietaireRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.oldebproprietaire.CreateProprietaireUseCase
import remocra.usecase.oldebproprietaire.DeleteProprietaireUseCase
import remocra.usecase.oldebproprietaire.UpdateProprietaireUseCase
import remocra.utils.badRequest
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/proprietaire")
@Produces(MediaType.APPLICATION_JSON)
class ProprietaireEndpoint : AbstractEndpoint() {

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var proprietaireRepository: ProprietaireRepository

    @Inject lateinit var createProprietaireUseCase: CreateProprietaireUseCase

    @Inject lateinit var updateProprietaireUseCase: UpdateProprietaireUseCase

    @Inject lateinit var deleteProprietaireUseCase: DeleteProprietaireUseCase

    @POST
    @Path("/")
    @RequireDroits([Droit.OLDEB_R])
    fun list(params: Params<ProprietaireRepository.Filter, ProprietaireRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                proprietaireRepository.getList(params),
                proprietaireRepository.getCount(params.filterBy),
            ),
        ).build()

    @POST
    @Path("/create")
    @RequireDroits([Droit.OLDEB_C])
    fun post(element: OldebProprietaireForm): Response =
        createProprietaireUseCase.execute(securityContext.userInfo, element).wrap()

    @GET
    @Path("/{proprietaireId}")
    @RequireDroits([Droit.OLDEB_U])
    fun get(@PathParam("proprietaireId") proprietaireId: UUID): Response =
        Response.ok(
            proprietaireRepository.get(proprietaireId).let {
                    element ->
                OldebProprietaireForm(
                    oldebProprietaireId = element.oldebProprietaireId,
                    oldebProprietaireOrganisme = element.oldebProprietaireOrganisme,
                    oldebProprietaireRaisonSociale = element.oldebProprietaireRaisonSociale,
                    oldebProprietaireCivilite = element.oldebProprietaireCivilite,
                    oldebProprietaireNom = element.oldebProprietaireNom,
                    oldebProprietairePrenom = element.oldebProprietairePrenom,
                    oldebProprietaireTelephone = element.oldebProprietaireTelephone,
                    oldebProprietaireEmail = element.oldebProprietaireEmail,
                    oldebProprietaireNumVoie = element.oldebProprietaireNumVoie,
                    oldebProprietaireVoie = element.oldebProprietaireVoie,
                    oldebProprietaireLieuDit = element.oldebProprietaireLieuDit,
                    oldebProprietaireCodePostal = element.oldebProprietaireCodePostal,
                    oldebProprietaireVille = element.oldebProprietaireVille,
                    oldebProprietairePays = element.oldebProprietairePays,
                )
            },
        ).build()

    @PUT
    @Path("/{proprietaireId}")
    @RequireDroits([Droit.OLDEB_U])
    fun put(@PathParam("proprietaireId") proprietaireId: UUID, element: OldebProprietaireForm): Response {
        if (proprietaireId != element.oldebProprietaireId) {
            return badRequest().build()
        }
        return updateProprietaireUseCase.execute(securityContext.userInfo, element).wrap()
    }

    @DELETE
    @Path("/{proprietaireId}")
    @RequireDroits([Droit.OLDEB_D])
    fun delete(@PathParam("proprietaireId") proprietaireId: UUID): Response =
        deleteProprietaireUseCase.execute(securityContext.userInfo, proprietaireRepository.get(proprietaireId)).wrap()
}
