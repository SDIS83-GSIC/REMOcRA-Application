package remocra.web.evenementsouscategorie

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
import remocra.data.EvenementSousCategorieWithComplementData
import remocra.data.Params
import remocra.data.SousCategorieComplement
import remocra.db.EvenementSousCategorieRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.usecase.crise.evenementsouscategorie.CreateEvenementSousCategorieUseCase
import remocra.usecase.crise.evenementsouscategorie.DeleteEvenementSousCategorieUseCase
import remocra.usecase.crise.evenementsouscategorie.UpdateEvenementSousCategorieUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/evenement-sous-categorie")
@Produces(MediaType.APPLICATION_JSON)
class EvenementSousCategorieEndPoint : AbstractEndpoint() {

    @Inject lateinit var evenementSousCategorieRepository: EvenementSousCategorieRepository

    @Inject lateinit var createEvenementSousCategorieUseCase: CreateEvenementSousCategorieUseCase

    @Inject lateinit var updateEvenementSousCategorieUseCase: UpdateEvenementSousCategorieUseCase

    @Inject lateinit var deleteEvenementSousCategorieUseCase: DeleteEvenementSousCategorieUseCase

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(params: Params<EvenementSousCategorieRepository.Filter, EvenementSousCategorieRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                evenementSousCategorieRepository.getAllForAdmin(params),
                evenementSousCategorieRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun post(element: EvenementSousCategorieInput): Response {
        return createEvenementSousCategorieUseCase.execute(
            securityContext.userInfo,
            EvenementSousCategorieWithComplementData(
                evenementSousCategorieCode = element.evenementSousCategorieCode,
                evenementSousCategorieLibelle = element.evenementSousCategorieLibelle,
                evenementSousCategorieTypeGeometrie = element.evenementSousCategorieTypeGeometrie,
                evenementSousCategorieEvenementCategorieId = element.evenementSousCategorieEvenementCategorieId,
                evenementSousCategorieActif = element.evenementSousCategorieActif,
                evenementSousCategorieComplement = element.evenementSousCategorieComplement,
            ),
        ).wrap()
    }
    class EvenementSousCategorieInput {
        @FormParam("evenementSousCategorieCode")
        lateinit var evenementSousCategorieCode: String

        @FormParam("evenementSousCategorieLibelle")
        lateinit var evenementSousCategorieLibelle: String

        @FormParam("evenementSousCategorieTypeGeometrie")
        lateinit var evenementSousCategorieTypeGeometrie: TypeGeometry

        @FormParam("evenementSousCategorieEvenementCategorieId")
        lateinit var evenementSousCategorieEvenementCategorieId: UUID

        @FormParam("evenementSousCategorieActif")
        val evenementSousCategorieActif: Boolean = false

        @FormParam("evenementSousCategorieComplement")
        val evenementSousCategorieComplement: Collection<SousCategorieComplement> = emptyList()
    }

    @PUT
    @Path("/update/{evenementSousCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(
        @PathParam("evenementSousCategorieId")
        evenementSousCategorieId: UUID,
        element: EvenementSousCategorieInput,
    ): Response {
        return updateEvenementSousCategorieUseCase.execute(
            securityContext.userInfo,
            EvenementSousCategorieWithComplementData(
                evenementSousCategorieId = evenementSousCategorieId,
                evenementSousCategorieCode = element.evenementSousCategorieCode,
                evenementSousCategorieLibelle = element.evenementSousCategorieLibelle,
                evenementSousCategorieTypeGeometrie = element.evenementSousCategorieTypeGeometrie,
                evenementSousCategorieEvenementCategorieId = element.evenementSousCategorieEvenementCategorieId,
                evenementSousCategorieActif = element.evenementSousCategorieActif,
                evenementSousCategorieComplement = element.evenementSousCategorieComplement,
            ),
        ).wrap()
    }

    @GET
    @Path("/get/{evenementSousCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun get(
        @PathParam("evenementSousCategorieId")
        evenementSousCategorieId: UUID,
    ): Response {
        return Response.ok(evenementSousCategorieRepository.getById(evenementSousCategorieId)).build()
    }

    @DELETE
    @Path("/delete/{evenementSousCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun delete(
        @PathParam("evenementSousCategorieId")
        evenementSousCategorieId: UUID,
    ): Response {
        return deleteEvenementSousCategorieUseCase.execute(
            securityContext.userInfo,
            evenementSousCategorieRepository.getById(evenementSousCategorieId),
        ).wrap()
    }
}
