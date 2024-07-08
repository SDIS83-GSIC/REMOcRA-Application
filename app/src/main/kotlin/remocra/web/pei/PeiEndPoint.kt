package remocra.web.pei

import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
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
import remocra.authn.userInfo
import remocra.data.PeiData
import remocra.db.PeiRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecases.pei.PeiUseCase
import remocra.usecases.pei.UpdatePeiUseCase
import java.util.UUID

@Path("/pei")
@Produces(MediaType.APPLICATION_JSON)
class PeiEndPoint {

    @Inject lateinit var peiUseCase: PeiUseCase

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiWithFilter(params: Params): Response {
        val listPei = peiUseCase.getPeiWithFilter(params)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilter(params)),
        )
            .build()
    }

    data class DataTableau(

        val list: List<PeiRepository.PeiForTableau>?,
        val count: Int,

    )
    data class Params(
        @QueryParam("limit")
        val limit: Int? = 10,
        @QueryParam("offset")
        val offset: Int? = 0,
        @QueryParam("filterBy")
        val filterBy: PeiRepository.Filter?,
        @QueryParam("sortBy")
        val sortBy: PeiRepository.Sort?,
    )

    @GET
    @Path("/{idPei}")
    fun getInfoPei(
        @PathParam("idPei") idPei: UUID,
    ): Response {
        return Response.ok(peiUseCase.getInfoPei(idPei)).build()
    }

    @GET
    @Path("/referentiel-for-update-pei") // TODO idPei
    fun getReferentielUpdatePei() =
        Response.ok(peiUseCase.getInfoForUpdate()).build()

    @PUT
    @Path("/update/{idPei}")
    fun update(
        @PathParam("idPei") idPei: UUID,
        peiInput: PeiInput,
    ): Response {
        return Response.ok(
            updatePeiUseCase.execute(
                securityContext.userInfo,
                PeiData(
                    peiId = idPei,
                    peiTypePei = peiInput.peiTypePei,
                    peiNumeroInterne = peiInput.peiNumeroInterne,
                    peiNumeroComplet = peiInput.peiNumeroComplet,
                    peiDisponibiliteTerrestre = peiInput.peiDisponibiliteTerrestre!!,
                    peiAutoriteDeciId = peiInput.peiAutoriteDeciId,
                    peiServicePublicDeciId = peiInput.peiServicePublicDeciId,
                    peiMaintenanceDeciId = peiInput.peiMaintenanceDeci,
                    peiCommuneId = peiInput.peiCommuneId,
                    peiVoieId = peiInput.peiVoieId,
                    peiNumeroVoie = peiInput.peiNumeroVoie,
                    peiSuffixeVoie = peiInput.peiSuffixeVoie,
                    peiLieuDitId = peiInput.peiLieuDitId,
                    peiCroisementId = peiInput.peiCroisementId,
                    peiComplementAdresse = peiInput.peiComplementAdresse,
                    peiEnFace = peiInput.peiEnFace,
                    peiDomaineId = peiInput.peiDomaineId,
                    peiNatureId = peiInput.peiNatureId,
                    peiSiteId = peiInput.peiSiteId,
                    peiGestionnaireId = peiInput.peiGestionnaireId,
                    peiNatureDeciId = peiInput.peiNatureDeciId,
                    peiZoneSpecialeId = null,
                    peiAnneeFabrication = null,
                    peiNiveauId = peiInput.peiNiveauId,
                    peiObservation = null,
                ),
            ),
        ).build()
    }

    class PeiInput {
        @FormParam("peiId")
        val peiId: UUID? = null

        @FormParam("peiNumeroInterne")
        val peiNumeroInterne: Int? = null // Dans le cadre d'une création

        @FormParam("peiNumeroComplet")
        val peiNumeroComplet: String? = null // Dans le cadre d'une création

        @FormParam("peiTypePei")
        lateinit var peiTypePei: TypePei

        @FormParam("peiAutoriteDeciId")
        lateinit var peiAutoriteDeciId: UUID

        @FormParam("peiDisponibiliteTerrestre")
        val peiDisponibiliteTerrestre: Disponibilite? = null

        @FormParam("peiServicePublicDeciId")
        lateinit var peiServicePublicDeciId: UUID

        @FormParam("peiMaintenanceDeci")
        var peiMaintenanceDeci: UUID? = null

        @FormParam("peiCommuneId")
        lateinit var peiCommuneId: UUID

        @FormParam("peiVoieId")
        lateinit var peiVoieId: UUID

        @FormParam("peiNumeroVoie")
        val peiNumeroVoie: Int? = null

        @FormParam("peiSuffixeVoie")
        val peiSuffixeVoie: String? = null

        @FormParam("peiLieuDitId")
        val peiLieuDitId: UUID? = null

        @FormParam("peiCroisementId")
        val peiCroisementId: UUID? = null

        @FormParam("peiComplementAdresse")
        val peiComplementAdresse: String? = null

        @FormParam("peiEnFace")
        val peiEnFace: Boolean = false

        @FormParam("peiDomaineId")
        lateinit var peiDomaineId: UUID

        @FormParam("peiNatureId")
        lateinit var peiNatureId: UUID

        @FormParam("peiSiteId")
        var peiSiteId: UUID? = null

        @FormParam("peiGestionnaireId")
        var peiGestionnaireId: UUID? = null

        @FormParam("peiNatureDeciId")
        lateinit var peiNatureDeciId: UUID

        @FormParam("peiNiveauId")
        var peiNiveauId: UUID? = null
    }
}
