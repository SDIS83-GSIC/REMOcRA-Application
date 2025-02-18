package remocra.web.permis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
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
import remocra.data.DocumentsData
import remocra.data.PermisData
import remocra.data.PermisDataToFront
import remocra.data.enums.TypePointCarte
import remocra.db.PermisRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Permis
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.permis.CreatePermisUseCase
import remocra.usecase.permis.DeletePermisUseCase
import remocra.usecase.permis.FetchPermisUseCase
import remocra.usecase.permis.UpdatePermisUseCase
import remocra.utils.forbidden
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.time.ZonedDateTime
import java.util.UUID

@Path("/permis")
@Produces(MediaType.APPLICATION_JSON)
class PermisEndpoint : AbstractEndpoint() {

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var permisRepository: PermisRepository

    @Inject lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var fetchPermisUseCase: FetchPermisUseCase

    @Inject lateinit var createPermisUseCase: CreatePermisUseCase

    @Inject lateinit var updatePermisUseCase: UpdatePermisUseCase

    @Inject lateinit var deletePermisUseCase: DeletePermisUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @GET
    @Path("/layer")
    @RequireDroits([Droit.PERMIS_R])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox = bbox,
                sridSource = srid,
                typePointCarte = TypePointCarte.PERMIS,
                userInfo = securityContext.userInfo!!,
            ),
        ).build()
    }

    @GET
    @Path("/{permisId}")
    @RequireDroits([Droit.PERMIS_A, Droit.PERMIS_R])
    fun get(@PathParam("permisId") permisId: UUID): Response =
        Response.ok().entity(
            PermisDataToFront(
                permis = permisRepository.getById(permisId),
                permisDocument = permisRepository.getDocumentById(permisId),
                permisCadastreParcelle = permisRepository.getParcelleByPermisId(permisId),
                permisLastUpdateDate = permisRepository.getLastUpdateDate(permisId),
                permisInstructeurUsername = permisRepository.getInstructeurUsername(permisId),
            ),
        ).build()

    @GET
    @Path("/fetchPermisData/")
    @RequireDroits([Droit.PERMIS_A, Droit.PERMIS_R])
    fun fetchPermisUseCase(
        @QueryParam("coordonneeX") coordonneeX: String,
        @QueryParam("coordonneeY") coordonneeY: String,
        @QueryParam("srid") srid: Int,
    ): Response =
        Response.ok().entity(fetchPermisUseCase.fetchPermisData(coordonneeX, coordonneeY, srid)).build()

    @POST
    @Path("/create")
    @RequireDroits([Droit.PERMIS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val permisInput = objectMapper.readValue(httpRequest.getTextPart("permisData"), PermisInput::class.java)
        val permisId = UUID.randomUUID()
        return createPermisUseCase.execute(
            userInfo = securityContext.userInfo,
            element = PermisData(
                Permis(
                    permisId = permisId,
                    permisLibelle = permisInput.permisLibelle,
                    permisNumero = permisInput.permisNumero,
                    permisInstructeurId = securityContext.userInfo!!.utilisateurId,
                    permisServiceInstructeurId = permisInput.permisServiceInstructeurId,
                    permisTypePermisInterserviceId = permisInput.permisTypePermisInterserviceId,
                    permisTypePermisAvisId = permisInput.permisTypePermisAvisId,
                    permisRiReceptionnee = permisInput.permisRiReceptionnee,
                    permisDossierRiValide = permisInput.permisDossierRiValide,
                    permisObservations = permisInput.permisObservations,
                    permisVoieText = permisInput.permisVoieId?.let { null } ?: permisInput.permisVoieText,
                    permisVoieId = permisInput.permisVoieId,
                    permisComplement = permisInput.permisComplement,
                    permisCommuneId = permisInput.permisCommuneId,
                    permisAnnee = permisInput.permisAnnee,
                    permisDatePermis = permisInput.permisDatePermis,
                    permisGeometrie = permisInput.permisGeometrie,
                ),
                permisCadastreParcelle = permisInput.permisCadastreParcelle,
                permisDocuments = DocumentsData.DocumentsPermis(
                    objectId = permisId,
                    listDocument = objectMapper.readValue<List<DocumentsData.DocumentPermisData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
            ),
        ).wrap()
    }

    @PUT
    @Path("/{permisId}")
    @RequireDroits([Droit.PERMIS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("permisId") permisId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val permisInput = objectMapper.readValue(httpRequest.getTextPart("permisData"), PermisInput::class.java)

        return updatePermisUseCase.execute(
            securityContext.userInfo,
            PermisData(
                Permis(
                    permisId = permisId,
                    permisLibelle = permisInput.permisLibelle,
                    permisNumero = permisInput.permisNumero,
                    permisInstructeurId = securityContext.userInfo!!.utilisateurId,
                    permisServiceInstructeurId = permisInput.permisServiceInstructeurId,
                    permisTypePermisInterserviceId = permisInput.permisTypePermisInterserviceId,
                    permisTypePermisAvisId = permisInput.permisTypePermisAvisId,
                    permisRiReceptionnee = permisInput.permisRiReceptionnee,
                    permisDossierRiValide = permisInput.permisDossierRiValide,
                    permisObservations = permisInput.permisObservations,
                    permisVoieText = permisInput.permisVoieId?.let { null } ?: permisInput.permisVoieText,
                    permisVoieId = permisInput.permisVoieId,
                    permisComplement = permisInput.permisComplement,
                    permisCommuneId = permisInput.permisCommuneId,
                    permisAnnee = permisInput.permisAnnee,
                    permisDatePermis = permisInput.permisDatePermis,
                    permisGeometrie = permisInput.permisGeometrie,
                ),
                permisCadastreParcelle = permisInput.permisCadastreParcelle,
                permisDocuments = DocumentsData.DocumentsPermis(
                    objectId = permisId,
                    listDocument = objectMapper.readValue<List<DocumentsData.DocumentPermisData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
            ),
        ).wrap()
    }

    data class PermisInput(
        val permisLibelle: String,
        val permisNumero: String,
        val permisServiceInstructeurId: UUID,
        val permisTypePermisInterserviceId: UUID,
        val permisTypePermisAvisId: UUID,
        val permisRiReceptionnee: Boolean = false,
        val permisDossierRiValide: Boolean = false,
        val permisObservations: String? = null,
        val permisVoieText: String? = null,
        val permisVoieId: UUID? = null,
        val permisComplement: String? = null,
        val permisCommuneId: UUID,
        val permisAnnee: Int,
        val permisDatePermis: ZonedDateTime,
        val permisCadastreParcelle: List<UUID> = listOf(),
        val permisGeometrie: Geometry,
    )

    @DELETE
    @Path("/{permisId}")
    @RequireDroits([Droit.PERMIS_A])
    fun delete(
        @PathParam("permisId") permisId: UUID,
    ): Response {
        val permis = PermisData(
            permis = permisRepository.getById(permisId),
            permisCadastreParcelle = permisRepository.getParcelleByPermisId(permisId),
            permisDocuments = DocumentsData.DocumentsPermis(
                objectId = permisId,
                listeDocsToRemove = permisRepository.getDocumentById(permisId).mapNotNull { it.documentId },
                listDocument = listOf(),
                listDocumentParts = listOf(),
            ),
        )
        return deletePermisUseCase.execute(securityContext.userInfo, permis).wrap()
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.PERMIS_R, Droit.PERMIS_R])
    fun getWithFilter(
        @QueryParam("searchNom") searchNom: String?,
        @QueryParam("searchCommuneId") searchCommuneId: UUID?,
        @QueryParam("searchNumero") searchNumero: String?,
        @QueryParam("searchSection") searchSection: String?,
        @QueryParam("searchParcelle") searchParcelle: String?,
        @QueryParam("searchAvisId") searchAvisId: UUID?,
    ): Response =
        Response.ok().entity(
            permisRepository.getWithFilter(
                PermisRepository.Filter(
                    searchNom = searchNom,
                    searchCommuneId = searchCommuneId,
                    searchNumero = searchNumero,
                    searchSection = searchSection,
                    searchParcelle = searchParcelle,
                    searchAvisId = searchAvisId,
                ),
            ),
        ).build()

    @GET
    @Path("/get-libelle-permis-avis")
    @RequireDroits([Droit.PERMIS_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getAvisForSelect(): Response = Response.ok(permisRepository.getAvis()).build()
}
