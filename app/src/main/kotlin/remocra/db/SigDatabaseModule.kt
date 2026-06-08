package remocra.db

import com.google.inject.BindingAnnotation
import com.google.inject.Provides
import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.annotation.Nullable
import jakarta.inject.Singleton
import org.jooq.ConnectionProvider
import org.jooq.DSLContext
import org.jooq.TransactionProvider
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.ThreadLocalTransactionProvider
import remocra.RemocraModule
import java.util.Properties
import javax.sql.DataSource

/**
 * Classe permettant une connexion à une autre BDD que REMOcRA, afin d'intégrer, dans la version actuelle, les données du SIG du SDIS dans une zone "connue" de REMOcRA
 */
class SigDatabaseModule
constructor(private val properties: Properties?, private val databaseVendor: DatabaseVendor?) :
    RemocraModule() {

    @Provides
    @Singleton
    @Sig
    fun provideHikariDataSource(): DataSource? =
        if (properties != null) HikariDataSource(HikariConfig(properties)) else null

    @Provides
    @Singleton
    @Sig
    fun provideDSLContextSig(
        @Nullable @Sig connectionProvider: ConnectionProvider?,
        @Nullable @Sig transactionProvider: TransactionProvider?,
    ): DSLContext? =
        properties?.let {
            DSL.using(
                DefaultConfiguration().set(connectionProvider).set(transactionProvider),
            )
        }

    @Provides
    @Singleton
    @Sig
    fun provideConnectionProviderSig(@Sig @Nullable dataSource: DataSource?): ConnectionProvider? =
        dataSource?.let { DataSourceConnectionProvider(it) }

    @Provides
    @Singleton
    @Sig
    fun provideTransactionProviderSig(@Sig @Nullable connectionProvider: ConnectionProvider?): TransactionProvider? =
        connectionProvider?.let { ThreadLocalTransactionProvider(it) }

    @Provides
    fun provideDatabaseVendor(): DatabaseVendor? {
        return databaseVendor
    }

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
            else -> throw IllegalArgumentException("Le fournisseur de base de données, pour SIG, n'est pas supporté par REMOcRA (les valeurs possible sont `postgres`).")
        }

        private fun DatabaseVendor.toDataSourceClassName() = when (this) {
            DatabaseVendor.POSTGRES -> "org.postgresql.ds.PGSimpleDataSource"
        }
    }
}

/**
 * Annotation permettant au moteur d'injection de distinguer 2 cas d'injection distincts ; soit c'est SIG, soit ça ne l'est pas
 */
@BindingAnnotation
annotation class Sig

enum class DatabaseVendor {
    POSTGRES,
}
