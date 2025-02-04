package remocra.web.nomenclatures

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.DataCacheProvider
import remocra.auth.Public
import remocra.data.enums.TypeDataCache
import remocra.usecase.nomenclature.NomenclatureUseCase
import remocra.web.AbstractEndpoint
import java.util.Locale

@Path("/nomenclatures")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class NomenclaturesEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject lateinit var nomenclatureUseCase: NomenclatureUseCase

    /**
     * Méthode retournant une map du type de nomenclature demandé, sous forme id -> POJO
     * @param typeNomenclatureString Type de nomenclature à récupérer, sous forme de littéral en minuscule de [TypeDataCache]
     *
     * @return Map<UUID, POJO> wrappé dans une Response
     */
    @GET
    @Path("/{typeNomenclature}")
    @Public("Les nomenclatures ne sont pas liées à un droit")
    fun getNomenclature(@PathParam("typeNomenclature")typeNomenclatureString: String): Response {
        return Response.ok(dataCacheProvider.getData(getTypeNomenclatureFromString(typeNomenclatureString))).build()
    }

    /**
     * Méthode permettant de retourner une liste à destination de Select (Collection<IdLibelleData>).
     *
     * A destination du front, attention à respecter le contrat (format du POJO et TypeNomenclature), faute de quoi on déclenchera une IllegalAccessException non catchée
     *
     * @param typeNomenclatureString Type de nomenclature à récupérer, sous forme de littéral en minuscule de [TypeDataCache]
     * @return Collection<IdCodeLibelleData> wrappé dans une Response
     */
    @GET
    @Path("/list/{typeNomenclature}")
    @Throws(IllegalAccessException::class, IllegalArgumentException::class)
    @Public("Les nomenclatures ne sont pas liées à un droit")
    fun getListIdLibelle(@PathParam("typeNomenclature")typeNomenclatureString: String): Response {
        val typeNomenclature = getTypeNomenclatureFromString(typeNomenclatureString)
        return Response.ok(nomenclatureUseCase.getListIdLibelle(typeNomenclature)).build()
    }

    private fun getTypeNomenclatureFromString(typeNomenclatureString: String): TypeDataCache {
        val typeDataCache = TypeDataCache.valueOf(typeNomenclatureString.uppercase(Locale.getDefault()))
        // On laisse planter si le type n'est pas acceptable
        return typeDataCache
    }
}
