package remocra.api.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.inject.Inject
import jakarta.validation.constraints.Max
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.api.usecase.ApiPeiUseCase
import remocra.auth.RequireDroitsApi
import remocra.auth.userInfo
import remocra.data.ApiPenaFormData
import remocra.data.ApiPibiFormData
import remocra.db.PeiRepository
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.AbstractUseCase
import remocra.usecase.modeleminimalpei.GetModeleMinimalPeiUseCase
import remocra.web.AbstractEndpoint

@Path("/deci/pei")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiPeiEndpoint : AbstractEndpoint() {
    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var peiUseCase: ApiPeiUseCase

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var getModeleMinimalPeiUseCase: GetModeleMinimalPeiUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    @GET
    @Path("")
    @Operation(
        summary = """Retourne la liste des PEI en lien avec l'organisme courant. Le lien est établi au sein de l'application REMOCRA par association entre un point d'eau et les types d’organismes suivants :
 *Autorité de police*
 *Service Public DECI*
 *Prestataire Technique pour le compte du Service Public DECI*
 *Service des eaux en charge de la distribution de l'eau pour les équipements sous pression (BI et PI)*
 Les PEI se répartissent selon leur type (PIBI pour les équipements reliés à réseau d'eau sous pression, PENA pour les autres ressources), leur nature (PIBI : Poteau (PI) ou borne (BI). PENA : Point d'aspiration , citerne, etc.) et le type de DECI (publique, privée, privée sous convention). Les caractéristiques techniques et les procédures applicables varient en fonction de ces critères de répartition.
 La structure de donnée retournée est conforme au modèle de donnée minimal défini par l'AFIGEO
 """,
        tags = ["DECI - Points d'Eau Incendie"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    fun getListPei(
        @Parameter(description = "Numéro INSEE de la commune où se trouve le PEI") @QueryParam("insee") codeInsee: String?,
        @Parameter(description = "Type du PEI : 'PIBI' ou 'PENA'") @QueryParam("type") type: TypePei?,
        @Parameter(description = "Nature du PEI") @QueryParam("codeNature") codeNature: String?,
        @Parameter(description = "Nature DECI : 'PRIVE', 'PUBLIC', 'CONVENTIONNE'") @QueryParam("codeNatureDECI") codeNatureDECI: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner (maximum fixé à 200 résultats)") @QueryParam("limit") @Max(value = 200) @DefaultValue("200") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") offset: Int?,
    ): Response {
        return Response.ok().entity(getModeleMinimalPeiUseCase.execute(codeInsee, type, codeNature, codeNatureDECI, limit, offset, securityContext.userInfo)).build()
    }

    @GET
    @Path("/{numeroComplet}")
    @Operation(summary = "Retourne les informations communes à tout type de PEI d'un PEI spécifique", tags = ["DECI - Points d'Eau Incendie"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    fun getPeiSpecifique(
        @Parameter(description = "Numéro du PEI") @PathParam("numeroComplet") numeroComplet: String,
    ): Response {
        return peiUseCase.getPeiSpecifiqueAsResult(numeroComplet, securityContext.userInfo).wrap()
    }

    @GET
    @Path("/{numeroComplet}/caracteristiques")
    @Operation(summary = "Retourne les caractéristiques techniques propres au PEI et à son type (PIBI ou PENA)", tags = ["DECI - Points d'Eau Incendie"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    fun getPeiCaracteristiques(
        @Parameter(description = "Numéro du PEI") @PathParam("numeroComplet") numeroComplet: String,
    ): Response {
        return peiUseCase.getPeiCaracteristiques(numeroComplet, securityContext.userInfo).wrap()
    }

    @PUT
    @Path("/{numeroComplet}/pibi-caracteristiques")
    @Operation(summary = "Modifie les caractéristiques techniques propres au PIBI", tags = ["DECI - Points d'Eau Incendie"])
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    fun updatePibiCaracteristiques(
        @Parameter(description = "Numéro du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Informations du PEI") peiForm: ApiPibiFormData,
    ): Response {
        return peiUseCase.updatePibiCaracteristiques(numeroComplet, peiForm, securityContext.userInfo).wrap()
    }

    @PUT
    @Path("/{numeroComplet}/pena-caracteristiques")
    @Operation(summary = "Modifie les caractéristiques techniques propres au PENA", tags = ["DECI - Points d'Eau Incendie"])
    @RequireDroitsApi([DroitApi.TRANSMETTRE])
    fun updatePenaCaracteristiques(
        @Parameter(description = "Numéro du PEI") @PathParam("numeroComplet") numeroComplet: String,
        @Parameter(description = "Informations du PEI") peiForm: ApiPenaFormData,
    ): Response {
        return peiUseCase.updatePenaCaracteristiques(numeroComplet, peiForm, securityContext.userInfo).wrap()
    }

    @GET
    @Path("/diff")
    @Operation(summary = "Liste des PEI ayant subit une modification (ajout, modification ou suppression) postérieure au *moment* passée en paramètre", tags = ["DECI - Points d'Eau Incendie"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    fun diff(
        @Parameter(description = "Moment à partir duquel retourner les résultats, format YYYY-MM-DD hh:mm", required = true) @QueryParam("moment") moment: String?,
    ): Response {
        // Quand tout s'est bien passé, on sérialise, sinon on wrap comme d'habitude
        val result = peiUseCase.diff(moment, securityContext.userInfo)
        if (result is AbstractUseCase.Result.Success) {
            return Response.ok(objectMapper.writeValueAsString(result.entity), MediaType.APPLICATION_JSON).build()
        }
        return result.wrap()
    }
}
