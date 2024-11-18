package remocra.eventbus

import com.google.common.eventbus.AsyncEventBus
import com.google.inject.Provides
import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import remocra.RemocraModule
import remocra.app.ParametresProvider
import remocra.eventbus.mobile.IntegrationTourneeEventListener
import remocra.eventbus.notification.NotificationEventListener
import remocra.eventbus.pei.PeiModifiedEventListener
import remocra.eventbus.tracabilite.TracabiliteEventListener
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object EventBusModule : RemocraModule() {
    override fun configure() {
        val multibinder = KotlinMultibinder.newSetBinder<EventListener<*>>(kotlinBinder)
        multibinder.apply {
            addBinding().to<NotificationEventListener>()
            addBinding().to<IntegrationTourneeEventListener>()
            addBinding().to<TracabiliteEventListener<*>>()
            addBinding().to<PeiModifiedEventListener>()
            addBinding().to<ParametresProvider>()
        }

        bind<EventBus>().to<EventBusImpl>().`in`(Singleton::class.java)
    }

    @Provides
    @Singleton
    fun provideEventBusExecutor(): Executor {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    @Provides
    @Singleton
    fun provideEventBusExecutor(executor: Executor): com.google.common.eventbus.EventBus {
        return AsyncEventBus(executor)
    }
}
