package remocra.api.endpoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.api.usecase.ApiIndisponibiliteTemporaireUseCase
import remocra.auth.RequireDroitsApi
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.web.AbstractEndpoint

@Path("/deci/indispoTemporaire")
@Produces(MediaType.APPLICATION_JSON)
class ApiIndispoTemporaireEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var apiIndisponibiliteTemporaireUseCase: ApiIndisponibiliteTemporaireUseCase

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
}
