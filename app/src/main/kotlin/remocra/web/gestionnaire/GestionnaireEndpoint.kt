package remocra.web.gestionnaire

import com.google.inject.Inject
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
import remocra.db.GestionnaireRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.usecase.gestionnaire.CreateGestionnaireUseCase
import remocra.usecase.gestionnaire.UpdateGestionnaireUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/gestionnaire")
@Produces(MediaType.APPLICATION_JSON)
class GestionnaireEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    lateinit var updateGestionnaireUseCase: UpdateGestionnaireUseCase

    @Inject
    lateinit var createGestionnaireUseCase: CreateGestionnaireUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/get")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAll(): Response =
        Response.ok(gestionnaireRepository.getAll()).build()

    @POST
    @Path("/")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAll(params: Params<GestionnaireRepository.Filter, GestionnaireRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = gestionnaireRepository.getAllForAdmin(params),
                count = gestionnaireRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @PUT
    @Path("/update/{gestionnaireId}")
    @RequireDroits([Droit.GEST_SITE_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(@PathParam("gestionnaireId") gestionnaireId: UUID, gestionnaireInput: GestionnaireInput): Response =
        updateGestionnaireUseCase.execute(
            securityContext.userInfo,
            Gestionnaire(
                gestionnaireId = gestionnaireId,
                gestionnaireActif = gestionnaireInput.gestionnaireActif,
                gestionnaireCode = gestionnaireInput.gestionnaireCode,
                gestionnaireLibelle = gestionnaireInput.gestionnaireLibelle,
            ),

        ).wrap()

    class GestionnaireInput {
        @FormParam("gestionnaireCode")
        lateinit var gestionnaireCode: String

        @FormParam("gestionnaireLibelle")
        lateinit var gestionnaireLibelle: String

        @FormParam("gestionnaireActif")
        var gestionnaireActif: Boolean = true
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.GEST_SITE_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(gestionnaireInput: GestionnaireInput): Response =
        createGestionnaireUseCase.execute(
            securityContext.userInfo,
            Gestionnaire(
                gestionnaireId = UUID.randomUUID(),
                gestionnaireActif = gestionnaireInput.gestionnaireActif,
                gestionnaireCode = gestionnaireInput.gestionnaireCode,
                gestionnaireLibelle = gestionnaireInput.gestionnaireLibelle,
            ),
        ).wrap()

    @GET
    @Path("/{gestionnaireId}")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getById(@PathParam("gestionnaireId") gestionnaireId: UUID): Response =
        Response.ok(gestionnaireRepository.getById(gestionnaireId)).build()
}
