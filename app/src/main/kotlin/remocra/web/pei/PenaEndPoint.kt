package remocra.web.pei

import com.google.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.AireAspirationUpsertData
import remocra.db.AireAspirationRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.pena.AireAspirationUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/pena")
@Produces(MediaType.APPLICATION_JSON)
class PenaEndPoint : AbstractEndpoint() {

    @Inject lateinit var aireAspirationRepository: AireAspirationRepository

    @Inject lateinit var aireAspirationUSeCase: AireAspirationUseCase

    @Context lateinit var securityContext: SecurityContext

    @GET
    @Path("/type-pena-aspiration")
    @RequireDroits([Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_U])
    fun getTypePenaAspiration() =
        Response.ok(aireAspirationRepository.getTypeAireAspiration()).build()

    @GET
    @Path("/get-aire-aspiration/{penaId}")
    @RequireDroits([Droit.PEI_R, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_U])
    fun getAireAspiration(
        @PathParam("penaId")
        penaId: UUID,
    ) =
        Response.ok(aireAspirationRepository.getAiresAspiration(penaId)).build()

    @PUT
    @Path("/upsert-pena-aspiration/{penaId}")
    @RequireDroits([Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun upsert(
        @PathParam("penaId")
        penaId: UUID,
        aireAspirationInput: AireAspirationInput,
    ) =
        aireAspirationUSeCase.execute(
            securityContext.userInfo,
            AireAspirationUseCase.PenaAspirationData(
                aireAspirationInput.listeAireAspiration,
                penaId,
            ),
        ).wrap()

    class AireAspirationInput {
        @FormParam("listeAireAspiration")
        lateinit var listeAireAspiration: List<AireAspirationUpsertData>
    }
}
