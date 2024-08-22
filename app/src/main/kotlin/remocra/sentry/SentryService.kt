package remocra.sentry

import com.google.inject.Inject
import io.sentry.Sentry
import io.sentry.SentryOptions
import io.sentry.log4j2.SentryAppender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import java.util.Optional

class SentryService
@Inject
constructor(
    @SentryJava private val sentryOptions: Optional<SentryOptions>,
    private val sentryAppenderConfig: SentryAppenderConfig,
) {
    fun start() {
        sentryOptions.ifPresent {
            // On initialise Sentry *avant* de mettre le SentryAppender
            // TODO: utiliser un OptionsConfiguration (init(SentryOptions) et API.internal)
            //       sauf que OptionsConfiguration.configure doit muter un SentryOptions existant et
            // que le SentryOptions.merge est package-private
            //       du coup notre ConfigPropertiesProvider ne sert plus à grand chose
            //       je ne sais pas trop si on pourra garder le côté générique du
            // ConfigPropertiesProvider (passer par du set propriété par propriété)
            Sentry.init(it)
            val ctx = LogManager.getContext(false) as LoggerContext
            val appender =
                SentryAppender.createAppender(
                    "Sentry",
                    sentryAppenderConfig.minimumBreadcrumbLevel,
                    sentryAppenderConfig.minimumEventLevel,
                    null,
                    it.isDebug,
                    null,
                    null,
                )
            appender!!.start()
            ctx.configuration.addAppender(appender)
            ctx.configuration.rootLogger.addAppender(appender, null, null)
            ctx.updateLoggers()
        }
    }
}
