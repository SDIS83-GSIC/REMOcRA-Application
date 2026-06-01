package remocra.tasks

import jakarta.inject.Inject
import org.jooq.exception.IOException
import org.slf4j.LoggerFactory
import remocra.auth.AuthModule
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.data.enums.ParametreEnum
import remocra.db.OrganismeRepository
import remocra.db.ProfilUtilisateurRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.UserRepresentation
import remocra.utils.getStringOrNull
import java.util.UUID

class SynchroUtilisateurTask @Inject constructor(
    private val organismeRepository: OrganismeRepository,
    private val profilUtilisateurRepository: ProfilUtilisateurRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val keycloakApi: KeycloakApi,
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
) : SchedulableTask<SynchroUtilisateurTaskParameters, SchedulableTaskResults>() {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val MAX_RESULTS = 100
    }

    override fun execute(parameters: SynchroUtilisateurTaskParameters?, userInfo: WrappedUserInfo): SchedulableTaskResults? {
        val response = keycloakToken.getToken(
            keycloakClient.clientId,
            keycloakClient.clientSecret,
        ).execute().body()!!

        val token = "${response.tokenType} ${response.accessToken}"

        try {
            transactionManager.transactionResult {
                var offset = 0
                var nbGroupe = 1
                var fini = false

                // On désactive les utilisateurs avant la synchro
                logManager.info("[TASK_SYNCHRO_UTILISATEUR] Désactivation de tous les utilisateurs")
                utilisateurRepository.desactiveAllUsers()

                var nbUtilisateurUpdate = 0
                var nbUtilisateurAdd = 0
                var nbUtilisateurSuppress = 0

                logManager.info("[TASK_SYNCHRO_UTILISATEUR] Récupération et traitement des utilisateurs par groupes de $MAX_RESULTS")
                while (!fini) {
                    try {
                        logManager.info("[TASK_SYNCHRO_UTILISATEUR] Groupe d'utilisateurs #$nbGroupe")
                        val usersKeycloak = keycloakApi.getUsers(authorization = token, first = offset, max = MAX_RESULTS).execute()
                        if (!usersKeycloak.isSuccessful) {
                            logManager.error("[TASK_SYNCHRO_UTILISATEUR] Erreur lors de la récupération des utilisateurs de keycloak : ${usersKeycloak.errorBody()}")
                            return@transactionResult
                        }

                        if (usersKeycloak.body().isNullOrEmpty()) {
                            logManager.info("[TASK_SYNCHRO_UTILISATEUR] Groupe d'utilisateurs vide, fin du traitement des utilisateurs")
                            fini = true
                        }

                        val listeUtilisateursRemocra = utilisateurRepository.getUtilisateurByKeycloakIds(usersKeycloak.body()?.map { it.id } ?: emptyList())
                        for (userRepresentation: UserRepresentation in usersKeycloak.body()!!) {
                            // Si l'utilisateur est déjà en base
                            val utilisateurExistant = listeUtilisateursRemocra?.firstOrNull { it.utilisateurKeycloakId == userRepresentation.id }
                            val email: String = userRepresentation.email?.takeIf { it.isNotEmpty() }
                                ?: if (parameters?.accepteUserSansEmail == true) {
                                    parameters.emailParDefaut
                                        ?: throw IllegalArgumentException("L'adresse mail par défaut doit être définie")
                                } else {
                                    val errorMessage = "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur '${userRepresentation.username}' n'a pas d'adresse mail et le paramètre 'accepteUserSansEmail' est à false"
                                    logManager.error(errorMessage)
                                    throw IllegalArgumentException(errorMessage)
                                }
                            if (utilisateurExistant != null) {
                                // On met à jour les propriétés si besoin
                                if (utilisateurExistant.utilisateurEmail != userRepresentation.email ||
                                    utilisateurExistant.utilisateurNom != userRepresentation.lastName ||
                                    utilisateurExistant.utilisateurPrenom != userRepresentation.firstName
                                ) {
                                    utilisateurRepository.updateUtilisateur(
                                        idUtilisateur = utilisateurExistant.utilisateurId,
                                        nom = userRepresentation.lastName,
                                        prenom = userRepresentation.firstName,
                                        email = email,
                                        actif = userRepresentation.enabled,
                                    )
                                    nbUtilisateurUpdate++
                                    logManager.info("[TASK_SYNCHRO_UTILISATEUR] L'utilisateur ${utilisateurExistant.utilisateurUsername} a été mis à jour")
                                }
                                // Si aucune valeur n'a été modifié et que l'utilisateur est actif dans Keycloak, on le remet
                                if (userRepresentation.enabled) {
                                    utilisateurRepository.setActif(true, utilisateurExistant.utilisateurId)
                                }
                            } else {
                                try {
                                    val utilisateur = utilisateurRepository.insertUtilisateur(
                                        id = UUID.randomUUID(),
                                        email = email,
                                        prenom = userRepresentation.firstName,
                                        nom = userRepresentation.lastName,
                                        username = userRepresentation.username,
                                        actif = userRepresentation.enabled,
                                        keycloakId = userRepresentation.id,
                                        organismeId = parametresProvider.get().mapParametres.getStringOrNull(ParametreEnum.ORGANISME_DEFAUT.name)?.let {
                                            organismeRepository.getByCode(it)?.organismeId
                                        },
                                        profilUtilisateurId = parametresProvider.get().mapParametres.getStringOrNull(ParametreEnum.PROFIL_UTILISATEUR_DEFAUT.name)?.let {
                                            profilUtilisateurRepository.getByCode(it)?.profilUtilisateurId
                                        },
                                    )
                                    nbUtilisateurAdd++
                                    logManager.info("[TASK_SYNCHRO_UTILISATEUR] L'utilisateur ${utilisateur.utilisateurUsername} a été inséré")
                                } catch (e: Exception) {
                                    logger.error("[TASK_SYNCHRO_UTILISATEUR] Erreur : ", e)
                                    logManager.error("[TASK_SYNCHRO_UTILISATEUR] Erreur : impossible d'insérer l'utilisateur '${userRepresentation.username}' (présent en base avec des données incohérentes).")
                                }
                            }
                        }
                    } catch (e: IOException) {
                        logger.error("[TASK_SYNCHRO_UTILISATEUR] Erreur lors de la synchronisation des utilisateurs : ", e)
                        logManager.error("[TASK_SYNCHRO_UTILISATEUR] Erreur lors de la synchronisation des utilisateurs : ${e.message}")
                        return@transactionResult
                    }
                    offset += MAX_RESULTS
                    nbGroupe++
                }

                if (parameters?.canSuppressUser == true) {
                    logManager.info("[TASK_SYNCHRO_UTILISATEUR] Suppression des utilisateurs restés inactifs")
                    nbUtilisateurSuppress = utilisateurRepository.deleteUtilisateurInactif()
                }

                logManager.info(
                    "[TASK_SYNCHRO_UTILISATEUR] Synchronisation terminée : " +
                        "$nbUtilisateurAdd utilisateur(s) ajouté(s), " +
                        "$nbUtilisateurUpdate utilisateur(s) mis à jour et " +
                        "$nbUtilisateurSuppress utilisateurs supprimés",
                )
            }
            return null
        } finally {
            logManager.info("[TASK_SYNCHRO_UTILISATEUR] Révocation du token Keycloak")
            keycloakToken.revokeToken(
                response.accessToken,
                keycloakClient.clientId,
                keycloakClient.clientSecret,
            ).execute()
        }
    }

    override fun checkParameters(parameters: SynchroUtilisateurTaskParameters?) {
        if (parameters?.accepteUserSansEmail == true && parameters.emailParDefaut == null) {
            val errorMessage = "L'adresse mail par défaut doit être définie"
            logManager.error(errorMessage)
            throw IllegalArgumentException(errorMessage)
        }
    }

    override fun getType(): TypeTask =
        TypeTask.SYNCHRO_UTILISATEUR

    override fun getTaskParametersClass(): Class<SynchroUtilisateurTaskParameters> {
        return SynchroUtilisateurTaskParameters::class.java
    }

    override fun notifySpecific(executionResults: SchedulableTaskResults?, notificationRaw: NotificationRaw) {
        // Pas de notification pour le moment
    }
}

data class SynchroUtilisateurTaskParameters(
    override val notification: NotificationMailData?,
    val canSuppressUser: Boolean = false,
    val accepteUserSansEmail: Boolean = false,
    val emailParDefaut: String?,
) : SchedulableTaskParameters(notification)
