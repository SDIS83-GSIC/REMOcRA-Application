package remocra.web

import com.google.inject.Binder
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import remocra.RemocraModule
import remocra.api.endpoint.ApiModule
import remocra.log.LogManagerFactory
import remocra.log.LogManagerFactoryImpl
import remocra.web.admin.AdminModule
import remocra.web.anomalie.AnomalieModule
import remocra.web.appsettings.AppSettingsModule
import remocra.web.commune.CommuneModule
import remocra.web.courrier.CourrierModule
import remocra.web.documents.DocumentModule
import remocra.web.nature.NatureModule
import remocra.web.natureDeci.NatureDeciModule
import remocra.web.nomenclatures.NomenclatureModule
import remocra.web.organisme.OrganismeModule
import remocra.web.pei.PeiModule
import remocra.web.visite.VisiteModule
import kotlin.reflect.KClass

object WebModule : RemocraModule() {

    override fun configure() {
        install(PeiModule)
        install(CommuneModule)
        install(NatureModule)
        install(OrganismeModule)
        install(NatureDeciModule)
        install(ApiModule)
        install(VisiteModule)
        install(AnomalieModule)
        install(NomenclatureModule)
        install(AppSettingsModule)
        install(DocumentModule)
        install(AdminModule)
        install(CourrierModule)
        bind(LogManagerFactory::class.java).to(LogManagerFactoryImpl::class.java)
    }

    private inline fun <reified T> registerResource() {
        binder().registerResource<T>()
    }
}

inline fun <reified T> Binder.registerResource() = registerResource(T::class)

fun Binder.registerResource(clazz: KClass<*>) =
    Multibinder.newSetBinder(this, object : TypeLiteral<Class<*>>() {})
        .addBinding()
        .toInstance(clazz.java)

fun Binder.registerResources(vararg classes: KClass<*>) =
    Multibinder.newSetBinder(this, object : TypeLiteral<Class<*>>() {}).run {
        classes.forEach { addBinding().toInstance(it.java) }
    }
