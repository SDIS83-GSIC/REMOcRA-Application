package remocra.api.endpoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.inject.Inject
import jakarta.validation.constraints.NotNull
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
import remocra.api.usecase.ApiIndisponibiliteTemporaireUseCase
import remocra.auth.RequireDroitsApi
import remocra.auth.userInfo
import remocra.data.ApiIndispoTempFormData
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/deci/indispoTemporaire")
@Produces(MediaType.APPLICATION_JSON)
class ApiIndispoTemporaireEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var apiIndisponibiliteTemporaireUseCase: ApiIndisponibiliteTemporaireUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("")
    @Operation(
        summary = "Retourne la liste des indisponibilités temporaires de REMOcRA",
        tags = ["DECI - Indispo temporaire"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    fun getIndispoTemporaire(
        @Parameter(description = "Code de l'organisme API étant à l'origine de l'indisponibilité temporaire")
        @QueryParam("organismeApi") organismeAPI: String?,
        @Parameter(description = "Ne renvoie que les indisponibilités temporaires liées à ce PEI") @QueryParam("numeroComplet") numeroComplet: String?,
        @Parameter(description = "Code du statut de l'indisponibilité temporaire (EN_COURS, PLANIFIEE ou TERMINEE)") @QueryParam("statut") statut: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Int?,
    ): Response {
        return Response.ok(
            apiIndisponibiliteTemporaireUseCase.getAll(organismeAPI, numeroComplet, statut, limit, offset),
        )
            .build()
    }

    @POST
    @Path("")
    @Operation(summary = "Ajoute une nouvelle indisponibilité temporaire", tags = ["DECI - Indispo temporaire"])
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    fun addIndispoTemporaire(
        @NotNull @Parameter(description = "Informations de l'indisponibilité temporaire")
        indispoForm: ApiIndispoTempFormData,
    ): Response {
        return apiIndisponibiliteTemporaireUseCase.addIndispoTemp(indispoForm, securityContext.userInfo).wrap()
    }

    @PUT
    @Path("/{idIndisponibiliteTemporaire}")
    @Operation(
        summary = "Modifie les informations relatives à une indisponibilité temporaire",
        tags = ["DECI - Indispo temporaire"],
    )
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    fun editIndispoTemporaire(
        @Parameter(description = "Identifiant de l'indisponibilité temporaire)")
        @PathParam("idIndisponibiliteTemporaire") idIndispo: UUID,
        @Parameter(
            description = "Informations d'indisponibilite temporaire",
            required = true,
        ) @NotNull apiIndispoTempFormData: ApiIndispoTempFormData,
    ): Response {
        return apiIndisponibiliteTemporaireUseCase.updateIndispoTemp(apiIndispoTempFormData, idIndispo, securityContext.userInfo).wrap()
    }
}
