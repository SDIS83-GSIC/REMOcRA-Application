package remocra.usecase.nomenclature

import jakarta.inject.Inject
import remocra.app.DataCacheProvider
import remocra.data.GlobalData
import remocra.data.enums.TypeDataCache
import remocra.usecase.AbstractUseCase
import java.util.UUID

private const val SUFFIXE_ID = "Id"
private const val SUFFIXE_LIBELLE = "Libelle"
private const val SUFFIXE_CODE = "Code"

class NomenclatureUseCase : AbstractUseCase() {

    @Inject lateinit var dataCacheProvider: DataCacheProvider

    fun getListIdLibelle(typeNomenclature: TypeDataCache): List<GlobalData.IdCodeLibelleLienData> {
        val clazz = dataCacheProvider.getPojoClassFromType(typeNomenclature)
        val linkedClazz = dataCacheProvider.getLinkedPojoClassFromType(typeNomenclature)

        // On veut identifier les attributs nommés maclasseId et maclasseLibelle dans Maclasse, pour les invoquer plus tard
        val fieldId = clazz.declaredFields.find { it.name.contains(clazz.simpleName + SUFFIXE_ID, true) }.also { it?.isAccessible = true }
        val fieldLibelle = clazz.declaredFields.find { it.name.contains(clazz.simpleName + SUFFIXE_LIBELLE, true) }.also { it?.isAccessible = true }
        val fieldCode = clazz.declaredFields.find { it.name.contains(clazz.simpleName + SUFFIXE_CODE, true) }.also { it?.isAccessible = true }
        val fieldLink = if (linkedClazz != null) {
            clazz.declaredFields.find { it.name.contains(clazz.simpleName + linkedClazz.simpleName + SUFFIXE_ID, true) }.also { it?.isAccessible = true }
        } else {
            null
        }

        return dataCacheProvider.getData(typeNomenclature).values.map {
            GlobalData.IdCodeLibelleLienData(
                fieldId?.get(it) as UUID,
                fieldCode?.get(it) as String,
                fieldLibelle?.get(it) as String,
                fieldLink?.get(it) as UUID?,
            )
        }
    }
}
