package remocra.web.crise

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
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.CriseData
import remocra.data.CriseDocumentData
import remocra.data.DataTableau
import remocra.data.DocumentsData
import remocra.data.EvenementData
import remocra.data.MessageData
import remocra.data.Params
import remocra.data.enums.TypeElementCarte
import remocra.db.CriseRepository
import remocra.db.EvenementRepository
import remocra.db.MessageRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.crise.CreateCriseUseCase
import remocra.usecase.crise.CriseUseCase
import remocra.usecase.crise.UpdateCriseUseCase
import remocra.usecase.crise.evenement.CreateEventUseCase
import remocra.usecase.crise.evenement.UpdateEvenementUseCase
import remocra.usecase.crise.evenement.document.CreateCriseDocument
import remocra.usecase.crise.evenement.message.CreateEventMessageUseCase
import remocra.utils.forbidden
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.time.ZonedDateTime
import java.util.UUID

@Path("/crise")
@Produces(MediaType.APPLICATION_JSON)
class CriseEndpoint : AbstractEndpoint() {

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var createCriseDocument: CreateCriseDocument

    @Inject lateinit var criseUseCase: CriseUseCase

    @Inject lateinit var createCriseUseCase: CreateCriseUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var updateCriseUseCase: UpdateCriseUseCase

    @Inject lateinit var objectMapper: ObjectMapper

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
        val typeCrise: UUID? = null,
        val criseStatutType: TypeCriseStatut = TypeCriseStatut.EN_COURS,
        val listeCommuneId: Collection<UUID>? = null,
        val listeToponymieId: Collection<UUID>? = null,
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

    @POST
    @Path("/")
    @RequireDroits([Droit.CRISE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getCrise(params: Params<CriseRepository.FilterCrise, CriseRepository.SortCrise>): Response {
        return Response.ok(
            DataTableau(
                criseRepository.getCrises(params),
                criseRepository.getCountCrises(params.filterBy),
            ),
        ).build()
    }

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
                criseTypeCriseId = criseInput.typeCrise,
                criseStatutType = criseInput.criseStatutType,
                listeCommuneId = criseInput.listeCommuneId,
                listeToponymieId = criseInput.listeToponymieId,
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
                criseTypeCriseId = criseInput.typeCrise,
                criseStatutType = criseInput.criseStatutType,
                listeCommuneId = criseInput.listeCommuneId,
                listeToponymieId = criseInput.listeToponymieId,
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
                listeCommuneId = crise.listeCommune,
                listeToponymieId = crise.listeToponymie,
            )
        return updateCriseUseCase.execute(
            userInfo = securityContext.userInfo,
            criseData,
        ).wrap()
    }

    @GET
    @Path("/{criseId}/evenement/type-sous-type")
    @RequireDroits([Droit.CRISE_C])
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
    @RequireDroits([Droit.CRISE_C])
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
    @Path("/{criseId}/evenement/")
    @RequireDroits([Droit.CRISE_R])
    fun getAllEvents(
        @PathParam("criseId")
        criseId: UUID,
    ): Response {
        return Response.ok(
            evenementRepository.getAllEvents(criseId),
        ).build()
    }

    @GET
    @Path("/evenement/message")
    @RequireDroits([Droit.CRISE_C])
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
    ): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox = bbox,
                sridSource = srid,
                etudeId = null,
                typeElementCarte = TypeElementCarte.CRISE,
                userInfo = securityContext.userInfo!!,
                criseId = criseId,
            ),
        ).build()
    }

    @POST
    @Path("/{criseId}/evenement/create")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun createEvent(
        @PathParam("criseId")
        criseId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val evenementId = UUID.randomUUID()
        return createEventUseCase.execute(
            securityContext.userInfo,
            EvenementData(
                evenementId = evenementId,
                evenementTypeId = UUID.fromString(httpRequest.getTextPart("evenementTypeId")),
                evenementLibelle = httpRequest.getTextPart("evenementLibelle"),
                evenementDescription = httpRequest.getTextPart("evenementDescription"),
                evenementOrigine = httpRequest.getTextPart("evenementOrigine"),
                evenementDateConstat = ZonedDateTime.parse(httpRequest.getTextPart("evenementDateDebut")),
                evenementImportance = httpRequest.getTextPart("evenementImportance").toInt(),
                evenementTag = httpRequest.getTextPart("evenementTag"),
                evenementEstFerme = httpRequest.getTextPart("evenementIsClosed").toBoolean(),
                evenementDateCloture = null, // un evenement n'a pas de date de cloture lorsqu'il est modifié
                evenementGeometrie = objectMapper.readValue<Geometry>(httpRequest.getTextPart("evenementGeometrie")),
                listeDocument = DocumentsData.DocumentsEvenement(
                    objectId = evenementId,
                    listDocument = objectMapper.readValue<List<DocumentsData.DocumentEvenementData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
                evenementCriseId = criseId,
                evenementStatut = EvenementStatut.EN_COURS,
                evenementUtilisateurId = UUID.fromString(httpRequest.getTextPart("evenementUtilisateurId")),
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
    @Path("/{criseId}/evenement/{evenementId}/update")
    @RequireDroits([Droit.CRISE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateEvenement(
        @PathParam("evenementId")
        evenementId: UUID,
        @PathParam("criseId")
        criseId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val evenementData =
            EvenementData(
                evenementId = evenementId,
                evenementTypeId = UUID.fromString(httpRequest.getTextPart("evenementTypeId")),
                evenementLibelle = httpRequest.getTextPart("evenementLibelle"),
                evenementDescription = httpRequest.getTextPart("evenementDescription"),
                evenementOrigine = httpRequest.getTextPart("evenementOrigine"),
                evenementDateConstat = ZonedDateTime.parse(httpRequest.getTextPart("evenementDateDebut")),
                evenementImportance = httpRequest.getTextPart("evenementImportance").toInt(),
                evenementTag = httpRequest.getTextPart("evenementTag"),
                evenementEstFerme = httpRequest.getTextPart("evenementIsClosed").toBoolean(),
                evenementDateCloture = null, // un evenement n'a pas de date de cloture lorsqu'il est modifié
                evenementGeometrie = objectMapper.readValue<Geometry>(httpRequest.getTextPart("evenementGeometrie")),
                listeDocument = DocumentsData.DocumentsEvenement(
                    objectId = evenementId,
                    listDocument = objectMapper.readValue<List<DocumentsData.DocumentEvenementData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
                evenementCriseId = criseId,
                evenementStatut = EvenementStatut.EN_COURS,
                evenementUtilisateurId = UUID.fromString(httpRequest.getTextPart("evenementUtilisateurId")),
            )
        return updateEvenementUseCase.execute(
            userInfo = securityContext.userInfo,
            evenementData,
        ).wrap()
    }

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
}
