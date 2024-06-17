package remocra.web.nomenclatures

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.NomenclaturesProvider
import remocra.data.enums.TypeNomenclature
import java.util.Locale

@Path("/nomenclatures")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class NomenclaturesEndpoint {

    @Inject
    lateinit var nomenclaturesProvider: NomenclaturesProvider

    @GET
    @Path("/{typeNomenclature}")
    fun getNomenclature(@PathParam("typeNomenclature")typeNomenclatureString: String): Response {
        val typeNomenclature = TypeNomenclature.valueOf(typeNomenclatureString.uppercase(Locale.getDefault()))
        // On laisse planter si le type n'est pas acceptable

        return Response.ok(nomenclaturesProvider.getData(typeNomenclature)).build()
    }
}
