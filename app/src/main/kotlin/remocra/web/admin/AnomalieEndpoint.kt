package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.AnomalieData
import remocra.data.PoidsAnomalieData
import remocra.db.AnomalieRepository
import remocra.db.NatureRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.AnomalieCategorie
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.usecase.admin.anomalie.CreateAnomalieUseCase
import remocra.usecase.admin.anomalie.DeleteAnomalieUseCase
import remocra.usecase.admin.anomalie.UpdateAnomalieUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=utf-8")
@Path("/anomalie")
class AnomalieEndpoint : AbstractEndpoint() {
    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    @Inject
    lateinit var natureRepository: NatureRepository

    @GET
    @Path("/list")
    @RequireDroits([Droit.ADMIN_ANOMALIES])
    fun list(): Response {
        // Liste des types visite trié dans l'ordre du workflow REMOcRA
        val ordreTypeVisite = listOf(TypeVisite.RECEPTION, TypeVisite.RECO_INIT, TypeVisite.CTP, TypeVisite.ROP, TypeVisite.NP)
        val anomaliePoidsList = anomalieRepository.getAllAnomaliePoidsForAdmin().map { poidsAnomalie ->
            val sortedTypes = poidsAnomalie.poidsAnomalieTypeVisite?.sortedBy { ordreTypeVisite.indexOf(it) }?.toTypedArray()
            poidsAnomalie.copy(poidsAnomalieTypeVisite = sortedTypes)
        }.groupBy { it.poidsAnomalieAnomalieId }
        return Response.ok(
            object {
                val anomalieList = anomalieRepository.getAllForAdmin()
                    .map { anomalie ->
                        object {
                            val anomalie: Anomalie = anomalie
                            val anomaliePoidsList: Collection<PoidsAnomalie> = anomaliePoidsList.getOrDefault(anomalie.anomalieId, listOf())
                        }
                    }
                val categorieList: Collection<AnomalieCategorie> = anomalieRepository.getAllAnomalieCategorieForAdmin()
                val natureList: Collection<Nature> = natureRepository.getAllForAdmin()
                val typeList: Collection<TypePei> = TypePei.entries
            },
        ).build()
    }

    @GET
    @Path("/referentiel")
    @RequireDroits([Droit.ADMIN_ANOMALIES])
    fun referentiel(): Response {
        return Response.ok(
            object {
                val categorieList = anomalieRepository.getAllAnomalieCategorieForAdmin()
                val natureList = natureRepository.getAllForAdmin()
                val typeVisiteList = TypeVisite.entries
            },
        ).build()
    }

    @GET
    @Path("/{anomalieId}")
    @RequireDroits([Droit.ADMIN_ANOMALIES])
    fun get(@PathParam("anomalieId") anomalieId: UUID): Response {
        return Response.ok(
            anomalieRepository.getAnomalieById(anomalieId).let { anomalie ->
                AnomalieData(
                    anomalieId = anomalie.anomalieId,
                    anomalieCode = anomalie.anomalieCode,
                    anomalieLibelle = anomalie.anomalieLibelle,
                    anomalieCommentaire = anomalie.anomalieCommentaire,
                    anomalieAnomalieCategorieId = anomalie.anomalieAnomalieCategorieId,
                    anomalieActif = anomalie.anomalieActif,
                    anomalieProtected = anomalie.anomalieProtected,
                    anomalieRendNonConforme = anomalie.anomalieRendNonConforme,
                    poidsAnomalieSystemeValIndispoTerrestre = anomalie.anomaliePoidsAnomalieSystemeValIndispoTerrestre,
                    poidsAnomalieSystemeValIndispoHbe = anomalie.anomaliePoidsAnomalieSystemeValIndispoHbe,
                    poidsAnomalieList = anomalieRepository.getAnomaliePoidsByAnomalieId(anomalieId).map { poidsAnomalie ->
                        PoidsAnomalieData(
                            poidsAnomalieId = poidsAnomalie.poidsAnomalieId,
                            poidsAnomalieNatureId = poidsAnomalie.poidsAnomalieNatureId,
                            poidsAnomalieTypeVisite = poidsAnomalie.poidsAnomalieTypeVisite,
                            poidsAnomalieValIndispoHbe = poidsAnomalie.poidsAnomalieValIndispoHbe,
                            poidsAnomalieValIndispoTerrestre = poidsAnomalie.poidsAnomalieValIndispoTerrestre,
                        )
                    },
                )
            },
        ).build()
    }

    @Inject
    lateinit var createAnomalieUseCase: CreateAnomalieUseCase

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_ANOMALIES])
    fun post(element: AnomalieData): Response {
        return createAnomalieUseCase.execute(securityContext.userInfo, element).wrap()
    }

    @Inject
    lateinit var updateAnomalieUseCase: UpdateAnomalieUseCase

    @PUT
    @Path("/update/{anomalieId}")
    @RequireDroits([Droit.ADMIN_ANOMALIES])
    fun put(@PathParam("anomalieId") anomalieId: UUID, element: AnomalieData): Response {
        return updateAnomalieUseCase.execute(securityContext.userInfo, element.copy(anomalieId = anomalieId)).wrap()
    }

    @Inject
    lateinit var deleteAnomalieUseCase: DeleteAnomalieUseCase

    @DELETE
    @Path("/delete/{anomalieId}")
    @RequireDroits([Droit.ADMIN_ANOMALIES])
    fun delete(@PathParam("anomalieId") anomalieId: UUID): Response {
        return deleteAnomalieUseCase.execute(securityContext.userInfo, anomalieRepository.getAnomalieById(anomalieId)).wrap()
    }
}
