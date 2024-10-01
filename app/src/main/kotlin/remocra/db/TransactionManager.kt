package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext

/**
 * TransactionManager à injecter dans les usecases si on a besoin de gérer des transactions.
 *
 * Le DSLContext injecté dans les repositories sera celui de la transaction (si transaction il y a).
 */
class TransactionManager @Inject constructor(private val context: DSLContext) {

    fun <T> transactionResult(
        wrapInsideTransaction: Boolean = true,
        transactional: () -> T,
    ): T =
        if (wrapInsideTransaction) {
            context.transactionResult { _ -> transactional() }
        } else {
            transactional()
        }
}
