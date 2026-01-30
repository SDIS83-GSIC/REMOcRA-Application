package remocra.web.signalements

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.locationtech.jts.io.WKTReader
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DocumentsData
import remocra.data.SignalementData
import remocra.data.SignalementElementInput
import remocra.data.enums.TypeElementCarte
import remocra.db.SignalementRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.signalement.CreateSignalementUsecase
import remocra.usecase.signalement.DepotDeliberationUseCase
import remocra.utils.getTextPart
import remocra.utils.getTextPartOrNull
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/signalements")
@Produces(MediaType.APPLICATION_JSON)
class SignalementEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var signalementRepository: SignalementRepository

    @Inject
    lateinit var createSignalementUsecase: CreateSignalementUsecase

    @Inject
    lateinit var depotDeliberationUseCase: DepotDeliberationUseCase

    @Context
    lateinit var securityContext: SecurityContext

    /**
     * Renvoie les Alertes au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @RequireDroits([Droit.SIGNALEMENTS_C])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response {
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.SIGNALEMENT,
                securityContext.userInfo,
            ),
        ).build()
    }

    @GET
    @Path("/type-sous-type")
    @RequireDroits([Droit.SIGNALEMENTS_C])
    fun getTypeSousType(): Response {
        return Response.ok(
            signalementRepository.getTypeAndSousType(),
        ).build()
    }

    @GET
    @Path("/type-anomalie")
    @RequireDroits([Droit.SIGNALEMENTS_C])
    fun getTypeAnomalie(): Response {
        return Response.ok(
            signalementRepository.getTypeAnomalie(),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.SIGNALEMENTS_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun createSignalement(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val signalementId = UUID.randomUUID()
        val rawElements: List<SignalementElementRaw> =
            httpRequest.getTextPart("listSignalementElement")
                .let { jacksonObjectMapper().readValue(it) }
                ?: emptyList()

        val elem = SignalementData(
            signalementId = signalementId,
            document = DocumentsData.DocumentsEvenement(
                objectId = signalementId,
                listDocument = if (!httpRequest.getTextPartOrNull("documents").isNullOrEmpty()) objectMapper.readValue<List<DocumentsData.DocumentEvenementData>>(httpRequest.getTextPart("documents")) else emptyList(),
                listeDocsToRemove = emptyList(),
                listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
            ),
            description = httpRequest.getTextPart("description"),
            listSignalementElement = rawElements.map { raw ->
                val geometry = WKTReader().read(raw.geometry.wkt)
                geometry.srid = raw.geometry.srid

                SignalementElementInput(
                    geometry = geometry,
                    anomalies = raw.anomalies,
                    description = raw.description,
                    sousType = raw.sousType,
                )
            },
        )

        return createSignalementUsecase.execute(
            userInfo = securityContext.userInfo,
            element = elem,
        ).wrap()
    }

    data class SignalementElementRaw(
        val geometry: GeometryRaw,
        val anomalies: Collection<String>,
        val description: String?,
        val sousType: UUID,
    )

    data class GeometryRaw(
        val wkt: String,
        val srid: Int,
    )

    @PUT
    @Path("/deliberation/")
    @RequireDroits([Droit.DEPOT_DELIB_C])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun depotDeliberationSignalement(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        return Response.ok(
            depotDeliberationUseCase.execute(
                securityContext.userInfo,
                httpRequest.getPart("document"),
            ),
        ).build()
    }
}
