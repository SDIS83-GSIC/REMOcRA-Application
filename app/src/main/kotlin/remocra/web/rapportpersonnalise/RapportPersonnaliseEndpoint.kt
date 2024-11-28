package remocra.web.rapportpersonnalise

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.RapportPersonnaliseData
import remocra.data.RapportPersonnaliseParametreData
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.rapportpersonnalise.CreateRapportPersonnaliseUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/rapport-personnalise")
@Produces(MediaType.APPLICATION_JSON)
class RapportPersonnaliseEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    lateinit var createRapportPersonnaliseUseCase: CreateRapportPersonnaliseUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    fun getAll(params: Params<RapportPersonnaliseRepository.Filter, RapportPersonnaliseRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = rapportPersonnaliseRepository.getAllForAdmin(params),
                count = rapportPersonnaliseRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/get-type-module")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    fun getTypeModule() =
        Response.ok(TypeModuleRapportCourrier.entries).build()

    @Path("/create")
    @POST
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    fun create(element: RapportPersonnaliseInput): Response =
        createRapportPersonnaliseUseCase.execute(
            securityContext.userInfo,
            RapportPersonnaliseData(
                rapportPersonnaliseId = element.rapportPersonnaliseId ?: UUID.randomUUID(),
                rapportPersonnaliseActif = element.rapportPersonnaliseActif,
                rapportPersonnaliseCode = element.rapportPersonnaliseCode,
                rapportPersonnaliseLibelle = element.rapportPersonnaliseLibelle,
                rapportPersonnaliseChampGeometrie = element.rapportPersonnaliseChampGeometrie,
                rapportPersonnaliseDescription = element.rapportPersonnaliseDescription,
                rapportPersonnaliseSourceSql = element.rapportPersonnaliseSourceSql,
                rapportPersonnaliseModule = element.rapportPersonnaliseModule,
                listeProfilDroitId = element.listeProfilDroitId,
                listeRapportPersonnaliseParametre = element.listeRapportPersonnaliseParametre,
            ),
        ).wrap()

    class RapportPersonnaliseInput {
        var rapportPersonnaliseId: UUID? = null
        val rapportPersonnaliseActif: Boolean = false
        lateinit var rapportPersonnaliseCode: String
        lateinit var rapportPersonnaliseLibelle: String
        val rapportPersonnaliseChampGeometrie: String? = null
        val rapportPersonnaliseDescription: String? = null
        lateinit var rapportPersonnaliseSourceSql: String
        lateinit var rapportPersonnaliseModule: TypeModuleRapportCourrier
        var listeProfilDroitId: Collection<UUID> = listOf()
        val listeRapportPersonnaliseParametre: Collection<RapportPersonnaliseParametreData> = listOf()
    }
}
