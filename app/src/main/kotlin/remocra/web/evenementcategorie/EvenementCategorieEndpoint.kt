package remocra.web.evenementcategorie

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
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.EvenementCategorieWithTypeCriseData
import remocra.data.Params
import remocra.db.EvenementCategorieRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.TypeCrise
import remocra.usecase.crise.evenementcategorie.CreateEvenementCategorieUseCase
import remocra.usecase.crise.evenementcategorie.DeleteEvenementCategorieUseCase
import remocra.usecase.crise.evenementcategorie.UpdateEvenementCategorieUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/evenement-categorie")
@Produces(MediaType.APPLICATION_JSON)
class EvenementCategorieEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var evenementCategorieRepository: EvenementCategorieRepository

    @Inject
    lateinit var createEvenementCategorieUseCase: CreateEvenementCategorieUseCase

    @Inject
    lateinit var updateEvenementCategorieUseCase: UpdateEvenementCategorieUseCase

    @Inject
    lateinit var deleteEvenementCategorieUseCase: DeleteEvenementCategorieUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(params: Params<EvenementCategorieRepository.Filter, EvenementCategorieRepository.Sort>): Response = Response.ok(
        DataTableau(
            evenementCategorieRepository.getAllForAdmin(params),
            evenementCategorieRepository.countAllForAdmin(params.filterBy),
        ),
    ).build()

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun post(element: EvenementCategorieInput): Response = createEvenementCategorieUseCase.execute(
        securityContext.userInfo,
        EvenementCategorieWithTypeCriseData(
            evenementCategorieCode = element.evenementCategorieCode,
            evenementCategorieLibelle = element.evenementCategorieLibelle,
            evenementCategorieActif = element.evenementCategorieActif,
            evenementCategorieTypeCrise = element.evenementCategorieTypeCrise,
        ),
    ).wrap()

    class EvenementCategorieInput {
        @FormParam("evenementCategorieCode")
        lateinit var evenementCategorieCode: String

        @FormParam("evenementCategorieLibelle")
        lateinit var evenementCategorieLibelle: String

        @FormParam("evenementCategorieActif")
        val evenementCategorieActif: Boolean = false

        @FormParam("evenementCategorieTypeCrise")
        val evenementCategorieTypeCrise: Collection<TypeCrise> = emptyList()
    }

    @PUT
    @Path("/update/{evenementCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(
        @PathParam("evenementCategorieId")
        evenementCategorieId: UUID,
        element: EvenementCategorieInput,
    ): Response {
        return updateEvenementCategorieUseCase.execute(
            securityContext.userInfo,
            EvenementCategorieWithTypeCriseData(
                evenementCategorieId = evenementCategorieId,
                evenementCategorieCode = element.evenementCategorieCode,
                evenementCategorieLibelle = element.evenementCategorieLibelle,
                evenementCategorieActif = element.evenementCategorieActif,
                evenementCategorieTypeCrise = element.evenementCategorieTypeCrise,
            ),
        ).wrap()
    }

    @GET
    @Path("/get/{evenementCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun get(
        @PathParam("evenementCategorieId")
        evenementCategorieId: UUID,
    ): Response = Response.ok(evenementCategorieRepository.getById(evenementCategorieId)).build()

    @DELETE
    @Path("/delete/{evenementCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun delete(
        @PathParam("evenementCategorieId")
        evenementCategorieId: UUID,
    ): Response = deleteEvenementCategorieUseCase.execute(
        securityContext.userInfo,
        evenementCategorieRepository.getById(evenementCategorieId),
    ).wrap()
}
