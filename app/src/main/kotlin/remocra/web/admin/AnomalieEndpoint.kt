package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.AnomalieRepository
import remocra.db.NatureRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.AnomalieCategorie
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.web.AbstractEndpoint

@Produces("application/json; charset=utf-8")
@Path("/anomalie")
class AnomalieEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    @Inject
    lateinit var natureRepository: NatureRepository

    @GET
    @Path("/list")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(): Response {
        val anomaliePoidsList = anomalieRepository.getAllAnomaliePoidsForAdmin().groupBy { it.poidsAnomalieAnomalieId }
        return Response.ok(
            object {
                val anomalieList = anomalieRepository.getAllForAdmin()
                    .map { anomalie ->
                        object {
                            val anomalie: Anomalie = anomalie
                            val anomaliePoidsList: Collection<PoidsAnomalie> = anomaliePoidsList.getOrDefault(anomalie.anomalieId, listOf())
                        }
                    }.sortedBy { it.anomalie.anomalieLibelle }
                val categorieList: Collection<AnomalieCategorie> = anomalieRepository.getAllAnomalieCategorieForAdmin().sortedBy { it.anomalieCategorieLibelle }
                val natureList: Collection<Nature> = natureRepository.getAllForAdmin()
                val typeList: Collection<TypePei> = TypePei.entries
            },
        ).build()
    }
}
