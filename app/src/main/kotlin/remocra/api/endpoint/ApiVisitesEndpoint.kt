package remocra.api.endpoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.api.usecase.ApiVisitesUseCase
import remocra.auth.RequireDroitsApi
import remocra.data.ApiVisiteFormData
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.exception.RemocraResponseException
import remocra.web.AbstractEndpoint
import java.io.IOException

@Path("/api/deci/pei/{numeroComplet}/visites")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiVisitesEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var apiVisitesUseCase: ApiVisitesUseCase

    @GET
    @Path("")
    @Operation(
        summary = "Retourne les visites associées à un PEI. Une visite correspond à une séance terrain auprès d'un PEI à " +
            "une date donnée dans un contexte périodique (contrôle technique périodique, reconnaissance opérationnelle périodique) ou " +
            "non (reconnaissance initiale, intervention). Chaque visite permet, selon son type de récolter de l'information sur le PEI " +
            "(mesure de débit et de pression, présence d'anomalies empêchant le fonctionnement,etc) qui conditionnent la disponibilité " +
            "du PEI",
        tags = ["DECI - Visites"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(IOException::class)
    fun getPeiVisites(
        @Parameter(description = "Numéro complet du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Code du type de visite") @QueryParam("codeTypeVisite") codeTypeVisite: String?,
        @Parameter(description = "Moment à partir duquel retourner les résultats, format YYYY-MM-DD hh:mm") @QueryParam("moment") moment: String?,
        @Parameter(description = "Renvoyer uniquement la dernière visite") @QueryParam("derniereOnly") derniereOnly: Boolean?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Int?,
    ): Response {
        return apiVisitesUseCase.getAll(numeroComplet, codeTypeVisite, moment, derniereOnly, limit, offset).wrap()
    }

    @POST
    @Path("")
    @Operation(
        summary = "Ajoute une visite à un PEI en précisant le type de visite, une liste de points contrôlés et une " +
            "liste d'anomalies éventuellement constatées. Des mesures de débit et de pression peuvent également être transmises dans le" +
            " cas d'un \"Contrôle Technique Périodique\"",
        tags = ["DECI - Visites"],
    )
    @ApiResponse(responseCode = "201", description = "Visite créée avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    @Throws(RemocraResponseException::class)
    fun addVisite(
        @Parameter(description = "Numéro complet du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Informations de la visite", required = true) form: ApiVisiteFormData,
    ): Response {
        return apiVisitesUseCase.addVisite(numeroComplet, form).wrap()
    }

    @GET
    @Path("/{idVisite}")
    @Operation(summary = "Retourne l'information détaillée d'une visite spécifique dont les éventuelles informations de débit et pressions", tags = ["DECI - Visites"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(IOException::class)
    fun getVisiteSpecifique(
        @Parameter(description = "Numéro complet du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") idVisite: String,
    ): Response {
        return apiVisitesUseCase.getVisiteSpecifique(numeroComplet, idVisite).wrap()
    }

    @PUT
    @Path("/{idVisite}")
    @Operation(summary = "Modifie les informations relatives à une visite spécifique", tags = ["DECI - Visites"])
    @ApiResponse(responseCode = "200", description = "Visite modifiée avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    @Throws(RemocraResponseException::class)
    fun updateVisite(
        @Parameter(description = "Numéro complet du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") idVisite: String,
        @Parameter(description = "Informations de la visite", required = true) form: ApiVisiteFormData,
    ): Response {
        return apiVisitesUseCase.updateVisite(numeroComplet, idVisite, form).wrap()
    }

    @DELETE
    @Path("/{idVisite}")
    @Operation(summary = "Supprime une visite spécifique", tags = ["DECI - Visites"])
    @ApiResponse(responseCode = "200", description = "Visite supprimée avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    fun deleteVisite(
        @Parameter(description = "Numéro complet du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") idVisite: String,
    ): Response {
        return apiVisitesUseCase.deleteVisite(numeroComplet, idVisite).wrap()
    }
}
