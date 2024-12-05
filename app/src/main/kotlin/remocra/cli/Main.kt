package remocra.cli

import com.google.inject.Guice
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import de.thetaphi.forbiddenapis.SuppressForbidden
import org.flywaydb.core.Flyway
import picocli.CommandLine
import picocli.CommandLine.Command
import remocra.app.AppModule
import remocra.auth.AuthModule
import remocra.csv.CsvModule
import remocra.db.DatabaseModule
import remocra.db.SigDatabaseModule
import remocra.eventbus.EventBusModule
import remocra.healthcheck.HealthModule
import remocra.http.HttpServerModule
import remocra.json.JsonModule
import remocra.keycloak.KeycloakModule
import remocra.schedule.ScheduleModule
import remocra.sentry.SentryModule
import remocra.web.WebModule
import remocra.web.geoserver.GeoserverModule
import kotlin.system.exitProcess

/**
 * Cette classe est le seul point d'entrée de REMOcRA. Toutes les actions sont définies comme des
 * *sous-commandes*.
 */
// TODO: fournir un IVersionProvider
@Command(
    name = "remocra",
    subcommands = [CommandLine.HelpCommand::class],
    mixinStandardHelpOptions = true,
)
class Main : Runnable {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty(
                "java.util.logging.manager",
                "org.apache.logging.log4j.jul.LogManager",
            )
            exitProcess(CommandLine(Main()).execute(*args))
        }
    }

    /** Commande par défaut, lorsque qu'aucune sous-commande n'est précisée. */
    @SuppressForbidden
    override fun run() {
        // XXX: est-ce qu'on devrait faire un serve() par défaut ?
        CommandLine.usage(this, System.out)
    }

    fun init(): Config {
        return ConfigFactory.load()
    }

    private fun withFlyway(action: Flyway.() -> Unit) {
        val config = init()

        Guice.createInjector(DatabaseModule.create(config.getConfig("remocra.database")))
            .getInstance(Flyway::class.java)
            .apply(action)
    }

    @Command(name = "migrate-db", description = ["Met à jour le schéma de la base de données"])
    fun migrateDb() = withFlyway { migrate() }

    @Command(
        name = "validate-db",
        description = ["Vérifie que le schéma de la base de données est à jour"],
    )
    fun validateDb() = withFlyway { validate() }

    @Command(
        name = "info-db",
        description = ["Affiche les informations sur la version du schéma de la base de données"],
    )
    fun infoDb() = withFlyway { info() }

    @Command(
        name = "repair-db",
        description = ["Répare la table de suivi de la version de schéma de la base de données"],
    )
    fun repairDb() = withFlyway { repair() }

    @Command(description = ["Démarre le serveur"])
    fun serve() {
        val config = init()
        val serve =
            Guice.createInjector(
                AppModule.create(config.getConfig("remocra.app")),
                WebModule,
                JsonModule,
                CsvModule,
                EventBusModule.create(config.getConfig("remocra.mail")),
                AuthModule.create(config.getConfig("remocra.authn")),
                KeycloakModule.create(config.getConfig("remocra.authn")),
                GeoserverModule.create(config.getConfig("remocra.geoserver")),
                HealthModule.create(config.getConfig("remocra.health")),
                SentryModule.create(config.getConfig("remocra.sentry")),
                HttpServerModule.create(config.getConfig("remocra.http")),
                DatabaseModule.create(config.getConfig("remocra.database")),
                SigDatabaseModule.create(config.getConfig("remocra.database-sig")),
                ScheduleModule,
            )
                .getInstance(Serve::class.java)
        serve.start()
    }
}
