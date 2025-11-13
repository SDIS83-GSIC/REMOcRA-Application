package remocra.web.crise

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
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
import remocra.data.CouchesData
import remocra.data.CreateDoc
import remocra.data.CriseData
import remocra.data.CriseDocumentData
import remocra.data.DataTableau
import remocra.data.DocumentsData
import remocra.data.EvenementData
import remocra.data.EvenementGeometrieData
import remocra.data.MessageData
import remocra.data.Params
import remocra.data.enums.TypeElementCarte
import remocra.db.CriseRepository
import remocra.db.EvenementRepository
import remocra.db.MessageRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.db.jooq.remocra.enums.EvenementStatutMode
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.security.NoCsrf
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.crise.CreateCriseUseCase
import remocra.usecase.crise.CreateScreenCriseUseCase
import remocra.usecase.crise.CriseUseCase
import remocra.usecase.crise.ExportCriseUseCase
import remocra.usecase.crise.MergeCriseUseCase
import remocra.usecase.crise.UpdateCriseUseCase
import remocra.usecase.crise.evenement.CreateEventUseCase
import remocra.usecase.crise.evenement.EvenementUseCase
import remocra.usecase.crise.evenement.UpdateEvenementUseCase
import remocra.usecase.crise.evenement.UpdateEventGeometryUseCase
import remocra.usecase.crise.evenement.document.CreateCriseDocument
import remocra.usecase.crise.evenement.message.CreateEventMessageUseCase
import remocra.utils.DateUtils
import remocra.utils.badRequest
import remocra.utils.getTextPart
import remocra.utils.getTextPartOrNull
import remocra.web.AbstractEndpoint
import java.time.ZonedDateTime
import java.util.UUID

@Path("/crise")
@Produces(MediaType.APPLICATION_JSON)
class CriseEndpoint : AbstractEndpoint() {

    @Inject lateinit var dateUtils: DateUtils

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var createCriseDocument: CreateCriseDocument

    @Inject lateinit var criseUseCase: CriseUseCase

    @Inject lateinit var createCriseUseCase: CreateCriseUseCase

    @Inject lateinit var evenementUseCase: EvenementUseCase

    @Inject lateinit var updateEventGeometryUseCase: UpdateEventGeometryUseCase

    @Inject lateinit var mergeCriseUseCase: MergeCriseUseCase

    @Inject lateinit var exportCriseUseCase: ExportCriseUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var updateCriseUseCase: UpdateCriseUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject lateinit var createScreenCrise: CreateScreenCriseUseCase

    @Inject lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var createEventUseCase: CreateEventUseCase

    @Inject lateinit var evenementRepository: EvenementRepository

    @Inject lateinit var updateEvenementUseCase: UpdateEvenementUseCase

    @Inject lateinit var createEventMessageUseCase: CreateEventMessageUseCase

    @Inject lateinit var messageRepository: MessageRepository

    data class CriseInput(
        val criseLibelle: String? = null,
        val criseDescription: String? = null,
        val criseDateDebut: ZonedDateTime? = null,
        val criseDateFin: ZonedDateTime? = null,
        val typeCriseId: UUID? = null,
        val criseStatutType: TypeCriseStatut = TypeCriseStatut.EN_COURS,
        val listeCommuneId: Collection<UUID>? = null,
        val listeToponymieId: Collection<UUID>? = null,
        val couchesWMS: Collection<CouchesData>? = null,
    )
    data class MessageInput(
        val messageObjet: String? = null,
        val messageDescription: String? = null,
        val messageDateConstat: ZonedDateTime? = null,
        var messageImportance: Int? = null, // peut être null, mais initialisé à 0 par défaut
        val messageOrigine: String? = null,
        val messageTags: String? = null,
        val messageUtilisateurId: UUID? = null,
    )

    data class CriseDataMerge(
        val criseId: UUID,
        val criseDateFin: ZonedDateTime?,
        val listeCriseId: Collection<UUID>?,
    )

    data class CriseDataExport(
        val criseId: UUID,
        val dateDebExtraction: ZonedDateTime,
        val dateFinExtraction: ZonedDateTime,
        val hasMessage: Boolean,
        val hasDoc: Boolean,
    )

    @POST
    @Path("/")
    @RequireDroits([Droit.CRISE_R])
    fun getCrise(params: Params<CriseRepository.FilterCrise, CriseRepository.SortCrise>): Response {
        return Response.ok(
            DataTableau(
                criseRepository.getCrises(params),
                criseRepository.getCountCrises(params.filterBy),
            ),
        ).build()
    }

    @GET
    @Path("/getCriseForMerge")
    @Public("Les types de crises ne sont pas liées à un droit.")
    fun getCriseForMerge(): Response {
        return Response.ok(
            criseUseCase.getCriseForMerge(),
        ).build()
    }

    @PUT
    @Path("/{criseId}/merge")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun mergeCrise(
        @PathParam("criseId")
        criseId: UUID,
        criseDataMerge: CriseDataMerge,
    ): Response {
        return mergeCriseUseCase.execute(
            userInfo = securityContext.userInfo,
            CriseDataMerge(
                criseId = criseDataMerge.criseId,
                criseDateFin = criseDataMerge.criseDateFin,
                listeCriseId = criseDataMerge.listeCriseId,
            ),
        ).wrap()
    }

    @GET
    @Path("/{criseId}/export")
    @RequireDroits([Droit.CRISE_R])
    @Produces(MediaType.TEXT_PLAIN)
    @NoCsrf("On télécharge un fichier")
    fun exportCrise(
        @PathParam("criseId") criseId: UUID,
        @QueryParam("dateDebExtraction") dateDebExtraction: ZonedDateTime,
        @QueryParam("dateFinExtraction") dateFinExtraction: ZonedDateTime,
        @QueryParam("hasMessage") hasMessage: Boolean,
        @QueryParam("hasDoc") hasDoc: Boolean,

    ): Response =
        Response.ok(
            exportCriseUseCase.execute(
                criseId = criseId,
                dateDebExtraction = dateDebExtraction,
                dateFinExtraction = dateFinExtraction,
                hasMessage = hasMessage,
                hasDoc = hasDoc,
            ),
        )
            .header("Content-Disposition", "attachment; filename=\"criseExport-${dateUtils.now()}.zip\"")
            .build()

    @GET
    @Path("/get-type-crise")
    @Public("Les types de crises ne sont pas liées à un droit.")
    fun getTypeCriseForSelect(): Response {
        return Response.ok(
            criseUseCase.getTypeCriseForSelect(),
        ).build()
    }

    @GET
    @Path("/{criseId}/getCommuneGeometrie")
    @Public("Les types de crises ne sont pas liées à un droit.")
    fun getCommuneGeometrieByCrise(
        @PathParam("criseId")
        criseId: UUID,
    ): Response {
        return Response.ok(
            criseUseCase.getCommuneGeometriesByCrise(criseId),
        ).build()
    }

    @GET
    @Path("/{criseId}/get-type-event-from-crise/{statut}")
    @Public("Les types d'évenements ne sont pas liées à un droit.")
    fun getTypeEventFromCrise(
        @PathParam("criseId")
        criseId: UUID,
        @PathParam("statut")
        statut: EvenementStatutMode,
    ): Response {
        return Response.ok(
            evenementUseCase.getTypeEventFromCrise(criseId, statut),
        ).build()
    }

    @GET
    @Path("/evenement/get-type-evenement")
    @Public("Les types d'évenements ne sont pas liées à un droit.")
    fun getTypeEventForSelect(): Response {
        return Response.ok(
            evenementRepository.getTypeEventForSelect(),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.CRISE_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun createCrise(criseInput: CriseInput): Response {
        return createCriseUseCase.execute(
            securityContext.userInfo,
            CriseData(
                criseId = UUID.randomUUID(),
                criseLibelle = criseInput.criseLibelle,
                criseDescription = criseInput.criseDescription,
                criseDateDebut = criseInput.criseDateDebut,
                criseDateFin = criseInput.criseDateFin,
                criseTypeCriseId = criseInput.typeCriseId,
                criseStatutType = criseInput.criseStatutType,
                listeCommuneId = criseInput.listeCommuneId,
                listeToponymieId = criseInput.listeToponymieId,
                couchesWMS = criseInput.couchesWMS,
            ),
        ).wrap()
    }

    @GET
    @Path("/{criseId}")
    @RequireDroits([Droit.CRISE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getCrise(
        @PathParam("criseId")
        criseId: UUID,
    ): Response {
        return Response.ok(criseRepository.getCrise(criseId)).build()
    }

    @GET
    @Path("/evenement/{evenementId}")
    @RequireDroits([Droit.CRISE_R])
    fun getEvenement(
        @PathParam("evenementId")
        evenementId: UUID,
    ): Response {
        return Response.ok(evenementRepository.getEvenement(evenementId)).build()
    }

    @PUT
    @Path("/{criseId}/update")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateCrise(
        @PathParam("criseId")
        criseId: UUID,
        criseInput: CriseInput,
    ): Response {
        return updateCriseUseCase.execute(
            userInfo = securityContext.userInfo,
            CriseData(
                criseId = criseId,
                criseLibelle = criseInput.criseLibelle,
                criseDescription = criseInput.criseDescription,
                criseDateDebut = criseInput.criseDateDebut,
                criseDateFin = criseInput.criseDateFin,
                criseTypeCriseId = criseInput.typeCriseId,
                criseStatutType = criseInput.criseStatutType,
                listeCommuneId = criseInput.listeCommuneId,
                listeToponymieId = criseInput.listeToponymieId,
                couchesWMS = criseInput.couchesWMS,
            ),
        ).wrap()
    }

    @POST
    @Path("/{criseId}/clore/")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun cloreCrise(
        @PathParam("criseId")
        criseId: UUID,
        criseInput: CriseInput,
    ): Response {
        val crise = criseRepository.getCrise(criseId)
        val criseData =
            CriseData(
                criseId = criseId,
                criseLibelle = crise.criseLibelle,
                criseDescription = crise.criseDescription,
                criseDateDebut = crise.criseDateDebut,
                criseDateFin = criseInput.criseDateFin,
                criseTypeCriseId = crise.typeCriseId,
                criseStatutType = TypeCriseStatut.TERMINEE,
                listeCommuneId = crise.listeCommuneId,
                listeToponymieId = crise.listeToponymieId,
            )
        return updateCriseUseCase.execute(
            userInfo = securityContext.userInfo,
            criseData,
        ).wrap()
    }

    @GET
    @Path("/{criseId}/evenement/type-sous-type")
    @RequireDroits([Droit.CRISE_C, Droit.CRISE_U])
    fun getTypeSousType(
        @PathParam("criseId")
        criseId: UUID,
    ): Response {
        return Response.ok(
            evenementRepository.getTypeAndSousType(criseId),
        ).build()
    }

    @POST
    @Path("/documents/getAllFromCrise/{criseId}")
    @RequireDroits([Droit.CRISE_C, Droit.CRISE_R, Droit.CRISE_U])
    fun getAllFromCrise(
        @PathParam("criseId")
        criseId: UUID,
        params: Params<CriseRepository.FilterCrise, CriseRepository.SortDocs>,
    ): Response {
        return Response.ok(
            DataTableau(
                criseRepository.getAllDocumentsFromCrise(criseId, params),
                criseRepository.getCountDocumentFromCrise(criseId, params.filterBy),
            ),

        ).build()
    }

    @DELETE
    @Path("/documents/supprimer/{documentId}")
    @RequireDroits([Droit.CRISE_U])
    fun supprimerDocumentCrise(
        @PathParam("documentId")
        documentId: UUID,
    ): Response {
        return Response.ok(
            criseRepository.deleteCriseDocuments(documentId),
        ).build()
    }

    @GET
    @Path("/{criseId}/evenement/{state}")
    @RequireDroits([Droit.CRISE_R])
    fun getAllEvents(
        @PathParam("criseId")
        criseId: UUID,
        @PathParam("state")
        state: EvenementStatutMode?,
        @QueryParam("filterType") type: Set<UUID>?,
        @QueryParam("filterAuthor") author: Set<UUID>?,
        @QueryParam("filterStatut") statut: EvenementStatut?,
        @QueryParam("filterImportance") importance: Int?,
        @QueryParam("filterMessage") message: String?,
        @QueryParam("filterTag") tags: Set<String>?,
    ): Response {
        val params = EvenementRepository.Filter(
            filterType = type,
            filterAuthor = author,
            filterStatut = statut,
            filterImportance = importance,
            filterMessage = message,
            filterTags = tags,
        )

        return Response.ok(
            evenementRepository.getAllEvents(criseId = criseId, params = params, state = state),
        ).build()
    }

    @GET
    @Path("/evenement/message")
    @RequireDroits([Droit.CRISE_R, Droit.CRISE_C, Droit.CRISE_U])
    fun getAllMessages(): Response {
        return Response.ok(
            messageRepository.getAllMessages(),
        ).build()
    }

    /**
     * Renvoie les évènements au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/evenement/layer")
    @RequireDroits([Droit.CRISE_R])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
        @QueryParam("criseId") criseId: UUID,
        @QueryParam("state") state: EvenementStatutMode,
    ): Response {
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox = bbox,
                sridSource = srid,
                etudeId = null,
                typeElementCarte = TypeElementCarte.CRISE,
                userInfo = securityContext.userInfo,
                criseId = criseId,
                criseState = state,
            ),
        ).build()
    }

    @POST
    @Path("/{criseId}/evenement/{state}/create")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun createEvent(
        @PathParam("criseId")
        criseId: UUID,
        @PathParam("state")
        state: EvenementStatutMode,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val evenementId = UUID.randomUUID()
        val docsEvenement = getDocumentCrise(evenementId, httpRequest)
        return createEventUseCase.execute(
            securityContext.userInfo,
            EvenementData(
                evenementId = evenementId,
                evenementCategorieId = UUID.fromString(httpRequest.getTextPart("evenementSousCategorieId")),
                evenementLibelle = httpRequest.getTextPart("evenementLibelle"),
                evenementDescription = httpRequest.getTextPart("evenementDescription"),
                evenementOrigine = httpRequest.getTextPart("evenementOrigine"),
                evenementDateConstat = ZonedDateTime.parse(httpRequest.getTextPart("evenementDateConstat")),
                evenementImportance = httpRequest.getTextPart("evenementImportance").toInt(),
                evenementTags = httpRequest.getTextPart("evenementTags").split(','),
                evenementEstFerme = httpRequest.getTextPart("evenementEstFerme").toBoolean(),
                evenementDateCloture = null, // un evenement n'a pas de date de cloture lorsqu'il est modifié
                evenementGeometrie = objectMapper.readValue<Geometry>(httpRequest.getTextPart("evenementGeometrie")),
                listeDocuments = docsEvenement,
                evenementCriseId = criseId,
                evenementStatut = if (httpRequest.getTextPart("evenementEstFerme").toBoolean()) EvenementStatut.CLOS else EvenementStatut.EN_COURS,
                evenementUtilisateurId = UUID.fromString(httpRequest.getTextPart("evenementUtilisateurId")),
                evenementStatutMode = state,
            ),
        ).wrap()
    }

    @POST
    @Path("/evenement/{evenementId}/message/create")
    @RequireDroits([Droit.CRISE_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun createMessage(
        @PathParam("evenementId")
        evenementId: UUID,
        messageInput: MessageInput,
    ): Response {
        val messageData =
            MessageData(
                messageObjet = messageInput.messageObjet,
                messageDescription = messageInput.messageDescription,
                messageDateConstat = messageInput.messageDateConstat,
                messageImportance = messageInput.messageImportance,
                messageOrigine = messageInput.messageOrigine,
                messageTags = messageInput.messageTags,
                messageId = UUID.randomUUID(),
                messageEvenementId = evenementId,
                messageUtilisateurId = messageInput.messageUtilisateurId,
            )

        return createEventMessageUseCase.execute(
            securityContext.userInfo,
            messageData,
        ).wrap()
    }

    @PUT
    @Path("/{criseId}/evenement/{state}/{evenementId}/update")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateEvenement(
        @PathParam("evenementId")
        evenementId: UUID,
        @PathParam("criseId")
        criseId: UUID,
        @PathParam("state")
        state: EvenementStatutMode,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val docsEvenement = getDocumentCrise(evenementId, httpRequest)
        val evenementData =
            EvenementData(
                evenementId = evenementId,
                evenementCategorieId = UUID.fromString(httpRequest.getTextPart("evenementSousCategorieId")),
                evenementLibelle = httpRequest.getTextPart("evenementLibelle"),
                evenementDescription = httpRequest.getTextPart("evenementDescription"),
                evenementOrigine = httpRequest.getTextPart("evenementOrigine"),
                evenementDateConstat = ZonedDateTime.parse(httpRequest.getTextPart("evenementDateConstat")),
                evenementImportance = httpRequest.getTextPart("evenementImportance").toInt(),
                evenementTags = if (httpRequest.getTextPart("evenementTags").isBlank()) emptyList() else objectMapper.readValue<List<String>>(httpRequest.getTextPart("evenementTags")),
                evenementEstFerme = httpRequest.getTextPart("evenementEstFerme").toBoolean(),
                evenementDateCloture = null, // un evenement n'a pas de date de cloture lorsqu'il est modifié
                evenementGeometrie = objectMapper.readValue<Geometry>(httpRequest.getTextPart("evenementGeometrie")),
                listeDocuments = docsEvenement,
                evenementCriseId = criseId,
                evenementStatut = if (httpRequest.getTextPart("evenementEstFerme").toBoolean()) EvenementStatut.CLOS else EvenementStatut.EN_COURS,
                evenementUtilisateurId = UUID.fromString(httpRequest.getTextPart("evenementUtilisateurId")),
                evenementStatutMode = state,
            )
        return updateEvenementUseCase.execute(
            userInfo = securityContext.userInfo,
            evenementData,
        ).wrap()
    }

    private fun getDocumentCrise(evenementId: UUID, httpRequest: HttpServletRequest) =
        DocumentsData.DocumentsEvenement(
            objectId = evenementId,
            listDocument = if (!httpRequest.getTextPartOrNull("documents").isNullOrEmpty()) objectMapper.readValue<List<DocumentsData.DocumentEvenementData>>(httpRequest.getTextPart("documents")) else emptyList(),
            listeDocsToRemove = if (httpRequest.getTextPartOrNull("listeDocsToRemove").isNullOrEmpty()) objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")) else emptyList(),
            listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
        )

    @POST
    @Path("/document/addDocument/{criseId}")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun addDocument(
        @PathParam("criseId")
        criseId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val docData =
            CriseDocumentData(
                criseId = criseId,
                listDocument = DocumentsData.DocumentsEvenement(
                    objectId = criseId,
                    listDocument = objectMapper.readValue<List<DocumentsData.DocumentEvenementData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
            )
        return createCriseDocument.execute(
            userInfo = securityContext.userInfo,
            docData,
        ).wrap()
    }

    @PATCH
    @Path("/{eventId}/geometry")
    @RequireDroits([Droit.CRISE_U])
    fun move(element: EvenementGeometrieData, @PathParam("eventId") eventId: UUID): Response {
        if (element.eventId != eventId) {
            return badRequest().build()
        }
        return updateEventGeometryUseCase.execute(securityContext.userInfo, element).wrap()
    }

    @GET
    @Path("/{criseId}/get-toponymies")
    @RequireDroits([Droit.CRISE_C])
    fun getToponymies(
        @PathParam("criseId")
        criseId: UUID,
        @QueryParam("libelle") libelle: String,
    ): Response {
        return Response.ok(
            criseUseCase.getToponymies(criseId, libelle),
        ).build()
    }

    @POST
    @Path("/{criseId}/screen")
    @RequireDroits([Droit.CRISE_C, Droit.CRISE_U])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun doScreen(
        @PathParam("criseId") criseId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        return createScreenCrise.execute(
            userInfo = securityContext.userInfo,
            CreateDoc(
                criseId = criseId,
                criseDocName = httpRequest.getTextPart("criseDocName"),
                criseDocument = httpRequest.getPart("document"),
                criseDocumentGeometrie = objectMapper.readValue<Geometry>(httpRequest.getTextPart("documentGeometry")),
            ),
        ).wrap()
    }

    @GET
    @Path("/get-couches-wms")
    @Public("Les couches WMS ne sont pas liées à un droit.")
    fun getCouchesWms(): Response {
        return Response.ok(
            criseRepository.getCouchesWms(),
        ).build()
    }

    @GET
    @Path("{criseId}/get-couches")
    @Public("Les couches WMS ne sont pas liées à un droit.")
    fun getCouchesByCrise(
        @PathParam("criseId") criseId: UUID,
    ): Response {
        return Response.ok(
            criseRepository.getCouchesByCrise(criseId),
        ).build()
    }

    @GET
    @Path("/{criseId}/geometrie")
    @RequireDroits([Droit.CRISE_R, Droit.CRISE_U, Droit.CRISE_C, Droit.CRISE_D])
    @Produces(MediaType.APPLICATION_JSON)
    fun getGeometrieById(@PathParam("criseId") criseId: UUID): Response = Response.ok(criseRepository.getCriseCommuneGeometrie(criseId)).build()
}
