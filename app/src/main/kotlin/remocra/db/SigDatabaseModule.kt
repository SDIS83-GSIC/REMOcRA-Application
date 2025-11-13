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
constructor(private val properties: Properties?) :
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

    companion object {
        fun create(config: Config): SigDatabaseModule {
            if (config.toProperties().isNotEmpty()) {
                return SigDatabaseModule(config.toProperties())
            }
            return SigDatabaseModule(null)
        }

        private fun Config.toProperties() =
            Properties().also {
                entrySet().forEach { (k, v) -> it.setProperty(k, v.unwrapped().toString()) }
            }
    }
}

/**
 * Annotation permettant au moteur d'injection de distinguer 2 cas d'injection distincts ; soit c'est SIG, soit ça ne l'est pas
 */
@BindingAnnotation
annotation class Sig
