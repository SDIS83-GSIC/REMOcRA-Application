package remocra.api.endpoint

import fr.sdis83.remocra.authn.ApiRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.db.CommuneRepository
import remocra.db.OrganismeRepository
import remocra.db.TypeOrganismeRepository
import remocra.db.VoieRepository

@Path("/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiReferentielsCommunsEndpoint {
    @Inject
    lateinit var typeOrganismeRepository: TypeOrganismeRepository

    @Inject
    lateinit var communeRepository: CommuneRepository

    @Inject
    lateinit var voieRepository: VoieRepository

    @Inject
    lateinit var organismesRepository: OrganismeRepository

    @GET
    @Path("/typesOrganismes")
    @Operation(summary = "Retourne les types d'organismes susceptibles d'exploiter REMOcRA", tags = ["Référentiels communs"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    fun getRefentielNatureOrganismes(
        @Parameter(description = "Nombre maximum de résultats à retourner")
        @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
        @QueryParam("start") start: Int?,
    ): Response {
        return Response.ok(typeOrganismeRepository.getAll(limit, start)).build()
    }

    @GET
    @Path("/communes")
    @Operation(summary = "Retourne la liste des communes", tags = ["Référentiels communs"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    fun getRefentielCommunes(
        @Parameter(description = "Code INSEE de la commune")
        @QueryParam("codeInsee") codeInsee: String?,
        @Parameter(description = "Tout ou partie du nom de la commune")
        @QueryParam("libelle") libelle: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner")
        @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
        @QueryParam("start") offset: Int?,
    ): Response {
        return Response.ok(communeRepository.getAll(codeInsee, libelle, limit, offset)).build()
    }

    @GET
    @Path("/voies/{codeInsee}")
    @Operation(summary = "Retourne les voies d'une commune donnée", tags = ["Référentiels communs"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    fun getRefentielVoies(
        @Parameter(description = "Code INSEE de la commune", required = true)
        @PathParam("codeInsee") codeInsee: String?,
        @Parameter(description = "Tout ou partie du nom de la voie")
        @QueryParam("libelle") libelle: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner")
        @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
        @QueryParam("start") offset: Int?,
    ): Response {
        return Response.ok(voieRepository.getAll(codeInsee, libelle, limit, offset)).build()
    }

    @GET
    @Path("/organismes")
    @Operation(summary = "Retourne les organismes susceptibles d'exploiter REMOcRA (utilisateurs nommés avec accès à l'interface applicative ou exploitation de l'API)", tags = ["Référentiels communs"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    fun getRefentielOrganismes(
        @Parameter(description = "Code de la nature de l'organisme")
        @QueryParam("codeType") codeTypeOrganisme: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner")
        @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
        @QueryParam("start") offset: Int?,
    ): Response {
        return Response.ok(organismesRepository.getAll(codeTypeOrganisme, limit, offset)).build()
    }
}
