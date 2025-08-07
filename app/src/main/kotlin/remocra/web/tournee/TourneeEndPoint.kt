package remocra.web.tournee

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.Params
import remocra.db.TourneeRepository
import remocra.db.TourneeRepository.PeiTourneeForDnD
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.security.NoCsrf
import remocra.usecase.tournee.CreateTourneeUseCase
import remocra.usecase.tournee.DeleteTourneeUseCase
import remocra.usecase.tournee.DesaffecterTourneeUseCase
import remocra.usecase.tournee.FetchTourneeDataUseCase
import remocra.usecase.tournee.ForcerAvancementTourneeUseCase
import remocra.usecase.tournee.GenereCarteTourneeUseCase
import remocra.usecase.tournee.UpdateLTourneePeiUseCase
import remocra.usecase.tournee.UpdateTourneeUseCase
import remocra.usecase.visites.FetchTourneeVisiteUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/tournee")
@Produces(MediaType.APPLICATION_JSON)
class TourneeEndPoint : AbstractEndpoint() {

    @Inject
    lateinit var tourneeRepository: TourneeRepository

    @Inject
    lateinit var fetchTourneeDataUseCase: FetchTourneeDataUseCase

    @Inject
    lateinit var fetchTourneeVisiteUseCase: FetchTourneeVisiteUseCase

    @Inject
    lateinit var createTourneeUseCase: CreateTourneeUseCase

    @Inject
    lateinit var updateTourneeUseCase: UpdateTourneeUseCase

    @Inject
    lateinit var deleteTourneeUseCase: DeleteTourneeUseCase

    @Inject
    lateinit var updateLTourneePeiUseCase: UpdateLTourneePeiUseCase

    @Inject
    lateinit var desaffecterTourneeUseCase: DesaffecterTourneeUseCase

    @Inject
    lateinit var forcerAvancementTourneeUseCase: ForcerAvancementTourneeUseCase

    @Inject
    lateinit var genereCarteTourneeUseCase: GenereCarteTourneeUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/")
    @RequireDroits([Droit.TOURNEE_R])
    fun getTourneeByZoneIntegrationShortData(): Response =
        Response.ok().entity(tourneeRepository.getTourneeByZoneIntegrationShortData(securityContext.userInfo)).build()

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun fetchTourneeData(
        params: Params<TourneeRepository.Filter, TourneeRepository.Sort>,
    ): Response {
        return Response.ok().entity(fetchTourneeDataUseCase.fetchTourneeData(params, securityContext.userInfo)).build()
    }

    @GET
    @Path("/acces-rapide")
    @RequireDroits([Droit.TOURNEE_R, Droit.PEI_R])
    fun getTourneeForAccesRapide(
        @QueryParam("motifLibelle") motifLibelle: String,
    ): Response =
        Response.ok().entity(tourneeRepository.getTourneeIdLibelleByMotif(securityContext.userInfo, motifLibelle)).build()

    @GET
    @Path("/actives")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun getTournees(
        @QueryParam("isPrive")
        isPrive: Boolean,
    ): Response {
        return Response.ok().entity(
            tourneeRepository.getTourneesActives(
                securityContext.userInfo.isSuperAdmin,
                securityContext.userInfo.affiliatedOrganismeIds!!,
                isPrive,
                null,
                null,
            ),
        ).build()
    }

    @GET
    @Path("/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_A])
    fun getTourneeById(@PathParam("tourneeId") tourneeId: UUID): Response =
        Response.ok().entity(tourneeRepository.getTourneeById(tourneeId)).build()

    @GET
    @Path("/genere-carte-tournee/{tourneeId}")
    @Produces("application/pdf")
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun getGenereCarteTournee(@PathParam("tourneeId") tourneeId: UUID): Response {
        val carteTournee = genereCarteTourneeUseCase.getCarteTournee(tourneeId)
        return Response.ok(carteTournee.file)
            .header("Content-Disposition", "attachment; filename=\"${carteTournee.fileName}\"")
            .build()
    }

    @POST
    @Path("/createTournee")
    @RequireDroits([Droit.TOURNEE_A])
    fun createTournee(tourneeInput: TourneeInput): Response =
        createTourneeUseCase.execute(
            userInfo = securityContext.userInfo,
            element = Tournee(
                tourneeId = UUID.randomUUID(),
                tourneeActif = true,
                tourneeOrganismeId = tourneeInput.tourneeOrganismeId,
                tourneeLibelle = tourneeInput.tourneeLibelle,
                tourneePourcentageAvancement = null,
                tourneeReservationUtilisateurId = null,
                tourneeDateSynchronisation = null,
            ),
        ).wrap()

    @PUT
    @Path("/updateTournee")
    @RequireDroits([Droit.TOURNEE_A])
    fun updateTournee(tourneeInput: TourneeInput): Response {
        val tourneeToUpdate = tourneeRepository.getTourneeById(tourneeInput.tourneeId!!).copy(tourneeLibelle = tourneeInput.tourneeLibelle)
        return updateTourneeUseCase.execute(userInfo = securityContext.userInfo, element = tourneeToUpdate).wrap()
    }

    class TourneeInput {
        @FormParam("tourneeId")
        val tourneeId: UUID? = null

        @FormParam("tourneeLibelle")
        lateinit var tourneeLibelle: String

        @FormParam("tourneeOrganismeId")
        lateinit var tourneeOrganismeId: UUID
    }

    @DELETE
    @Path("/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_A])
    fun deleteTournee(@PathParam("tourneeId") tourneeId: UUID) =
        deleteTourneeUseCase.execute(
            userInfo = securityContext.userInfo,
            element = tourneeRepository.getTourneeById(tourneeId),
        ).wrap()

    @GET
    @Path("/listPeiTournee/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_A])
    fun getListPeiTournee(
        @PathParam("tourneeId") tourneeId: UUID,
        @QueryParam("listePeiId") listePeiId: Set<UUID>?,
    ): Response =
        Response.ok().entity(
            DataToSendTourneePei(
                tourneeLibelle = tourneeRepository.getTourneeLibelleById(tourneeId = tourneeId),
                organismeLibelle = tourneeRepository.getTourneeOrganismeLibelleById(tourneeId = tourneeId),
                listPeiTournee = tourneeRepository.getAllPeiByTourneeIdForDnD(tourneeId = tourneeId, listePeiId).sortedBy { it.lTourneePeiOrdre },
            ),
        ).build()

    data class DataToSendTourneePei(
        val tourneeLibelle: String,
        val organismeLibelle: String,
        val listPeiTournee: List<PeiTourneeForDnD>,
    )

    @GET
    @Path("/listPei/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_A])
    fun getPeiForDnD(
        @PathParam("tourneeId") tourneeId: UUID,
    ): Response =
        Response.ok().entity(tourneeRepository.getPeiForDnD(tourneeId)).build()

    @PUT
    @Path("/listPeiTournee/update/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_A])
    fun updateLTourneePei(@PathParam("tourneeId") tourneeId: UUID, @Context httpRequest: HttpServletRequest): Response =
        updateLTourneePeiUseCase.execute(
            userInfo = securityContext.userInfo,
            element = UpdateLTourneePeiUseCase.LTourneePeiToInsert(
                tourneeId = tourneeId,
                listLTourneePei = objectMapper.readValue<List<LTourneePei>>(httpRequest.getTextPart("listTourneePei")),
            ),
        ).wrap()

    @GET
    @Path("/fetchTourneeVisiteInfo/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun fetchTourneeVisiteUseCase(@PathParam("tourneeId") tourneeId: UUID): Response =
        Response.ok().entity(fetchTourneeVisiteUseCase.fetchTourneeVisite(tourneeId)).build()

    @POST
    @Path("/desaffecter/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_FORCER_POURCENTAGE_E])
    fun desaffectationTournee(@PathParam("tourneeId") tourneeId: UUID): Response =
        desaffecterTourneeUseCase.execute(
            userInfo = securityContext.userInfo,
            element = tourneeRepository.getTourneeById(tourneeId).copy(tourneeReservationUtilisateurId = null),
        ).wrap()

    @POST
    @Path("/avancement-force-0/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_FORCER_POURCENTAGE_E])
    fun setAvancementTournee0(@PathParam("tourneeId") tourneeId: UUID): Response =
        forcerAvancementTourneeUseCase.execute(
            userInfo = securityContext.userInfo,
            element = tourneeRepository.getTourneeById(tourneeId).copy(tourneePourcentageAvancement = 0),
        ).wrap()

    @POST
    @Path("/avancement-force-100/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_FORCER_POURCENTAGE_E])
    fun setAvancementTournee100(@PathParam("tourneeId") tourneeId: UUID): Response =
        forcerAvancementTourneeUseCase.execute(
            userInfo = securityContext.userInfo,
            element = tourneeRepository.getTourneeById(tourneeId).copy(tourneePourcentageAvancement = 100),
        ).wrap()

    @GET
    @Path("/{tourneePei}/geometrie")
    @RequireDroits([Droit.TOURNEE_R])
    fun getGeometrieById(@PathParam("tourneePei") tourneePei: UUID): Response {
        return Response.ok(tourneeRepository.getGeometrieTournee(tourneePei)).build()
    }

    @GET
    @Path("/get-libelle-tournee")
    @RequireDroits([Droit.TOURNEE_R, Droit.PEI_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getTourneeForSelect(): Response {
        return Response.ok(
            tourneeRepository.getTourneeForSelect(),
        )
            .build()
    }
}
