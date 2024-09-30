package remocra.web.indisponibiliteTemporaire

import com.fasterxml.jackson.databind.ObjectMapper
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
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecases.indisponibiliteTemporaire.CloreIndisponibiliteTemporaireUseCase
import remocra.usecases.indisponibiliteTemporaire.CreateIndisponibiliteTemporaireUseCase
import remocra.usecases.indisponibiliteTemporaire.DeleteIndisponibiliteTemporaireUseCase
import remocra.usecases.indisponibiliteTemporaire.IndisponibiliteTemporaireUseCase
import remocra.usecases.indisponibiliteTemporaire.UpdateIndisponibiliteTemporaireUseCase
import remocra.web.AbstractEndpoint
import java.time.ZonedDateTime
import java.util.UUID

@Path("/indisponibilite-temporaire")
@Produces(MediaType.APPLICATION_JSON)
class IndisponibiliteTemporaireEndPoint() : AbstractEndpoint() {

    @Inject
    lateinit var indisponibiliteTemporaireUseCase: IndisponibiliteTemporaireUseCase

    @Inject
    lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    @Inject
    lateinit var createIndisponibiliteTemporaireUseCase: CreateIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var updateIndisponibiliteTemporaireUseCase: UpdateIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var cloreIndisponibiliteTemporaireUseCase: CloreIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var deleteIndisponibiliteTemporaireUseCase: DeleteIndisponibiliteTemporaireUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var objectMapper: ObjectMapper

    @POST
    @Path("/")
    @RequireDroits(
        [Droit.INDISPO_TEMP_R],
    )
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllIndisponibiliteTemporaire(params: Params<IndisponibiliteTemporaireRepository.Filter, IndisponibiliteTemporaireRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                list = indisponibiliteTemporaireUseCase.getAllWithListPei(params),
                count = indisponibiliteTemporaireRepository.countAllWithListPei(params.filterBy),
            ),
        )
            .build()
    }

    @POST
    @Path("/create")
    @RequireDroits(
        [Droit.INDISPO_TEMP_C],
    )
    @Produces(MediaType.APPLICATION_JSON)
    fun createIndisponibiliteTemporaire(
        indisponibiliteTemporaireInput: IndisponibiliteTemporaireInput,
    ): Response = createIndisponibiliteTemporaireUseCase.execute(
        userInfo = securityContext.userInfo,
        inputToData(UUID.randomUUID(), indisponibiliteTemporaireInput),
    ).wrap()

    @GET
    @Path("/{indisponibiliteTemporaireId}")
    @RequireDroits([Droit.INDISPO_TEMP_R])
    fun getIndisponibiliteTemporaireById(
        @PathParam("indisponibiliteTemporaireId") indisponibiliteTemporaireId: UUID,
    ): Response {
        return Response.ok(indisponibiliteTemporaireRepository.getWithListPeiById(indisponibiliteTemporaireId)).build()
    }

    @PUT
    @Path("/{indisponibiliteTemporaireId}")
    @RequireDroits(
        [Droit.INDISPO_TEMP_U],
    )
    @Produces(MediaType.APPLICATION_JSON)
    fun updateIndisponibiliteTemporaire(
        indisponibiliteTemporaireInput: IndisponibiliteTemporaireInput,
        @PathParam("indisponibiliteTemporaireId") indisponibiliteTemporaireId: UUID,
    ): Response = updateIndisponibiliteTemporaireUseCase.execute(
        userInfo = securityContext.userInfo,
        inputToData(indisponibiliteTemporaireId, indisponibiliteTemporaireInput),
    ).wrap()

    @PUT
    @Path("clore/{indisponibiliteTemporaireId}")
    @RequireDroits(
        [Droit.INDISPO_TEMP_U],
    )
    @Produces(MediaType.APPLICATION_JSON)
    fun cloreIndisponibiliteTemporaire(
        @PathParam("indisponibiliteTemporaireId") indisponibiliteTemporaireId: UUID,
    ): Response = cloreIndisponibiliteTemporaireUseCase.execute(
        userInfo = securityContext.userInfo,
        indisponibiliteTemporaireUseCase.getDataFromId(indisponibiliteTemporaireId),
    ).wrap()

    @DELETE
    @Path("/delete/{indisponibiliteTemporaireId}")
    @RequireDroits(
        [Droit.INDISPO_TEMP_D],
    )
    fun deleteIndisponibiliteTemporaire(
        @PathParam("indisponibiliteTemporaireId") indisponibiliteTemporaireId: UUID,
    ): Response = deleteIndisponibiliteTemporaireUseCase.execute(
        userInfo = securityContext.userInfo,
        indisponibiliteTemporaireUseCase.getDataFromId(indisponibiliteTemporaireId),
    ).wrap()
    private fun inputToData(id: UUID, indisponibiliteTemporaireInput: IndisponibiliteTemporaireInput): IndisponibiliteTemporaireData {
        return IndisponibiliteTemporaireData(
            indisponibiliteTemporaireId = id,
            indisponibiliteTemporaireMotif = indisponibiliteTemporaireInput.indisponibiliteTemporaireMotif,
            indisponibiliteTemporaireObservation = indisponibiliteTemporaireInput.indisponibiliteTemporaireObservation,
            indisponibiliteTemporaireDateFin = indisponibiliteTemporaireInput.indisponibiliteTemporaireDateFin,
            indisponibiliteTemporaireDateDebut = indisponibiliteTemporaireInput.indisponibiliteTemporaireDateDebut,
            indisponibiliteTemporaireMailAvantIndisponibilite = indisponibiliteTemporaireInput.indisponibiliteTemporaireMailAvantIndisponibilite,
            indisponibiliteTemporaireMailApresIndisponibilite = indisponibiliteTemporaireInput.indisponibiliteTemporaireMailApresIndisponibilite,
            indisponibiliteTemporaireBasculeAutoDisponible = indisponibiliteTemporaireInput.indisponibiliteTemporaireBasculeAutoDisponible,
            indisponibiliteTemporaireBasculeAutoIndisponible = indisponibiliteTemporaireInput.indisponibiliteTemporaireBasculeAutoIndisponible,
            indisponibiliteTemporaireListePeiId = indisponibiliteTemporaireInput.listePeiId,
        )
    }
}

class IndisponibiliteTemporaireInput {

    @FormParam("indisponibiliteTemporaireMotif")
    lateinit var indisponibiliteTemporaireMotif: String

    @FormParam("indisponibiliteTemporaireObservation")
    var indisponibiliteTemporaireObservation: String? = null

    @FormParam("indisponibiliteTemporaireDateDebut")
    lateinit var indisponibiliteTemporaireDateDebut: ZonedDateTime

    @FormParam("indisponibiliteTemporaireMailAvantIndisponibilite")
    var indisponibiliteTemporaireMailAvantIndisponibilite: Boolean = false

    @FormParam("indisponibiliteTemporaireMailApresIndisponibilite")
    var indisponibiliteTemporaireMailApresIndisponibilite: Boolean = false

    @FormParam("indisponibiliteTemporaireBasculeAutoDisponible")
    var indisponibiliteTemporaireBasculeAutoDisponible: Boolean = false

    @FormParam("indisponibiliteTemporaireBasculeAutoIndisponible")
    var indisponibiliteTemporaireBasculeAutoIndisponible: Boolean = false

    @FormParam("indisponibiliteTemporaireDateFin")
    var indisponibiliteTemporaireDateFin: ZonedDateTime? = null

    @FormParam("listePeiId")
    lateinit var listePeiId: Collection<UUID>
}
