package remocra.db

import com.google.common.io.Resources
import com.google.inject.Provides
import com.google.inject.Singleton
import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jooq.ConnectionProvider
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.TransactionProvider
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.ThreadLocalTransactionProvider
import remocra.RemocraModule
import java.util.Locale
import java.util.Properties
import javax.sql.DataSource

class DatabaseModule
private constructor(private val sqlDialect: SQLDialect, private val properties: Properties) :
    RemocraModule() {

    @Provides
    @Singleton
    fun provideHikariDataSource(): DataSource = HikariDataSource(HikariConfig(properties))

    @Provides
    @Singleton
    fun provideDSLContext(
        connectionProvider: ConnectionProvider,
        transactionProvider: TransactionProvider,
    ): DSLContext =
        DSL.using(
            DefaultConfiguration().set(connectionProvider).set(sqlDialect).set(transactionProvider),
        )

    @Provides
    @Singleton
    fun provideDataSource(dataSource: DataSource): ConnectionProvider =
        DataSourceConnectionProvider(dataSource)

    @Provides
    @Singleton
    fun provideTransactionProvider(connectionProvider: ConnectionProvider): TransactionProvider =
        ThreadLocalTransactionProvider(connectionProvider)

    @Provides
    fun providesFlyway(dataSource: DataSource): Flyway =
        Flyway.configure()
            .configuration(
                Properties().apply {
                    Resources.getResource("db/flyway.conf").openStream().reader().use { load(it) }
                },
            )
            .dataSource(dataSource)
            .load()

    companion object {
        fun create(config: Config) =
            DatabaseModule(
                SQLDialect.valueOf(config.getString("sql-dialect").uppercase(Locale.US)),
                config.withoutPath("sql-dialect").toProperties(),
            )

        private fun Config.toProperties() =
            Properties().also {
                entrySet().forEach { (k, v) -> it.setProperty(k, v.unwrapped().toString()) }
            }
    }
}
