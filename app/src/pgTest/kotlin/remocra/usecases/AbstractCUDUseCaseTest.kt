package remocra.usecases

import jakarta.ws.rs.ForbiddenException
import org.jooq.DSLContext
import org.jooq.exception.NoDataFoundException
import org.jooq.kotlin.fetchSingleValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import remocra.auth.UserInfo
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.TransactionManager
import remocra.db.jooq.fixtures.PostgresqlExtension
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.AbstractUseCase
import java.util.UUID

@ExtendWith(PostgresqlExtension::class)
class AbstractCUDUseCaseTest {
    @Test
    fun testBasicException(dsl: DSLContext) {
        val result = doTest(dsl) { throw UnsupportedOperationException() }
        assert(result is AbstractUseCase.Result.Error)
    }

    @Test
    fun testForbiddenException(dsl: DSLContext) {
        val result = doTest(dsl) { throw ForbiddenException() }
        assert(result is AbstractUseCase.Result.Forbidden)
    }

    @Test
    fun testNoDataFoundException(dsl: DSLContext) {
        val result = doTest(dsl) { throw NoDataFoundException() }
        assert(result is AbstractUseCase.Result.NotFound)
    }

    @Test
    fun testRemocraResponseException(dsl: DSLContext) {
        val result = doTest(dsl) { throw RemocraResponseException(ErrorType.UTILISATEUR_ERROR_INSERT) }
        assert(result is AbstractUseCase.Result.Error)
    }

    @Test
    fun testRemocraResponseExceptionForbiddenResponse(dsl: DSLContext) {
        val result = doTest(dsl) { throw RemocraResponseException(ErrorType.FORBIDDEN) }
        assert(result is AbstractUseCase.Result.Forbidden)
    }

    private fun doTest(
        dsl: DSLContext,
        doThrow: () -> Nothing,
    ): AbstractUseCase.Result {
        val insertedUuid = UUID.randomUUID()
        val sut = object : AbstractCUDUseCase<UUID>(TypeOperation.INSERT) {
            init {
                transactionManager = TransactionManager(dsl)
            }

            override fun checkDroits(userInfo: WrappedUserInfo) {}

            override fun postEvent(element: UUID, userInfo: WrappedUserInfo) {}

            override fun execute(userInfo: WrappedUserInfo, element: UUID): UUID {
                dsl.insertInto(UTILISATEUR)
                    .set(UTILISATEUR.ID, element)
                    .set(UTILISATEUR.USERNAME, element.toString())
                    .set(UTILISATEUR.NOM, element.toString())
                    .set(UTILISATEUR.PRENOM, element.toString())
                    .set(UTILISATEUR.EMAIL, element.toString())
                    .set(UTILISATEUR.ACTIF, true)
                    .execute()
                doThrow()
            }

            override fun checkContraintes(userInfo: WrappedUserInfo, element: UUID) {}
        }

        val result = sut.execute(
            WrappedUserInfo().apply {
                userInfo = UserInfo(
                    Utilisateur(
                        UUID.randomUUID(),
                        true,
                        "admin@example.com",
                        "admin",
                        "admin",
                        "admin",
                        null,
                        false,
                        null,
                        null,
                        true,
                        null,
                    ),
                    Droit.entries.toSet(),
                    null,
                    emptySet(),
                    null,
                    TypeSourceModification.REMOCRA_WEB,
                )
            },
            insertedUuid,

        )
        // Vérifie que le rollback a bien eu lieu
        assertEquals(0, dsl.selectCount().from(UTILISATEUR).where(UTILISATEUR.ID.eq(insertedUuid)).fetchSingleValue())
        return result
    }
}
