package remocra.web.crise

import jakarta.inject.Inject
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
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.CriseData
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.CriseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.usecase.crise.CreateCriseUseCase
import remocra.usecase.crise.CriseUseCase
import remocra.usecase.crise.UpdateCriseUseCase
import remocra.web.AbstractEndpoint
import java.time.ZonedDateTime
import java.util.UUID

@Path("/crise")
@Produces(MediaType.APPLICATION_JSON)
class CriseEndpoint : AbstractEndpoint() {

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var criseUseCase: CriseUseCase

    @Inject lateinit var createCriseUseCase: CreateCriseUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var updateCriseUseCase: UpdateCriseUseCase

    data class CriseInput(
        val criseLibelle: String? = null,
        val criseDescription: String? = null,
        val criseDateDebut: ZonedDateTime? = null,
        val criseDateFin: ZonedDateTime? = null,
        val typeCrise: UUID? = null,
        val criseStatutType: TypeCriseStatut = TypeCriseStatut.EN_COURS,
        val listeCommuneId: Collection<UUID>? = null,
        val listeToponymieId: Collection<UUID>? = null,
    )

    @POST
    @Path("/")
    @RequireDroits([Droit.CRISE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getCrise(params: Params<CriseRepository.FilterCrise, CriseRepository.SortCrise>): Response {
        return Response.ok(
            DataTableau(
                criseRepository.getCrises(params),
                criseRepository.getCountCrises(params.filterBy),
            ),
        ).build()
    }

    @GET
    @Path("/get-type-crise")
    @Public("Les types de crises ne sont pas liées à un droit.")
    fun getTypeCriseForSelect(): Response {
        return Response.ok(
            criseUseCase.getTypeCriseForSelect(),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.CRISE_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun createCrise(criseInput: CriseInput): Response {
        return createCriseUseCase.execute(
            securityContext.userInfo,
            CriseData(
                criseId = UUID.randomUUID(),
                criseLibelle = criseInput.criseLibelle,
                criseDescription = criseInput.criseDescription,
                criseDateDebut = criseInput.criseDateDebut,
                criseDateFin = criseInput.criseDateFin,
                criseTypeCriseId = criseInput.typeCrise,
                criseStatutType = criseInput.criseStatutType,
                listeCommuneId = criseInput.listeCommuneId,
                listeToponymieId = criseInput.listeToponymieId,
            ),
        ).wrap()
    }

    @GET
    @Path("/{criseId}")
    @RequireDroits([Droit.CRISE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getCrise(
        @PathParam("criseId")
        criseId: UUID,
    ): Response {
        return Response.ok(criseRepository.getCrise(criseId)).build()
    }

    @PUT
    @Path("/{criseId}/update")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateCrise(
        @PathParam("criseId")
        criseId: UUID,
        criseInput: CriseInput,
    ): Response {
        return updateCriseUseCase.execute(
            userInfo = securityContext.userInfo,
            CriseData(
                criseId = criseId,
                criseLibelle = criseInput.criseLibelle,
                criseDescription = criseInput.criseDescription,
                criseDateDebut = criseInput.criseDateDebut,
                criseDateFin = criseInput.criseDateFin,
                criseTypeCriseId = criseInput.typeCrise,
                criseStatutType = criseInput.criseStatutType,
                listeCommuneId = criseInput.listeCommuneId,
                listeToponymieId = criseInput.listeToponymieId,
            ),
        ).wrap()
    }

    @POST
    @Path("/{criseId}/clore/")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun cloreCrise(
        @PathParam("criseId")
        criseId: UUID,
        criseInput: CriseInput,
    ): Response {
        val crise = criseRepository.getCrise(criseId)
        val criseData =
            CriseData(
                criseId = criseId,
                criseLibelle = crise.criseLibelle,
                criseDescription = crise.criseDescription,
                criseDateDebut = crise.criseDateDebut,
                criseDateFin = criseInput.criseDateFin,
                criseTypeCriseId = crise.typeCriseId,
                criseStatutType = TypeCriseStatut.TERMINEE,
                listeCommuneId = crise.listeCommune,
                listeToponymieId = crise.listeToponymie,
            )
        return updateCriseUseCase.execute(
            userInfo = securityContext.userInfo,
            criseData,
        ).wrap()
    }
}
