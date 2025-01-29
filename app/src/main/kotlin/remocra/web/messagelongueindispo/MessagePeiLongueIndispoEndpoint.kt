package remocra.web.messagelongueindispo

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.Params
import remocra.db.PeiRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.messagelongueindispo.GetMessagePeiLongueIndispoUseCase
import remocra.usecase.pei.PeiUseCase
import remocra.utils.forbidden
import remocra.web.AbstractEndpoint

@Path("/message-pei-longue-indispo")
@Produces(MediaType.APPLICATION_JSON)
class MessagePeiLongueIndispoEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getMessagePeiLongueIndispoUseCase: GetMessagePeiLongueIndispoUseCase

    @Inject
    lateinit var peiUseCase: PeiUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @Path("/")
    @GET
    @Public("Est rattaché à des types d'organismes, pas à un droit")
    fun getMessageAlerte(): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }

        return Response.ok(getMessagePeiLongueIndispoUseCase.execute(securityContext.userInfo!!)).build()
    }

    @Path("/pei")
    @POST
    @RequireDroits([Droit.PEI_R])
    fun getListePei(
        params: Params<
            PeiRepository.Filter,
            PeiRepository.Sort,
            >,
    ): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }

        return Response.ok(
            peiUseCase.getPeiWithFilterByMessageAlerteForDataTableau(
                params,
                securityContext.userInfo!!,
            ),
        ).build()
    }
}
