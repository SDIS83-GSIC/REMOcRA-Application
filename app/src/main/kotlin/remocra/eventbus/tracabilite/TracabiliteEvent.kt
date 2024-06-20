package remocra.eventbus.tracabilite

import com.google.inject.Inject
import remocra.data.AuteurTracabiliteData
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.eventbus.Event
import java.time.OffsetDateTime
import java.util.UUID

/***
 * Pour remplir la traçabilité, il nous faut
 * * Un pojo contenant l'état actuel de l'objet
 * * L'id technique de l'objet
 * * Le type de l'objet
 * * Le type de l'opération (UPDATE, INSERT, DELETE)
 * * L'auteur de l'opération : si on n'a pas de UserInfo connecté et donc qu'on est dans une task on doit renvoyer le user systeme
 * * Date de l'opération
 */
class TracabiliteEvent<T> @Inject constructor(
    val pojo: T,
    val pojoId: UUID,
    val typeOperation: TypeOperation,
    val typeObjet: TypeObjet,
    val auteurTracabilite: AuteurTracabiliteData,
    val date: OffsetDateTime,
) : Event
