package remocra.db.jooq.fixtures

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.util.Modules
import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway
import org.jooq.ConnectionProvider
import org.jooq.DSLContext
import org.jooq.TransactionProvider
import org.jooq.impl.DSL
import org.jooq.impl.DefaultTransactionProvider
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import remocra.db.DatabaseModule
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException

class PostgresqlExtension : ParameterResolver {
    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        DSLContext::class.java == parameterContext.parameter.type

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        extensionContext
            .getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent(State::class.java, { createState(CTX) }, State::class.java)
            .dsl()
}

private fun createState(dsl: DSLContext) = State().apply { begin(dsl) }

private val CTX by lazy {
    val injector: Injector =
        Guice.createInjector(
            Modules.override(
                DatabaseModule.create(ConfigFactory.load().getConfig("remocra.database")),
            )
                .with(
                    object : AbstractModule() {
                        @Provides
                        @Singleton
                        fun provideTransactionProvider(
                            connectionProvider: ConnectionProvider,
                        ): TransactionProvider = DefaultTransactionProvider(connectionProvider)
                    },
                ),
        )
    injector.getInstance(Flyway::class.java).validate()
    injector.getInstance(DSLContext::class.java)
}

private class State : CloseableResource {
    private val dsl = CompletableFuture<DSLContext>()
    private lateinit var transaction: CountDownLatch
    private lateinit var end: CompletableFuture<Void>
    fun dsl(): DSLContext {
        return dsl.get()
    }
    fun begin(dsl: DSLContext) {
        transaction = CountDownLatch(1)
        end =
            dsl
                .transactionAsync { configuration ->
                    this.dsl.complete(DSL.using(configuration))
                    this.transaction.await()
                    throw RollbackException()
                }
                .toCompletableFuture()
    }
    override fun close() {
        transaction.countDown()
        try {
            end.get()
        } catch (e: ExecutionException) {
            if (e.cause !is RollbackException) {
                throw e
            }
        }
    }
}
private class RollbackException : RuntimeException()
