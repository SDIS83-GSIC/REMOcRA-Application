package remocra.web.nomenclatures

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object NomenclatureModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(NomenclaturesEndpoint::class)
        binder.registerResources(NomenclatureCodeLibelleEndpoint::class)
        binder.registerResources(NomenclatureAnomalieCategorieEndpoint::class)
    }
}
