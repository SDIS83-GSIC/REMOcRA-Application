package remocra.web.pei

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
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
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.PeiRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.document.UpsertDocumentPeiUseCase
import remocra.usecase.pei.CreatePeiUseCase
import remocra.usecase.pei.GetCoordonneesBySrid
import remocra.usecase.pei.PeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase
import remocra.web.AbstractEndpoint
import remocra.web.forbidden
import remocra.web.geometryFromBBox
import remocra.web.getTextPart
import remocra.web.notFound
import remocra.web.toGeomFromText
import java.util.UUID

@Path("/pei")
@Produces(MediaType.APPLICATION_JSON)
class PeiEndPoint : AbstractEndpoint() {

    @Inject lateinit var peiUseCase: PeiUseCase

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject lateinit var createPeiUseCase: CreatePeiUseCase

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    @Inject lateinit var upsertDocumentPeiUseCase: UpsertDocumentPeiUseCase

    @Inject lateinit var getCoordonneesBySrid: GetCoordonneesBySrid

    @Inject lateinit var objectMapper: ObjectMapper

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.PEI_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiWithFilter(params: Params<PeiRepository.Filter, PeiRepository.Sort>): Response {
        val listPei = peiUseCase.getPeiWithFilter(params)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilter(params.filterBy)),
        )
            .build()
    }

    @POST
    @Path("/get-by-indispo/{idIndisponibiliteTemporaire}")
    @RequireDroits([Droit.PEI_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiByIndisponibiliteTemporaire(
        @PathParam("idIndisponibiliteTemporaire") idIndisponibiliteTemporaire: UUID,
        params: Params<
            PeiRepository.Filter,
            PeiRepository.Sort,
            >,
    ): Response {
        val listPei = peiUseCase.getPeiWithFilterByIndisponibiliteTemporaire(params, idIndisponibiliteTemporaire)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilterByIndisponibiliteTemporaire(params.filterBy, idIndisponibiliteTemporaire)),
        )
            .build()
    }

    @POST
    @Path("/get-by-tournee/{idTournee}")
    @RequireDroits([Droit.PEI_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiByTournee(
        @PathParam("idTournee") idTournee: UUID,
        params: Params<
            PeiRepository.Filter,
            PeiRepository.Sort,
            >,
    ): Response {
        val listPei = peiUseCase.getPeiWithFilterByTournee(params, idTournee)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilterByTournee(params.filterBy, idTournee)),
        )
            .build()
    }

    @GET
    @Path("/get-id-numero")
    @RequireDroits([Droit.PEI_R])
    fun getIdNumeroComplet(): Response {
        return Response.ok(peiRepository.getListIdNumeroCompletInZoneCompetence(securityContext.userInfo?.organismeId)).build()
    }

    @GET
    @Path("/{idPei}")
    @RequireDroits([Droit.PEI_R])
    fun getInfoPei(
        @PathParam("idPei") idPei: UUID,
    ): Response {
        return Response.ok(peiUseCase.getInfoPei(idPei)).build()
    }

    @GET
    @Path("/referentiel-for-upsert-pei/")
    @RequireDroits([Droit.PEI_U, Droit.PEI_C, Droit.PEI_CARACTERISTIQUES_U])
    fun getReferentielUpdateOrCreatePei(
        @QueryParam("coordonneeX") coordonneeX: String?,
        @QueryParam("coordonneeY") coordonneeY: String?,
        @QueryParam("peiId") peiId: UUID?,
    ) =
        Response.ok(peiUseCase.getInfoForUpdateOrCreate(coordonneeX, coordonneeY, peiId)).build()

    @PUT
    @Path("/update")
    @RequireDroits([Droit.PEI_U, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_NUMERO_INTERNE_U, Droit.PEI_DEPLACEMENT_U])
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
                objectId = pei.peiId,
                listDocument = objectMapper.readValue<List<UpsertDocumentPeiUseCase.DocumentData>>(httpRequest.getTextPart("documents")),
                listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
        ).wrap()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.PEI_C])
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
                objectId = pei.peiId,
                listDocument = objectMapper.readValue<List<UpsertDocumentPeiUseCase.DocumentData>>(httpRequest.getTextPart("documents")),
                listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("documentIdToRemove")),
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
    @RequireDroits([Droit.PEI_U, Droit.PEI_C, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_DEPLACEMENT_U])
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

    /**
     * Renvoie les points d'eau au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @RequireDroits([Droit.PEI_R])
    fun layer(@QueryParam("bbox") bbox: String?, @QueryParam("srid") srid: String?): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }
        val zoneCompetence = utilisateurRepository.getZoneByUtilisateurId(securityContext.userInfo!!.utilisateurId) ?: return notFound().build()

        return Response.ok(
            LayersRes(
                features =
                bbox.let {
                    if (it.isNullOrEmpty()) {
                        peiRepository.getPointsWithinZone(zoneCompetence.zoneIntegrationId)
                    } else {
                        val geom = geometryFromBBox(bbox, srid) ?: return notFound().build()
                        peiRepository.getPointsWithinZoneAndBbox(zoneCompetence.zoneIntegrationId, geom.toGeomFromText())
                    }
                }.map {
                    Feature(
                        geometry = FeatureGeom(
                            type = it.peiGeometrie.geometryType,
                            coordinates = it.peiGeometrie.coordinates.map { c -> arrayOf(c.x, c.y) }.first(),
                            srid = "EPSG:${it.peiGeometrie.srid}",
                        ),
                        id = it.peiId,
                        properties = it,
                    )
                },
            ),
        ).build()
    }

    // Data classes pour renvoyer les données au format GeoJSON
    data class LayersRes(
        val type: String = "FeatureCollection",
        val features: List<Feature>,
    )

    data class Feature(
        val type: String = "Feature",
        val geometry: FeatureGeom,
        val id: UUID,
        val properties: Any,
    )

    data class FeatureGeom(
        val type: String,
        val coordinates: Array<Double>,
        val srid: String,
    )
}
