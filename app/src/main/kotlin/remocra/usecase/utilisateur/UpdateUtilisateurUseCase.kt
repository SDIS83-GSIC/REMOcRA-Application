package remocra.usecase.utilisateur

import com.google.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.UtilisateurData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.UtilisateurRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.representations.UserRepresentation
import remocra.usecase.AbstractCUDUseCase

class UpdateUtilisateurUseCase : AbstractCUDUseCase<UtilisateurData>(TypeOperation.UPDATE) {
    @Inject private lateinit var keycloakApi: KeycloakApi

    @Inject private lateinit var utilisateurRepository: UtilisateurRepository

    private val logger = LoggerFactory.getLogger(UpdateUtilisateurUseCase::class.java)

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
        // Mise à jour l'utilisateur côté Keycloak
        val updateResponse = keycloakApi.updateUser(
            userInfo!!.accessToken.toAuthorizationHeader(),
            user = UserRepresentation(
                id = element.utilisateurId.toString(),
                username = element.utilisateurUsername,
                firstName = element.utilisateurPrenom,
                lastName = element.utilisateurNom,
                email = element.utilisateurEmail,
                enabled = element.utilisateurActif,
                requiredActions = listOf(),
            ),
            userId = element.utilisateurId.toString(),
        ).execute()

        if (!updateResponse.isSuccessful) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK)
        }

        // Mise à jour côté REMOcRA
        utilisateurRepository.updateUtilisateur(
            Utilisateur(
                utilisateurId = element.utilisateurId,
                utilisateurActif = element.utilisateurActif,
                utilisateurEmail = element.utilisateurEmail,
                utilisateurNom = element.utilisateurNom,
                utilisateurPrenom = element.utilisateurPrenom,
                utilisateurUsername = element.utilisateurUsername,
                utilisateurTelephone = element.utilisateurTelephone,
                utilisateurCanBeNotified = element.utilisateurCanBeNotified,
                utilisateurProfilUtilisateurId = element.utilisateurProfilUtilisateurId,
                utilisateurOrganismeId = element.utilisateurOrganismeId,
                utilisateurIsSuperAdmin = element.utilisateurIsSuperAdmin,
            ),
        )

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: UtilisateurData) {
        if (element.utilisateurUsername.trim().length < 3) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_LENGTH)
        }

        if (utilisateurRepository.checkExistsUsername(element.utilisateurUsername)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_EXISTS)
        }

        if (utilisateurRepository.checkExistsEmail(element.utilisateurUsername)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_EMAIL_EXISTS)
        }
    }
}
