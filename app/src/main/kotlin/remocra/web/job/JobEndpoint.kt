package remocra.web.job

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.JobRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.web.AbstractEndpoint

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
class JobEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var jobRepository: JobRepository

    @POST
    @Path("/list")
    @RequireDroits([Droit.OPERATIONS_DIVERSES_E ])
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
}
