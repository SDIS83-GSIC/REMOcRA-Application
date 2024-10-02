package remocra.usecase
import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.core.Response
import org.jooq.exception.NoDataFoundException
import remocra.auth.UserInfo
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.eventbus.EventBus
import remocra.exception.RemocraResponseException
import remocra.web.AbstractEndpoint.Result
import java.time.Clock

abstract class AbstractCUDUseCase<T : Any>(val typeOperation: TypeOperation) {
    @Inject lateinit var transactionManager: TransactionManager

    @Inject lateinit var eventBus: EventBus

    @Inject lateinit var clock: Clock

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
    protected abstract fun checkContraintes(userInfo: UserInfo?, element: T)

    /** Exécute la logique métier d'insert / update / delete.
     *
     * Retourner l'entité la plus à jour afin de permettre aux mécanismes ultérieurs (événements) de fonctionner au mieux
     *
     * @param element L'élément à enregistrer
     * @return T l'élément enregistré
     */
    protected abstract fun execute(userInfo: UserInfo?, element: T): T

    /**
     * Point d'entrée du useCase permettant de vérifier les droits et de déclencher l'action au sein
     * d'une transaction.
     * Pour une exécution "simple" du useCase, le transactionManager est défini en interne.
     * En revanche, afin de permettre un enchaînement des useCases au sein d'une seule transaction, il faudra définir
     * le *TransactionManager* dans l'appel de plus haut niveau, et le faire transiter dans tous les appels subséquents
     * @return Result : dans le cas d'un success, on peut au besoin faire retourner n'importe quoi
     * dans le success, pour le faire transiter côté client, mais ce n'est pas obligatoire.
     */
    fun execute(userInfo: UserInfo?, element: T, mainTransactionManager: TransactionManager? = null): Result {
        try {
            if (userInfo == null) {
                throw ForbiddenException()
            }
            checkDroits(userInfo)
            checkContraintes(userInfo, element)

            // On utilise le transactionManager parent s'il est fourni, sinon fallback sur celui qui est injecté
            val savedElement = (mainTransactionManager ?: transactionManager).transactionResult { execute(userInfo, element) }
            postEvent(savedElement, userInfo)

            // On veut renvoyer un HTTP 201 lors d'une création
            return if (TypeOperation.INSERT == typeOperation) Result.Created(savedElement) else Result.Success(savedElement)
        } catch (e: ForbiddenException) {
            return Result.Forbidden(e.message)
        } catch (ndfe: NoDataFoundException) {
            return Result.NotFound(ndfe.message)
        } catch (rre: RemocraResponseException) {
            // Par propreté, on transforme en "vrai" Forbidden
            if (rre.status == Response.Status.FORBIDDEN) {
                return Result.Forbidden(rre.message)
            }
            return Result.Error(rre.message)
        } catch (e: Exception) {
            return Result.Error(e.message)
        }
    }

    /**
     * Permet de lancer un évènement suite à la mise à jour / insertion ou suppression d'un objet
     */
    protected abstract fun postEvent(element: T, userInfo: UserInfo)
}
