package remocra.eventbus

import com.google.common.eventbus.AsyncEventBus
import com.google.inject.Provides
import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import remocra.eventbus.notification.NotificationEventListener
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object EventBusModule : KotlinModule() {
    override fun configure() {
        val multibinder = KotlinMultibinder.newSetBinder<EventListener<*>>(kotlinBinder)
        multibinder.addBinding().to<NotificationEventListener>()
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
