package remocra.web.couverturehydraulique

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
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
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.DocumentsData
import remocra.data.EtudeData
import remocra.data.Params
import remocra.data.PeiProjetData
import remocra.data.couverturehydraulique.ReseauBatimentPeiProjet
import remocra.data.enums.TypeElementCarte
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.couverturehydraulique.CloreEtudeUseCase
import remocra.usecase.couverturehydraulique.CreateEtudeUseCase
import remocra.usecase.couverturehydraulique.CreatePeiProjetUseCase
import remocra.usecase.couverturehydraulique.DeletePeiProjetUseCase
import remocra.usecase.couverturehydraulique.ImportDataCouvertureHydrauliqueUseCase
import remocra.usecase.couverturehydraulique.UpdateEtudeUseCase
import remocra.usecase.couverturehydraulique.UpdatePeiProjetUseCase
import remocra.utils.forbidden
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/couverture-hydraulique")
@Produces(MediaType.APPLICATION_JSON)
class CouvertureHydrauliqueEndPoint : AbstractEndpoint() {

    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    @Inject lateinit var createPeiProjetUseCase: CreatePeiProjetUseCase

    @Inject lateinit var deletePeiProjetUseCase: DeletePeiProjetUseCase

    @Inject lateinit var updatePeiProjetUseCase: UpdatePeiProjetUseCase

    @Inject lateinit var updateEtudeUseCase: UpdateEtudeUseCase

    @Inject lateinit var createEtudeUseCase: CreateEtudeUseCase

    @Inject lateinit var cloreEtudeUseCase: CloreEtudeUseCase

    @Inject lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var importDataCouvertureHydrauliqueUseCase: ImportDataCouvertureHydrauliqueUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var objectMapper: ObjectMapper

    @POST
    @Path("/")
    @RequireDroits([Droit.ETUDE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getEtude(params: Params<CouvertureHydrauliqueRepository.Filter, CouvertureHydrauliqueRepository.Sort>): Response {
        val affiliatedOrganismeIds = securityContext.userInfo!!.affiliatedOrganismeIds
        return Response.ok(
            DataTableau(
                couvertureHydrauliqueRepository.getEtudes(params, affiliatedOrganismeIds),
                couvertureHydrauliqueRepository.getCountEtudes(params.filterBy, affiliatedOrganismeIds),
            ),
        ).build()
    }

    @GET
    @Path("/type-etudes")
    @RequireDroits([Droit.ETUDE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getTypeEtudes(): Response {
        return Response.ok(couvertureHydrauliqueRepository.getTypeEtudes()).build()
    }

    @POST
    @Path("/etude/{etudeId}/pei-projet/create")
    @RequireDroits([Droit.ETUDE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun createPeiProjet(
        @PathParam("etudeId")
        etudeId: UUID,
        peiProjetInput: PeiProjetInput,
    ): Response {
        return createPeiProjetUseCase.execute(
            securityContext.userInfo,
            PeiProjetData(
                peiProjetId = UUID.randomUUID(),
                peiProjetEtudeId = etudeId,
                peiProjetDiametreId = peiProjetInput.peiProjetDiametreId,
                peiProjetNatureDeciId = peiProjetInput.peiProjetNatureDeciId,
                peiProjetTypePeiProjet = peiProjetInput.peiProjetTypePeiProjet,
                peiProjetCapacite = peiProjetInput.peiProjetCapacite,
                peiProjetDebit = peiProjetInput.peiProjetDebit,
                peiProjetDiametreCanalisation = peiProjetInput.peiProjetDiametreCanalisation,
                peiProjetGeometrie = peiProjetInput.peiProjetGeometrie,
            ),
        ).wrap()
    }

    @GET
    @Path("/pei-projet/{peiProjetId}")
    @RequireDroits([Droit.ETUDE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiProjet(
        @PathParam("peiProjetId")
        peiProjetId: UUID,
    ): Response {
        return Response.ok(
            couvertureHydrauliqueRepository.getPeiProjet(peiProjetId),
        ).build()
    }

    @PUT
    @Path("/etude/{etudeId}/pei-projet/{peiProjetId}")
    @RequireDroits([Droit.ETUDE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun updatePeiProjet(
        @PathParam("etudeId")
        etudeId: UUID,
        @PathParam("peiProjetId")
        peiProjetId: UUID,
        peiProjetInput: PeiProjetInput,
    ): Response {
        return updatePeiProjetUseCase.execute(
            securityContext.userInfo,
            PeiProjetData(
                peiProjetId = peiProjetId,
                peiProjetEtudeId = etudeId,
                peiProjetDiametreId = peiProjetInput.peiProjetDiametreId,
                peiProjetNatureDeciId = peiProjetInput.peiProjetNatureDeciId,
                peiProjetTypePeiProjet = peiProjetInput.peiProjetTypePeiProjet,
                peiProjetCapacite = peiProjetInput.peiProjetCapacite,
                peiProjetDebit = peiProjetInput.peiProjetDebit,
                peiProjetDiametreCanalisation = peiProjetInput.peiProjetDiametreCanalisation,
                peiProjetGeometrie = peiProjetInput.peiProjetGeometrie,
            ),
        ).wrap()
    }

    @PUT
    @Path("/pei-projet/move/{peiProjetId}")
    @RequireDroits([Droit.ETUDE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun movePeiProjet(
        @PathParam("peiProjetId")
        peiProjetId: UUID,
        geometry: Geometrie,
    ): Response {
        val peiProjetData = couvertureHydrauliqueRepository.getPeiProjet(peiProjetId)
        return updatePeiProjetUseCase.execute(
            securityContext.userInfo,
            peiProjetData.copy(
                peiProjetGeometrie = geometry.geometry,
            ),
        ).wrap()
    }

    data class Geometrie(
        val geometry: Geometry,
    )

    @DELETE
    @Path("/pei-projet/{peiProjetId}")
    @RequireDroits([Droit.ETUDE_U])
    fun deletePeiProjet(
        @PathParam("peiProjetId")
        peiProjetId: UUID,
    ): Response {
        val peiProjetData = couvertureHydrauliqueRepository.getPeiProjet(peiProjetId)
        return deletePeiProjetUseCase.execute(securityContext.userInfo, peiProjetData).wrap()
    }

    data class PeiProjetInput(
        val peiProjetNatureDeciId: UUID,
        val peiProjetTypePeiProjet: TypePeiProjet,
        val peiProjetDiametreId: UUID? = null,
        val peiProjetDiametreCanalisation: Int? = null,
        val peiProjetCapacite: Int? = null,
        val peiProjetDebit: Int? = null,
        val peiProjetGeometrie: Geometry,
    )

    @GET
    @Path("/etude/{etudeId}")
    @RequireDroits([Droit.ETUDE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getEtude(
        @PathParam("etudeId")
        etudeId: UUID,
    ): Response {
        return Response.ok(couvertureHydrauliqueRepository.getEtude(etudeId)).build()
    }

    @POST
    @Path("/etude/clore/{etudeId}")
    @RequireDroits([Droit.ETUDE_D])
    @Produces(MediaType.APPLICATION_JSON)
    fun cloreEtude(
        @PathParam("etudeId")
        etudeId: UUID,
    ): Response {
        return Response.ok(cloreEtudeUseCase.execute(securityContext.userInfo, etudeId)).build()
    }

    @PUT
    @Path("/etude/{etudeId}")
    @RequireDroits([Droit.ETUDE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateEtude(
        @PathParam("etudeId")
        etudeId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        // On crée un objet Etude
        val etudeData = EtudeData(
            typeEtudeId = UUID.fromString(httpRequest.getTextPart("typeEtudeId")),
            etudeId = etudeId,
            etudeNumero = httpRequest.getTextPart("etudeNumero"),
            etudeLibelle = httpRequest.getTextPart("etudeLibelle"),
            etudeDescription = httpRequest.getTextPart("etudeDescription"),
            listeCommuneId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeCommuneId")),
            listeDocument = DocumentsData.DocumentsEtude(
                objectId = etudeId,
                listDocument = objectMapper.readValue<List<DocumentsData.DocumentEtudeData>>(httpRequest.getTextPart("documents")),
                listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
        )

        return updateEtudeUseCase.execute(
            userInfo = securityContext.userInfo,
            etudeData,
        ).wrap()
    }

    @POST
    @Path("/etude/create")
    @RequireDroits([Droit.ETUDE_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun createEtude(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val etudeId = UUID.randomUUID()

        val etudeData = EtudeData(
            etudeId = etudeId,
            typeEtudeId = UUID.fromString(httpRequest.getTextPart("typeEtudeId")),
            etudeNumero = httpRequest.getTextPart("etudeNumero"),
            etudeLibelle = httpRequest.getTextPart("etudeLibelle"),
            etudeDescription = httpRequest.getTextPart("etudeDescription"),
            listeCommuneId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeCommuneId")),
            listeDocument = DocumentsData.DocumentsEtude(
                objectId = etudeId,
                listDocument = objectMapper.readValue<List<DocumentsData.DocumentEtudeData>>(httpRequest.getTextPart("documents")),
                listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
        )

        return createEtudeUseCase.execute(
            securityContext.userInfo,
            etudeData,
        ).wrap()
    }

    @PUT
    @Path("/import/{etudeId}")
    @RequireDroits([Droit.ETUDE_U])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importData(
        @PathParam("etudeId")
        etudeId: UUID,
        @Context httpRequest: HttpServletRequest,
    ) =
        importDataCouvertureHydrauliqueUseCase.execute(
            securityContext.userInfo,
            ReseauBatimentPeiProjet(
                etudeId,
                if (httpRequest.getPart("fileReseau").contentType != null) httpRequest.getPart("fileReseau").inputStream else null,
                if (httpRequest.getPart("fileBatiment").contentType != null) httpRequest.getPart("fileBatiment").inputStream else null,
                if (httpRequest.getPart("filePeiProjet").contentType != null) httpRequest.getPart("filePeiProjet").inputStream else null,
            ),
        ).wrap()

    /**
     * Renvoie les points d'eau au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @RequireDroits([Droit.ETUDE_R])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
        @QueryParam("etudeId") etudeId: UUID,
    ): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                etudeId,
                TypeElementCarte.PEI_PROJET,
                securityContext.userInfo!!,
            ),
        ).build()
    }
}
