package remocra.apiapachehop.endpoint

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.apiapachehop.data.NotifierCourrierData
import remocra.apiapachehop.usecase.NotifieUseCase
import remocra.apiapachehop.usecase.NotifierCourrierUseCase
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.NotificationMailData
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/apache-hop")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApacheHopEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var notifierCourrierUseCase: NotifierCourrierUseCase

    @Inject
    lateinit var notifierUseCase: NotifieUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @Path("/notifier-courrier")
    @POST
    @NoCsrf("Point d'entrée pour Apache Hop")
    @RequireDroits([Droit.COURRIER_C])
    fun notifierCourrier(
        notifierCourrierData: NotifierCourrierData,
    ): Response {
        return Response.ok(
            notifierCourrierUseCase.execute(
                notifierCourrierData,
                securityContext.userInfo,
            ),
        ).build()
    }

    @Path("/notifier-mail")
    @POST
    @NoCsrf("Point d'entrée pour Apache Hop")
    @RequireDroits([Droit.COURRIER_C])
    fun notifierMail(
        notificationMailData: NotificationMailData,
        @QueryParam("jobId") jobId: UUID,
    ): Response {
        return Response.ok(
            notifierUseCase.execute(
                notificationMailData = notificationMailData,
                idJob = jobId,
            ),
        ).build()
    }
}
