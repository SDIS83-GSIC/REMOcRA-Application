package remocra.web

import com.google.inject.AbstractModule
import com.google.inject.Binder
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import remocra.api.s.ApiModule
import remocra.log.LogManagerFactory
import remocra.log.LogManagerFactoryImpl
import remocra.web.nomenclatures.NomenclatureModule
import kotlin.reflect.KClass

object WebModule : AbstractModule() {

    override fun configure() {
        bind(LogManagerFactory::class.java).to(LogManagerFactoryImpl::class.java)

        install(ApiModule)
        install(NomenclatureModule)
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
