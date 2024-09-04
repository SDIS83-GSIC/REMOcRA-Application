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
import remocra.data.GlobalData
import remocra.data.enums.TypeDataCache
import java.util.Locale
import java.util.UUID

private const val SUFFIXE_ID = "Id"
private const val SUFFIXE_LIBELLE = "Libelle"
private const val SUFFIXE_CODE = "Code"

@Path("/nomenclatures")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class NomenclaturesEndpoint {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    /**
     * Méthode retournant une map du type de nomenclature demandé, sous forme id -> POJO
     * @param typeNomenclatureString Type de nomenclature à récupérer, sous forme de littéral en minuscule de [TypeDataCache]
     *
     * @return Map<UUID, POJO> wrappé dans une Response
     */
    @GET
    @Path("/{typeNomenclature}")
    @Public("Les nomenclatures ne sont pas liés à un droit")
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
    fun getListIdLibelle(@PathParam("typeNomenclature")typeNomenclatureString: String): Response {
        val typeNomenclature = getTypeNomenclatureFromString(typeNomenclatureString)

        val clazz = dataCacheProvider.getPojoClassFromType(typeNomenclature)

        // On veut identifier les attributs nommés maclasseId et maclasseLibelle dans Maclasse, pour les invoquer plus tard
        val fieldId = clazz.declaredFields.find { it.name.contains(clazz.simpleName + SUFFIXE_ID, true) }.also { it?.isAccessible = true }
        val fieldLibelle = clazz.declaredFields.find { it.name.contains(clazz.simpleName + SUFFIXE_LIBELLE, true) }.also { it?.isAccessible = true }
        val fieldCode = clazz.declaredFields.find { it.name.contains(clazz.simpleName + SUFFIXE_CODE, true) }.also { it?.isAccessible = true }

        // On invoque sur chaque objet et on met le résultat dans un data pour fourniture au front
        return Response.ok(
            dataCacheProvider.getData(typeNomenclature).values.map {
                GlobalData.IdCodeLibelleData(
                    fieldId?.get(it) as UUID,
                    fieldCode?.get(it) as String,
                    fieldLibelle?.get(it) as String,
                )
            },
        ).build()
    }

    private fun getTypeNomenclatureFromString(typeNomenclatureString: String): TypeDataCache {
        val typeDataCache = TypeDataCache.valueOf(typeNomenclatureString.uppercase(Locale.getDefault()))
        // On laisse planter si le type n'est pas acceptable
        return typeDataCache
    }
}
