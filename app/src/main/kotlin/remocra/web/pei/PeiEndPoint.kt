package remocra.web.pei

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
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
import remocra.app.AppSettings
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.PeiRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecases.document.UpsertDocumentPeiUseCase
import remocra.usecases.pei.CreatePeiUseCase
import remocra.usecases.pei.GetCoordonneesBySrid
import remocra.usecases.pei.PeiUseCase
import remocra.usecases.pei.UpdatePeiUseCase
import remocra.web.AbstractEndpoint
import remocra.web.getTextPart
import java.util.UUID
import kotlin.properties.Delegates

@Path("/pei")
@Produces(MediaType.APPLICATION_JSON)
class PeiEndPoint : AbstractEndpoint() {

    @Inject lateinit var peiUseCase: PeiUseCase

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject lateinit var createPeiUseCase: CreatePeiUseCase

    @Inject lateinit var upsertDocumentPeiUseCase: UpsertDocumentPeiUseCase

    @Inject lateinit var getCoordonneesBySrid: GetCoordonneesBySrid

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject lateinit var appSettings: AppSettings

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiWithFilter(params: Params<PeiRepository.Filter, PeiRepository.Sort>): Response {
        val listPei = peiUseCase.getPeiWithFilter(params)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilter(params)),
        )
            .build()
    }

    @GET
    @Path("/{idPei}")
    fun getInfoPei(
        @PathParam("idPei") idPei: UUID,
    ): Response {
        return Response.ok(peiUseCase.getInfoPei(idPei)).build()
    }

    @GET
    @Path("/referentiel-for-upsert-pei/")
    fun getReferentielUpdateOrCreatePei(
        @QueryParam("coordonneeX") coordonneeX: String?,
        @QueryParam("coordonneeY") coordonneeY: String?,
        @QueryParam("peiId") peiId: UUID?,
    ) =
        Response.ok(peiUseCase.getInfoForUpdateOrCreate(coordonneeX, coordonneeY, peiId)).build()

    @PUT
    @Path("/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun update(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val pei = getPeiData(httpRequest)

        val result = updatePeiUseCase.execute(securityContext.userInfo, pei)

        // Si on n'a pas réussi à update le PEI
        if (result !is Result.Success) {
            return result.wrap()
        }

        return upsertDocumentPeiUseCase.execute(
            securityContext.userInfo,
            UpsertDocumentPeiUseCase.DocumentsPei(
                peiId = pei.peiId,
                listDocument = objectMapper.readValue<List<UpsertDocumentPeiUseCase.DocumentData>>(httpRequest.getTextPart("documents")),
                documentIdToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("documentIdToRemove")),
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
        ).wrap()
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val pei = getPeiData(httpRequest)

        val result = createPeiUseCase.execute(
            securityContext.userInfo,
            pei,
        )

        // Si on n'a pas réussi à insérer le PEI
        if (result !is Result.Success) {
            return result.wrap()
        }

        return upsertDocumentPeiUseCase.execute(
            securityContext.userInfo,
            UpsertDocumentPeiUseCase.DocumentsPei(
                peiId = pei.peiId,
                listDocument = objectMapper.readValue<List<UpsertDocumentPeiUseCase.DocumentData>>(httpRequest.getTextPart("documents")),
                documentIdToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("documentIdToRemove")),
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
        ).wrap()
    }

    private fun getPeiData(httpRequest: HttpServletRequest) =
        if (httpRequest.getTextPart("peiTypePei") == TypePei.PIBI.literal) {
            objectMapper.readValue(httpRequest.getTextPart("peiData"), PibiData::class.java)
        } else {
            objectMapper.readValue(httpRequest.getTextPart("peiData"), PenaData::class.java)
        }

    /**
     * Permet de renvoyer les différentes coordonnées en fonction du système de projection
     * On passe les coordonnées en String, puisque en fonction du système de projection, ce n'est pas forcément un double
     */
    @GET
    @Path("/get-geometrie-by-srid")
    fun getGeometrieByTypeSrid(
        @QueryParam("coordonneeX")
        coordonneeX: String?,
        @QueryParam("coordonneeY")
        coordonneeY: String?,
        @QueryParam("srid")
        srid: Int?,
    ): Response {
        if (coordonneeX.isNullOrEmpty() || coordonneeY.isNullOrEmpty() || srid == null) {
            return Response.ok(listOf<GetCoordonneesBySrid.CoordonneesBySysteme>()).build()
        }
        return Response.ok(getCoordonneesBySrid.execute(coordonneeX, coordonneeY, srid)).build()
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

        @FormParam("peiAnneeFabrication")
        var peiAnneeFabrication: Int? = null

        @FormParam("peiServicePublicDeciId")
        lateinit var peiServicePublicDeciId: UUID

        @FormParam("peiMaintenanceDeciId")
        var peiMaintenanceDeciId: UUID? = null

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

        @get:FormParam("coordonneeX")
        var coordonneeX by Delegates.notNull<Double>()

        @get:FormParam("coordonneeY")
        var coordonneeY by Delegates.notNull<Double>()

        // //////////////// VALEURS POUR LES PIBI
        @FormParam("pibiDiametreId")
        var pibiDiametreId: UUID? = null

        @FormParam("pibiServiceEauId")
        var pibiServiceEauId: UUID? = null

        @FormParam("pibiNumeroScp")
        var pibiNumeroScp: String? = null

        @FormParam("pibiRenversable")
        var pibiRenversable: Boolean = false

        @FormParam("pibiDispositifInviolabilite")
        var pibiDispositifInviolabilite: Boolean = false

        @FormParam("pibiModeleId")
        var pibiModeleId: UUID? = null

        @FormParam("pibiMarqueId")
        var pibiMarqueId: UUID? = null

        @FormParam("pibiReservoirId")
        var pibiReservoirId: UUID? = null

        @FormParam("pibiDebitRenforce")
        var pibiDebitRenforce: Boolean = false

        @FormParam("pibiTypeCanalisationId")
        var pibiTypeCanalisationId: UUID? = null

        @FormParam("pibiTypeReseauId")
        var pibiTypeReseauId: UUID? = null

        @FormParam("pibiDiametreCanalisation")
        var pibiDiametreCanalisation: Int? = null

        @FormParam("pibiSurpresse")
        var pibiSurpresse: Boolean = false

        @FormParam("pibiAdditive")
        var pibiAdditive: Boolean = false

        @FormParam("pibiJumeleId")
        var pibiJumeleId: UUID? = null

        // DONNEES PENA
        @FormParam("penaMateriauId")
        var penaMateriauId: UUID? = null

        @FormParam("penaCapaciteIllimitee")
        var penaCapaciteIllimitee: Boolean = false

        @FormParam("penaCapaciteIncertaine")
        var penaCapaciteIncertaine: Boolean = false

        @FormParam("penaQuantiteAppoint")
        var penaQuantiteAppoint: Double? = null

        @FormParam("penaDisponibiliteHbe")
        val penaDisponibiliteHbe: Disponibilite? = null

        @FormParam("penaCapacite")
        var penaCapacite: Int? = null

        // On renvoie aussi les valeurs initiales pour la numérotation
        @FormParam("peiNumeroInterneInitial")
        var peiNumeroInterneInitial: Int? = null

        @FormParam("peiCommuneIdInitial")
        var peiCommuneIdInitial: UUID? = null

        @FormParam("peiZoneSpecialeIdInitial")
        var peiZoneSpecialeIdInitial: UUID? = null

        @FormParam("peiNatureDeciIdInitial")
        var peiNatureDeciIdInitial: UUID? = null

        @FormParam("peiDomaineIdInitial")
        var peiDomaineIdInitial: UUID? = null
    }
}
