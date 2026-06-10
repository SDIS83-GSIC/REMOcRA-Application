package remocra.db.sig

import com.google.inject.BindingAnnotation
import com.google.inject.Provides
import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.inject.Singleton
import remocra.RemocraModule
import remocra.db.sig.strategy.NotConfiguredSigQueries
import remocra.db.sig.strategy.SigOracleQueries
import remocra.db.sig.strategy.SigPostgresQueries
import remocra.db.sig.strategy.SigQueries
import java.util.Properties
import javax.sql.DataSource

/**
 * Classe permettant une connexion à une autre BDD que REMOcRA, afin d'intégrer, dans la version actuelle, les données du SIG du SDIS dans une zone "connue" de REMOcRA
 */
class SigDatabaseModule
private constructor(private val properties: Properties?, private val databaseVendor: DatabaseVendor?) :
    RemocraModule() {

    override fun configure() {
        bind(SigQueries::class.java).to(databaseVendor.toSigQueries())
    }

    @Provides
    @Singleton
    @Sig
    fun provideHikariDataSource(): DataSource? =
        if (properties != null) HikariDataSource(HikariConfig(properties)) else null

    companion object {
        fun create(config: Config): SigDatabaseModule {
            val properties = config.withoutPath("database-vendor").toProperties()
            if (properties.isNotEmpty()) {
                val databaseVendor = config.getString("database-vendor").toDatabaseVendor()
                properties["dataSourceClassName"] = databaseVendor.toDataSourceClassName()
                return SigDatabaseModule(properties, databaseVendor)
            }
            return SigDatabaseModule(null, null)
        }

        private fun Config.toProperties() =
            Properties().also {
                entrySet().forEach { (k, v) -> it.setProperty(k, v.unwrapped().toString()) }
            }

        private fun String.toDatabaseVendor() = when (this.lowercase()) {
            "postgres" -> DatabaseVendor.POSTGRES
            "oracle" -> DatabaseVendor.ORACLE
            else -> throw IllegalArgumentException("Le fournisseur de base de données, pour SIG, n'est pas supporté par REMOcRA (les valeurs possible sont `postgres` ou `oracle`).")
        }

        private fun DatabaseVendor.toDataSourceClassName() = when (this) {
            DatabaseVendor.POSTGRES -> "org.postgresql.ds.PGSimpleDataSource"
            DatabaseVendor.ORACLE -> "oracle.jdbc.pool.OracleDataSource"
        }

        private fun DatabaseVendor?.toSigQueries(): Class<out SigQueries>? = when (this) {
            DatabaseVendor.POSTGRES -> SigPostgresQueries::class.java
            DatabaseVendor.ORACLE -> SigOracleQueries::class.java
            null -> NotConfiguredSigQueries::class.java
        }
    }

    private enum class DatabaseVendor {
        POSTGRES,
        ORACLE,
    }
}

/**
 * Annotation permettant au moteur d'injection de distinguer 2 cas d'injection distincts ; soit c'est SIG, soit ça ne l'est pas
 */
@BindingAnnotation
annotation class Sig
