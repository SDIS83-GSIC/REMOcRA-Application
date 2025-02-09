package remocra.tasks

import com.google.inject.Inject
import org.jooq.exception.IOException
import remocra.auth.AuthModule
import remocra.auth.UserInfo
import remocra.data.NotificationMailData
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.UserRepresentation
import java.util.UUID

class SynchroUtilisateurTask @Inject constructor() : SchedulableTask<SynchroUtilisateurTaskParameters, SchedulableTaskResults>() {

    companion object {
        const val MAX_RESULTS = 100
    }

    @Inject lateinit var keycloakApi: KeycloakApi

    @Inject lateinit var keycloakToken: KeycloakToken

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    @Inject lateinit var keycloakClient: AuthModule.KeycloakClient

    override fun execute(parameters: SynchroUtilisateurTaskParameters?, userInfo: UserInfo): SchedulableTaskResults? {
        var i = 0
        var fini = false

        val response = keycloakToken.getToken(
            keycloakClient.clientId,
            keycloakClient.clientSecret,
        ).execute().body()!!

        val token = "${response.tokenType} ${response.accessToken}"

        val utilisateursRemocra = utilisateurRepository.getAll()

        // On désactive les utilisateurs avant la synchro
        utilisateurRepository.desactiveAllUsers()

        var nbUtilisateurUpdate = 0
        var nbUtilisateurAdd = 0
        var nbUtilisateurSuppress = 0

        val utilisateursInactifs = keycloakApi.getUsersInactif(token).execute().body()

        while (!fini) {
            try {
                val usersKeycloak =
                    keycloakApi
                        .getUsers(token, i, i + MAX_RESULTS)
                        .execute()
                if (!usersKeycloak.isSuccessful) {
                    logManager.error("[TASK_SYNCHRO_UTILISATEUR] Erreur lors de la récupération des utilisateurs de keycloak : ${usersKeycloak.errorBody()}")
                    return null
                }

                if (usersKeycloak.body()?.size == 0 || usersKeycloak.body() == null) {
                    fini = true
                }

                for (userRepresentation: UserRepresentation in usersKeycloak.body()!!) {
                    // Si l'utilisateur est déjà en base
                    val utilisateurExistant = utilisateursRemocra.firstOrNull { it.utilisateurId == UUID.fromString(userRepresentation.id) }
                    if (utilisateurExistant != null) {
                        // On met à jour les propriétés si besoin
                        val inactif = utilisateursInactifs?.map { it.username }?.contains(utilisateurExistant.utilisateurUsername) ?: false
                        if (utilisateurExistant.utilisateurEmail != userRepresentation.email ||
                            utilisateurExistant.utilisateurNom != userRepresentation.lastName ||
                            utilisateurExistant.utilisateurPrenom != userRepresentation.firstName ||
                            (utilisateurExistant.utilisateurActif != !inactif)
                        ) {
                            utilisateurRepository.updateUtilisateur(
                                idUtilisateur = utilisateurExistant.utilisateurId,
                                nom = userRepresentation.lastName,
                                prenom = userRepresentation.firstName,
                                email = userRepresentation.email,
                                actif = !inactif,
                            )
                            nbUtilisateurUpdate++
                            logManager.info(
                                "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur" +
                                    " ${utilisateurExistant.utilisateurUsername} a été mis à jour",
                            )
                        }

                        // Si aucune valeur n'a été modifié et que l'utilisateur était actif, on le remet
                        if (!inactif) {
                            utilisateurRepository.setActif(true, utilisateurExistant.utilisateurId)
                        }
                    } else {
                        val utilisateur = utilisateurRepository.insertUtilisateur(
                            id = UUID.fromString(userRepresentation.id),
                            email = userRepresentation.email,
                            prenom = userRepresentation.firstName,
                            nom = userRepresentation.lastName,
                            username = userRepresentation.username,
                            actif = utilisateursInactifs?.map { it.username }
                                ?.contains(userRepresentation.username) == false,
                        )
                        nbUtilisateurAdd++
                        logManager.info(
                            "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur ${utilisateur.utilisateurUsername} " +
                                "a été inséré",
                        )
                    }
                }
            } catch (e: IOException) {
                logManager.error("[TASK_SYNCHRO_UTILISATEUR] Erreur lors de la synchronisation des utilisateurs : ${e.message}")
                return null
            }
            i += MAX_RESULTS
        }

        if (parameters?.canSuppressUser == true) {
            nbUtilisateurSuppress = utilisateurRepository.deleteUtilisateurInactif()
        }

        logManager.info(
            "[TASK_SYNCHRO_UTILISATEUR] Synchronisation terminée : " +
                "$nbUtilisateurAdd utilisateur(s) ajouté(s), " +
                "$nbUtilisateurUpdate utilisateur(s) mis à jour et " +
                "$nbUtilisateurSuppress utilisateurs supprimés",
        )

        keycloakToken.revokeToken(
            response.accessToken,
            keycloakClient.clientId,
            keycloakClient.clientSecret,
        ).execute()
        return null
    }

    override fun checkParameters(parameters: SynchroUtilisateurTaskParameters?) {
        // Par défaut le champ "canSuppress" est false
    }

    override fun getType(): TypeTask =
        TypeTask.SYNCHRO_UTILISATEUR

    override fun getTaskParametersClass(): Class<SynchroUtilisateurTaskParameters> {
        return SynchroUtilisateurTaskParameters::class.java
    }

    override fun notifySpecific(executionResults: SchedulableTaskResults?, notificationRaw: NotificationRaw) {
        // TODO: Pas de notification pour le moment
    }
}

data class SynchroUtilisateurTaskParameters(
    override val notification: NotificationMailData?,
    val canSuppressUser: Boolean = false,
) : SchedulableTaskParameters(notification)
