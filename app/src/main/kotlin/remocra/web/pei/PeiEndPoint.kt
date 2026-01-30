package remocra.web.pei

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
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
import org.locationtech.jts.geom.Geometry
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.DocumentsData.DocumentData
import remocra.data.DocumentsData.DocumentsPei
import remocra.data.Params
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.TypeElementCarte
import remocra.db.PeiRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.AbstractUseCase
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.document.UpsertDocumentPeiUseCase
import remocra.usecase.geometrie.CalculGeometrieUseCase
import remocra.usecase.geometrie.GetCoordonneesBySrid
import remocra.usecase.pei.CreatePeiUseCase
import remocra.usecase.pei.DeletePeiUseCase
import remocra.usecase.pei.MovePeiUseCase
import remocra.usecase.pei.PeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/pei")
@Produces(MediaType.APPLICATION_JSON)
class PeiEndPoint : AbstractEndpoint() {

    @Inject lateinit var peiUseCase: PeiUseCase

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject lateinit var movePeiUseCase: MovePeiUseCase

    @Inject lateinit var createPeiUseCase: CreatePeiUseCase

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    @Inject lateinit var deletePeiUseCase: DeletePeiUseCase

    @Inject lateinit var upsertDocumentPeiUseCase: UpsertDocumentPeiUseCase

    @Inject lateinit var getCoordonneesBySrid: GetCoordonneesBySrid

    @Inject lateinit var calculGeometrieUseCase: CalculGeometrieUseCase

    @Inject lateinit var getElementCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.PEI_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiWithFilter(params: Params<PeiRepository.Filter, PeiRepository.Sort>): Response {
        val listPei = peiUseCase.getPeiWithFilter(params, securityContext.userInfo)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilter(params.filterBy, securityContext.userInfo.zoneCompetence?.zoneIntegrationId, securityContext.userInfo.isSuperAdmin)),
        )
            .build()
    }

    @GET
    @Path("/")
    @RequireDroits([Droit.PEI_R])
    fun getPeiByZoneIntegrationShortData(): Response =
        Response.ok().entity(peiRepository.getPeiByZoneIntegrationShortData(securityContext.userInfo)).build()

    @GET
    @Path("/acces-rapide")
    @RequireDroits([Droit.PEI_R])
    fun getPeiForAccesRapide(
        @QueryParam("motifLibelle") motifLibelle: String,
    ): Response =
        Response.ok().entity(peiRepository.getPeiIdLibelleByMotif(securityContext.userInfo, motifLibelle)).build()

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
        val listPei = peiUseCase.getPeiWithFilterByIndisponibiliteTemporaire(params, idIndisponibiliteTemporaire, securityContext.userInfo)
        return Response.ok(
            DataTableau(
                listPei,
                peiRepository.countAllPeiWithFilterByIndisponibiliteTemporaire(
                    params.filterBy,
                    idIndisponibiliteTemporaire,
                    securityContext.userInfo.zoneCompetence?.zoneIntegrationId,
                    securityContext.userInfo.isSuperAdmin,
                ),
            ),
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
        val listPei = peiUseCase.getPeiWithFilterByTournee(params, idTournee, securityContext.userInfo)
        return Response.ok(
            DataTableau(
                listPei,
                peiRepository.countAllPeiWithFilterByTournee(
                    params.filterBy,
                    idTournee,
                    securityContext.userInfo.zoneCompetence?.zoneIntegrationId,
                    securityContext.userInfo.isSuperAdmin,
                ),
            ),
        )
            .build()
    }

    @GET
    @Path("/get-id-numero")
    @RequireDroits([Droit.PEI_R])
    fun getIdNumeroComplet(): Response {
        return Response.ok(peiRepository.getListIdNumeroCompletInZoneCompetence(securityContext.userInfo)).build()
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
    @RequireDroits([Droit.PEI_R, Droit.PEI_U, Droit.PEI_C, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_ADRESSE_C, Droit.PEI_DEPLACEMENT_U, Droit.PEI_NUMERO_INTERNE_U])
    fun getReferentielUpdateOrCreatePei(
        @QueryParam("coordonneeX") coordonneeX: String?,
        @QueryParam("coordonneeY") coordonneeY: String?,
        @QueryParam("srid") srid: Int,
        @QueryParam("peiId") peiId: UUID?,
    ): Response {
        val geometry = calculGeometrieUseCase.createPointWithSridFromCoordinates(coordonneeX, coordonneeY, srid)
        return Response.ok(peiUseCase.getInfoForUpdateOrCreate(geometry, peiId)).build()
    }

    @PUT
    @Path("/update")
    @RequireDroits([Droit.PEI_U, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_NUMERO_INTERNE_U, Droit.PEI_DEPLACEMENT_U, Droit.PEI_ADRESSE_C])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun update(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val pei = getPeiData(httpRequest)

        val result = updatePeiUseCase.execute(securityContext.userInfo, pei)

        // Si on n'a pas réussi à update le PEI
        if (result !is AbstractUseCase.Result.Success) {
            return result.wrap()
        }

        // On ne doit pas avoir accès à la gestion des documents PEI dans tous les contextes de modification PEI.
        // Il n'est donc pas nécessaire de mettre à jour les documents si l'utilisateur n'était pas en capacité de les modifier.
        if (securityContext.userInfo.hasDroit(droitWeb = Droit.PEI_U)) {
            val resultInsertDoc = upsertDocumentPeiUseCase.execute(
                securityContext.userInfo,
                DocumentsPei(
                    objectId = pei.peiId,
                    listDocument = objectMapper.readValue<List<DocumentData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
            )

            // Le type de retour attendu n'est pas celui du useCase des documents, mais bien un PeiData
            if (resultInsertDoc !is AbstractUseCase.Result.Success) {
                return resultInsertDoc.wrap()
            }
        }
        return result.wrap()
    }

    @POST
    @Path("/deplacer/{peiId}")
    @RequireDroits([Droit.PEI_DEPLACEMENT_U])
    fun updateLocalisation(
        @PathParam("peiId") peiId: UUID,
        geometry: Geometrie,
    ): Response {
        val peiData = movePeiUseCase.execute(geometry.geometry, peiId)
        return updatePeiUseCase.execute(securityContext.userInfo, peiData).wrap()
    }

    data class Geometrie(
        val geometry: Geometry,
    )

    @DELETE
    @Path("/delete/{peiId}")
    @RequireDroits([Droit.PEI_D])
    fun deletePei(@PathParam("peiId")peiId: UUID): Response {
        return deletePeiUseCase.execute(securityContext.userInfo, peiRepository.getInfoPei(peiId)).wrap()
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
        // Si on n'a pas réussi à insérer le PEI, on remonte l'erreur
        if (result !is AbstractUseCase.Result.Success && result !is AbstractUseCase.Result.Created) { return result.wrap() }

        val resultInsertDoc = upsertDocumentPeiUseCase.execute(
            securityContext.userInfo,
            DocumentsPei(
                objectId = pei.peiId,
                listDocument = objectMapper.readValue<List<DocumentData>>(httpRequest.getTextPart("documents")),
                listeDocsToRemove = listOf(), // Liste vide : à la création d'un pei, on ne peut pas retirer des éléments qui n'existaient pas
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
        )
        // En cas d'erreur à l'insertion des documents, on remonte l'erreur
        if (resultInsertDoc !is AbstractUseCase.Result.Success) { return resultInsertDoc.wrap() }
        // Sinon, le front attend le résultat de l'insertion du PEI
        return result.wrap()
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
    @RequireDroits([Droit.PEI_R, Droit.PEI_U, Droit.PEI_C, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_DEPLACEMENT_U, Droit.PEI_ADRESSE_C, Droit.PEI_NUMERO_INTERNE_U])
    fun getGeometrieByTypeSrid(
        @QueryParam("coordonneeX")
        coordonneeX: String?,
        @QueryParam("coordonneeY")
        coordonneeY: String?,
        @QueryParam("srid")
        srid: Int?,
    ): Response {
        if (coordonneeX.isNullOrEmpty() || coordonneeY.isNullOrEmpty() || srid == null) {
            return Response.ok(emptyList<GetCoordonneesBySrid.CoordonneesBySysteme>()).build()
        }
        return Response.ok(getCoordonneesBySrid.execute(coordonneeX, coordonneeY, srid)).build()
    }

    /**
     * Renvoie les points d'eau au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @Public("La couches des PEI est accessible en mode grand public (carte des risques)")
    fun layer(@QueryParam("bbox") bbox: String, @QueryParam("srid") srid: String): Response {
        return Response.ok(
            getElementCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.PEI,
                securityContext.userInfo,
            ),
        ).build()
    }

    @GET
    @Path("/{idPei}/geometrie")
    @RequireDroits([Droit.PEI_R])
    fun getGeometrieById(@PathParam("idPei") idPei: UUID): Response {
        return Response.ok(peiRepository.getGeometriePei(idPei)).build()
    }

    /**
     * Renvoie les points d'eau au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/hightlight/layer")
    @RequireDroits([Droit.PEI_R])
    fun hightlightLayer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
        @QueryParam("listePeiId") listePeiId: Set<UUID>,
    ): Response {
        return Response.ok(
            getElementCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.PEI_HIGHLIGHT,
                securityContext.userInfo,
                listePeiId = listePeiId,
            ),
        ).build()
    }
}
