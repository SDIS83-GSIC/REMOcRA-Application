package remocra.resteasy

import com.google.common.base.Preconditions
import com.google.inject.Inject
import com.google.inject.Injector
import org.jboss.resteasy.core.InjectorFactoryImpl
import org.jboss.resteasy.spi.ConstructorInjector
import org.jboss.resteasy.spi.HttpRequest
import org.jboss.resteasy.spi.HttpResponse
import org.jboss.resteasy.spi.InjectorFactory
import org.jboss.resteasy.spi.ResteasyProviderFactory
import org.jboss.resteasy.spi.metadata.ResourceConstructor
import java.lang.reflect.Constructor
import java.util.concurrent.CompletableFuture

class GuiceInjectorFactory
private constructor(private val injector: Injector, private val delegate: InjectorFactory) :
    InjectorFactory by delegate {

    @Inject constructor(injector: Injector) : this(injector, InjectorFactoryImpl())

    override fun createConstructor(constructor: Constructor<*>, factory: ResteasyProviderFactory) =
        if (constructor.parameterCount == 0) {
            GuiceConstructorInjector(constructor.declaringClass)
        } else {
            Preconditions.checkArgument(
                !constructor.declaringClass.`package`.name.startsWith("remocra."),
                "Les constructeurs des resources et providers JAX-RS remocra ne devraient pas avoir d'argument.",
            )
            delegate.createConstructor(constructor, factory)
        }

    override fun createConstructor(
        constructor: ResourceConstructor,
        providerFactory: ResteasyProviderFactory,
    ): ConstructorInjector {
        Preconditions.checkArgument(constructor.params.isEmpty())
        return GuiceConstructorInjector(constructor.resourceClass.clazz)
    }

    private inner class GuiceConstructorInjector(private val resourceClass: Class<*>) :
        ConstructorInjector {
        override fun construct(unwrapAsync: Boolean) =
            CompletableFuture.completedFuture(injector.getInstance(resourceClass))

        override fun construct(request: HttpRequest, response: HttpResponse, unwrapAsync: Boolean) =
            construct(unwrapAsync)

        override fun injectableArguments(unwrapAsync: Boolean) =
            CompletableFuture.completedFuture<Array<Any>>(arrayOf())

        override fun injectableArguments(
            request: HttpRequest,
            response: HttpResponse,
            unwrapAsync: Boolean,
        ) = injectableArguments(unwrapAsync)
    }
}
