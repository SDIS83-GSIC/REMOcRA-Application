package remocra.mail

import com.google.inject.Provides
import com.typesafe.config.Config
import remocra.RemocraModule
import remocra.getStringOrNull
import remocra.healthcheck.HealthModule

class MailModule(private val settings: MailSettings) : RemocraModule() {
    companion object {
        fun create(config: Config) = MailModule(
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
    fun provideMailSettings() = settings

    override fun configure() {
        HealthModule.addHealthCheck(binder(), "mail").to(MailHealthChecker::class.java)
    }
}

data class MailSettings(val from: String, val smtpUrl: String, val smtpPort: Int, val smtpUser: String?, val smtpPassword: String?, val urlSite: String)
