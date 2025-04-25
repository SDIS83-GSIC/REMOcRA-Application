package remocra.web.debitsimultane

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
import remocra.data.DebitSimultaneData
import remocra.data.DebitSimultaneMesureData
import remocra.data.enums.TypeElementCarte
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.debitsimultane.CreateDebitSimultaneUseCase
import remocra.usecase.debitsimultane.DeleteDebitSimultaneUseCase
import remocra.usecase.debitsimultane.GetDebitSimultaneCompletUseCase
import remocra.usecase.debitsimultane.GetPibiForDebitSimultaneUseCase
import remocra.usecase.debitsimultane.UpdateDebitSimultaneUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/debit-simultane")
@Produces(MediaType.APPLICATION_JSON)
class DebitSimultaneEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject
    lateinit var getDebitSimultaneCompletUseCase: GetDebitSimultaneCompletUseCase

    @Inject
    lateinit var getPibiForDebitSimultaneUseCase: GetPibiForDebitSimultaneUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var updateDebitSimultaneUseCase: UpdateDebitSimultaneUseCase

    @Inject
    lateinit var createDebitSimultaneUseCase: CreateDebitSimultaneUseCase

    @Inject
    lateinit var deleteDebitSimultaneUseCase: DeleteDebitSimultaneUseCase

    @Context
    lateinit var securityContext: SecurityContext

    /**
     * Renvoie les débits simultanés au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @RequireDroits([Droit.DEBITS_SIMULTANES_R])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response {
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.DEBIT_SIMULTANE,
                securityContext.userInfo,
            ),
        ).build()
    }

    @GET
    @Path("/get/{debitSimultaneId}")
    @RequireDroits([Droit.DEBITS_SIMULTANES_R])
    fun get(
        @PathParam("debitSimultaneId")
        debitSimultaneId: UUID,
    ) = Response.ok(
        getDebitSimultaneCompletUseCase.execute(
            securityContext.userInfo,
            debitSimultaneId,
        ),
    ).build()

    @GET
    @Path("/get-infos")
    @RequireDroits([Droit.DEBITS_SIMULTANES_A])
    fun getInfos(
        @QueryParam("listePibiId")
        listePibiId: Set<UUID>,
    ) = Response.ok(
        getPibiForDebitSimultaneUseCase.getInfosTypeReseauMaxDiametre(
            listePibiId,
        ),
    ).build()

    @GET
    @Path("/pei")
    @RequireDroits([Droit.DEBITS_SIMULTANES_R])
    fun getPei(
        @QueryParam("geometry")
        geometry: Geometry?,
        @QueryParam("typeReseauId")
        typeReseauId: UUID,
        @QueryParam("listePibiId")
        listePibiId: Set<UUID>?,
    ): Response {
        val listePibi =
            if (listePibiId != null) {
                getPibiForDebitSimultaneUseCase.execute(
                    null,
                    listePibiId,
                    typeReseauId,
                )
            } else {
                getPibiForDebitSimultaneUseCase.execute(
                    geometry,
                    null,
                    typeReseauId,
                )
            }
        return Response.ok(listePibi).build()
    }

    @DELETE
    @Path("/delete/{debitSimultaneId}")
    @RequireDroits([Droit.DEBITS_SIMULTANES_A])
    fun delete(
        @PathParam("debitSimultaneId")
        debitSimultaneId: UUID,
    ): Response {
        val debit = getDebitSimultaneCompletUseCase.execute(debitSimultaneId = debitSimultaneId, userInfo = securityContext.userInfo)
        return deleteDebitSimultaneUseCase.execute(
            securityContext.userInfo,
            DebitSimultaneData(
                debitSimultaneId = debit.debitSimultaneId,
                debitSimultaneNumeroDossier = debit.debitSimultaneNumeroDossier,
                listeDebitSimultaneMesure = debit.listeDebitSimultaneMesure.toList(),
                listeDocument = null,
            ),
        ).wrap()
    }

    @PUT
    @Path("/update/{debitSimultaneId}")
    @RequireDroits([Droit.DEBITS_SIMULTANES_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun update(
        @PathParam("debitSimultaneId")
        debitSimultaneId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val debitSimultaneData = DebitSimultaneData(
            debitSimultaneId = debitSimultaneId,
            debitSimultaneNumeroDossier = httpRequest.getTextPart("debitSimultaneNumeroDossier"),
            listeDocument = httpRequest.parts.filter { it.name.contains("document_") },
            listeDebitSimultaneMesure = objectMapper.readValue<List<DebitSimultaneMesureData>>
                (httpRequest.getTextPart("listeDebitSimultaneMesure")),
        )

        return updateDebitSimultaneUseCase.execute(securityContext.userInfo, debitSimultaneData).wrap()
    }

    @POST
    @Path("/check-distance")
    @RequireDroits([Droit.DEBITS_SIMULTANES_A])
    fun checkDistance(
        @QueryParam("listePibiId")
        listePibiId: Set<UUID>,
    ): Response {
        return Response.ok(getPibiForDebitSimultaneUseCase.checkDistance(listePibiId)).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.DEBITS_SIMULTANES_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val debitSimultaneData = DebitSimultaneData(
            debitSimultaneId = UUID.randomUUID(),
            debitSimultaneNumeroDossier = httpRequest.getTextPart("debitSimultaneNumeroDossier"),
            listeDocument = httpRequest.parts.filter { it.name.contains("document_") },
            listeDebitSimultaneMesure = objectMapper.readValue<List<DebitSimultaneMesureData>>
                (httpRequest.getTextPart("listeDebitSimultaneMesure")),
        )

        return createDebitSimultaneUseCase.execute(securityContext.userInfo, debitSimultaneData).wrap()
    }
}
