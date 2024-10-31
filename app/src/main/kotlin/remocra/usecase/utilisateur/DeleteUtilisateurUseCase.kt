package remocra.usecase.utilisateur

import com.google.inject.Inject
import org.jooq.exception.NoDataFoundException
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.UtilisateurData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.UtilisateurRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.usecase.AbstractCUDUseCase

class DeleteUtilisateurUseCase : AbstractCUDUseCase<UtilisateurData>(TypeOperation.DELETE) {
    @Inject private lateinit var keycloakApi: KeycloakApi

    @Inject private lateinit var utilisateurRepository: UtilisateurRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_FORBIDDEN)
        }
    }

    override fun postEvent(element: UtilisateurData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.utilisateurId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.UTILISATEUR,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: UtilisateurData): UtilisateurData {
        // On supprime dans keycloak
        val deleteResponse = keycloakApi.deleteUser(userInfo!!.accessToken.toAuthorizationHeader(), element.utilisateurId.toString())
            .execute()

        if (!deleteResponse.isSuccessful) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_SUPPRESSION_KEYCLOAK)
        }

        when (utilisateurRepository.deleteUtilisateur(element.utilisateurId)) {
            1 -> Result.Success()
            0 -> throw NoDataFoundException("L'utilisateur n'existe pas")
            else -> throw RuntimeException("Erreur lors de la suppression de l'utilisateur")
        }
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: UtilisateurData) {
        // Si l'utilisateur a réservé une tournée, on interdit la suppression
        if (utilisateurRepository.checkExistsInTournee(element.utilisateurId)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_TOURNEE_RESERVEE)
        }
    }
}
