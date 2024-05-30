package remocra.eventbus

import com.google.common.eventbus.AsyncEventBus
import com.google.inject.Provides
import com.google.inject.Singleton
import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import remocra.RemocraModule
import remocra.app.ParametresProvider
import remocra.eventbus.notification.NotificationEventListener
import remocra.eventbus.tracabilite.TracabiliteEventListener
import remocra.getStringOrNull
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class EventBusModule(private val settings: MailSettings) : RemocraModule() {
    override fun configure() {
        val multibinder = KotlinMultibinder.newSetBinder<EventListener<*>>(kotlinBinder)
        multibinder.apply {
            addBinding().to<NotificationEventListener>()
            addBinding().to<TracabiliteEventListener<*>>()
            addBinding().to<ParametresProvider>()
        }

        bind<EventBus>().to<EventBusImpl>().`in`(Singleton::class.java)
        bind(MailSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config) =
            EventBusModule(
                MailSettings(
                    from = config.getString("from"),
                    smtpUrl = config.getString("smtp-url"),
                    smtpPort = config.getInt("smtp-port"),
                    smtpUser = config.getStringOrNull("smtp-user"),
                    smtpPassword = config.getStringOrNull("smtp-password"),
                    urlSite = config.getString("url-site"),
                ),
            )
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

data class MailSettings(val from: String, val smtpUrl: String, val smtpPort: Int, val smtpUser: String?, val smtpPassword: String?, val urlSite: String)
