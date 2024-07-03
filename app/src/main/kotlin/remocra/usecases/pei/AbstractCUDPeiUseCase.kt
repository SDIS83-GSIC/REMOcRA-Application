package remocra.usecases.pei

import com.google.inject.Inject
import remocra.authn.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.Pei
import remocra.eventbus.EventBus
import remocra.eventbus.pei.PeiModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecases.AbstractCUDUseCase
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * Classe mère des useCases des opérations C, U, D des PEI.
 * Permet de gérer les opérations transverses, calcul de la numérotation, de la dispo, et déclenchement des events, communes aux différents types d'opérations
 */
abstract class AbstractCUDPeiUseCase(private val typeOperation: TypeOperation) : AbstractCUDUseCase<Pei>() {
    @Inject
    lateinit var eventBus: EventBus

    override fun postEvent(element: Pei, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.idUtilisateur, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = OffsetDateTime.now(ZoneId.systemDefault()),
            ),
        )
        eventBus.post(PeiModifiedEvent(element.peiId))
    }

    override fun execute(element: Pei): Pei {
        var peiTravail: Pei = element
        // TODO les useCases ne sont pas encore mergés, donc leur utilisation arrivera dans un commit ultérieur

// Tout est à jour, on peut enregistrer l'élément :
        executeSpecific(peiTravail)

        // On rend la main au parent pour la logique d'événements
        return peiTravail
    }

    /**
     * Méthode permettant de décrire tout ce qui est spécifique à chaque opération, typiquement le service métier à appeler
     */
    abstract fun executeSpecific(element: Pei): Any?
}
