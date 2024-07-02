package remocra.usecases
import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import org.jooq.exception.NoDataFoundException
import remocra.authn.UserInfo
import remocra.db.TransactionManager
abstract class AbstractCUDUseCase<T : Any> {
    @Inject lateinit var transactionManager: TransactionManager

    /**
     * Vérifie les droits de l'utilisateur, et déclenche une [ForbiddenException] si l'utilisateur
     * n'est pas habilité convenablement
     */
    protected abstract fun checkDroits(userInfo: UserInfo)

    /**
     * Vérifie si l'action peut être faite
     * exemple : dans le cas d'une suppression de PEI, on vérifie s'il est utilisé quelque part
     * Doit déclencher des [Exception] si les contraintes ne sont pas vérifiées
     */
    protected abstract fun checkContraintes(element: T)

    /**
     * Exécute la logique métier d'insert / update / delete. Le type de retour est *facultatif*, le
     * cas nominal est un non-retour, ce qui va déboucher sur un Result.Success wrappé dans un
     * Response.ok() vide
     */
    protected abstract fun execute(element: T): Any?

    /**
     * Point d'entrée du useCase permettant de vérifier les droits et de déclencher l'action au sein
     * d'une transaction.
     * @return Result : dans le cas d'un success, on peut au besoin faire retourner n'importe quoi
     * dans le success, pour le faire transiter côté client, mais ce n'est pas obligatoire.
     */
    fun execute(userInfo: UserInfo?, element: T): Result {
        try {
            if (userInfo == null) {
                throw ForbiddenException()
            }
            checkDroits(userInfo)
            checkContraintes(element)
            val result = transactionManager.transactionResult { execute(element) }
            postEvent(element, userInfo)
            return Result.Success(
                result,
            )
        } catch (e: ForbiddenException) {
            return Result.Forbidden(e.message)
        } catch (ndfe: NoDataFoundException) {
            return Result.NotFound(ndfe.message)
        } catch (e: Exception) {
            return Result.Error(e.message)
        }
    }

    /**
     * Permet de lancer un évènement suite à la mise à jour / insertion ou suppression d'un objet
     */
    protected abstract fun postEvent(element: T, userInfo: UserInfo)

    sealed class Result {
        data class Success(val entity: Any? = null) : Result()
        data class NotFound(val message: String?) : Result()
        data class Forbidden(val message: String?) : Result()
        data class Error(val message: String?) : Result()
    }
}
