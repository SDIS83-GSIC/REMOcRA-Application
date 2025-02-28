package remocra.web.job

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.JobRepository
import remocra.db.LogLineRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
class JobEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var jobRepository: JobRepository

    @Inject
    lateinit var logLineRepository: LogLineRepository

    @POST
    @Path("/list")
    @RequireDroits([Droit.OPERATIONS_DIVERSES_E])
    fun getJobs(params: Params<JobRepository.Filter, JobRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                jobRepository.listWithTask(params),
                jobRepository.countJobs(params.filterBy),
            ),
        ).build()
    }

    @GET
    @Path("/types-task")
    @Public("Filtre des types de tâches")
    fun getTypesTask() = Response.ok(TypeTask.entries.map { it.literal }).build()

    @POST
    @Path("/log-lines/{jobId}")
    @RequireDroits([Droit.OPERATIONS_DIVERSES_E])
    @Produces(MediaType.APPLICATION_JSON)
    fun getLogLines(@PathParam("jobId") jobId: UUID, params: Params<LogLineRepository.Filter, LogLineRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                logLineRepository.getLogLines(jobId, params),
                logLineRepository.countLogLines(jobId, params.filterBy),
            ),
        ).build()
    }
}
