package remocra.api.endpoint

import com.fasterxml.jackson.core.JsonProcessingException
import fr.sdis83.remocra.authn.ApiRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.NomenclaturesProvider
import remocra.data.enums.TypeNomenclature
import remocra.db.jooq.enums.TypePei
import remocra.db.jooq.tables.pojos.MarquePibi
import remocra.db.jooq.tables.pojos.ModelePibi
import remocra.db.jooq.tables.pojos.Nature
import remocra.web.limitOffset

@Path("deci/referentiel/pibi")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiReferentielsPibiEndpoint {

    @Inject
    lateinit var nomenclaturesProvider: NomenclaturesProvider

    @GET
    @Path("/naturesPEI")
    @Operation(summary = "Retourne les natures de PEI possibles pour les PEI de type PIBI", tags = ["DECI - Référentiels PIBI"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesPEI(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.NATURE)
                .values
                .filter { TypePei.PIBI == (it as Nature).natureTypePei }
                .limitOffset(limit, offset),
        ).build()
    }

    // TODO nature : voir si on a toujours besoin de discriminer les diamètres par nature, et si oui, comment
    // en attendant, on retourne tous les diamètres
    @GET
    @Path("/diametres")
//    @Path("/diametres/{codeNature}")
    @Operation(summary = "Retourne les diamètres de demi-raccord possibles une nature de PIBI", tags = ["DECI - Référentiels PIBI"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielDiametres(
//        @Parameter(description = "Code de nature PIBI") @PathParam("codeNature") codeNature: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.DIAMETRE)
                .values
                .filter { TypePei.PIBI == (it as Nature).natureTypePei }
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("{codeNature}/naturesAnomalies")
    @Operation(
        summary = "Retourne les types d'anomalies pouvant être constatées pour une nature de PIBI et un contexte " +
            "(type de visite) spécifiques",
        tags = ["DECI - Référentiels PIBI"],
    )
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesAnomalies(
//        @Parameter(description = "Nature du PIBI") @PathParam("codeNature") codeNature: String?,
//        @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite") contexteVisite: String?,
//        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Int?,
//        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Int?,
    ): Response {
        TODO()
    }

    @GET
    @Path("/marques")
    @Operation(summary = "Retourne les marques susceptibles d'équiper le parc de PIBI", tags = ["DECI - Référentiels PIBI"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielMarques(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.MARQUE_PIBI)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/modeles")
    @Operation(summary = "Retourne les modèles susceptibles d'équiper le parc de PIBI", tags = ["DECI - Référentiels PIBI"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielModeles(
        @Parameter(description = "Code de la marque") @QueryParam("codeMarque") codeMarque: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        @Suppress("UNCHECKED_CAST")
        val data = nomenclaturesProvider.getData(TypeNomenclature.MODELE_PIBI)
            .values
            .let { it as Collection<ModelePibi> }

        // On va chercher l'identifiant de la marque dont le code est passé
        @Suppress("UNCHECKED_CAST")
        val marquePibiId = nomenclaturesProvider.getData(TypeNomenclature.MARQUE_PIBI)
            .values
            .let { it as Collection<MarquePibi> }
            .find { marque -> codeMarque == marque.marquePibiCode }
            ?.marquePibiId
        if (marquePibiId != null) {
            // Si la marque est trouvée, on filtre dessus
            return Response.ok(
                data
                    .filter { it.modelePibiMarqueId == marquePibiId }
                    .limitOffset(limit, offset),
            ).build()
        }
        return Response.ok(
            data
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/typeReseau")
    @Operation(summary = "Retourne le type de réseau", tags = ["DECI - Référentiels PIBI"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielTypesReseau(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.TYPE_RESEAU)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/typeCanalisation")
    @Operation(summary = "Retourne le type de canalisation", tags = ["DECI - Référentiels PIBI"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielTypesCanalisation(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.TYPE_CANALISATION)
                .values
                .limitOffset(limit, offset),
        ).build()
    }
}
