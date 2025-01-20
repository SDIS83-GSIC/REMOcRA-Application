package remocra.web.adresses

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.AdresseData
import remocra.data.enums.TypePointCarte
import remocra.db.AdresseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.adresse.CreateAdresseUsecase
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.web.AbstractEndpoint

@Path("/adresses")
@Produces(MediaType.APPLICATION_JSON)
class AdresseEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject
    lateinit var adresseRepository: AdresseRepository

    @Inject
    lateinit var createAdresseUsecase: CreateAdresseUsecase

    @Context
    lateinit var securityContext: SecurityContext

    /**
     * Renvoie les Alertes au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @RequireDroits([Droit.ADRESSES_C])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response {
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypePointCarte.ADRESSE,
                securityContext.userInfo!!,
            ),
        ).build()
    }

    @GET
    @Path("/type-sous-type")
    @RequireDroits([Droit.ADRESSES_C])
    fun getTypeSousType(): Response {
        return Response.ok(
            adresseRepository.getTypeAndSousType(),
        ).build()
    }

    @GET
    @Path("/type-anomalie")
    @RequireDroits([Droit.ADRESSES_C])
    fun getTypeAnomalie(): Response {
        return Response.ok(
            adresseRepository.getTypeAnomalie(),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADRESSES_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun createAdresse(adresseData: AdresseData): Response {
        return createAdresseUsecase.execute(
            userInfo = securityContext.userInfo,
            element = adresseData,
        ).wrap()
    }
}
