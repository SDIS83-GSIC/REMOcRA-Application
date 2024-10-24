package remocra.web.nature

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.DataCacheProvider
import remocra.auth.Public
import remocra.data.GlobalData

@Path("/marque-pibi")
@Produces(MediaType.APPLICATION_JSON)
class MarquePibiEndPoint {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @GET
    @Path("/get")
    @Public("Les marques ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun get(): Response {
        return Response.ok(
            dataCacheProvider.getMarquesPibi().values.map {
                GlobalData.IdCodeLibelleData(
                    id = it.marquePibiId,
                    code = it.marquePibiCode,
                    libelle = it.marquePibiLibelle,
                )
            },
        )
            .build()
    }
}
