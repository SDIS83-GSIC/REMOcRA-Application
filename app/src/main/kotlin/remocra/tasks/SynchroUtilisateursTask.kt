package remocra.tasks

import jakarta.inject.Inject
import jakarta.inject.Provider
import org.jooq.exception.IOException
import org.slf4j.LoggerFactory
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.auth.AuthModule
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.data.enums.ParametreEnum
import remocra.db.LienProfilFonctionnaliteRepository
import remocra.db.OrganismeRepository
import remocra.db.ProfilUtilisateurRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.UserRepresentation
import java.util.UUID

class SynchroUtilisateurTask @Inject constructor(
    private val organismeRepository: OrganismeRepository,
    private val profilUtilisateurRepository: ProfilUtilisateurRepository,
    private val lienProfilFonctionnaliteRepository: LienProfilFonctionnaliteRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val keycloakApi: KeycloakApi,
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
    private val parametreProvider: Provider<ParametresProvider>,
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

                val parametres = parametreProvider.get()
                val ldapFaitFoi = parametres.getParametreBoolean(GlobalConstants.ORGANISME_PROFIL_MAJ_SYNCHRO) == true

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
                            // Un utilisateur issu du LDAP/AD a un federationLink non null
                            val isLdapUser = userRepresentation.federationLink != null

                            // Si l'utilisateur LDAP n'est membre d'aucun groupe organisme/profil côté AD, il reste désactivé
                            val manqueGroupes = isLdapUser && ldapFaitFoi &&
                                (
                                    userRepresentation.organismeCode.isNullOrEmpty() ||
                                        userRepresentation.profilUtilisateurCode.isNullOrEmpty()
                                    )
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
                                // Actif seulement si enabled dans Keycloak ET membre des groupes organisme/profil
                                val actif = userRepresentation.enabled && !manqueGroupes

                                var organismeId: UUID? = utilisateurExistant.utilisateurOrganismeId
                                var profilUtilisateurId: UUID? = utilisateurExistant.utilisateurProfilUtilisateurId

                                if (isLdapUser && ldapFaitFoi) {
                                    if (manqueGroupes) {
                                        logManager.error(
                                            "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur LDAP '${userRepresentation.username}' est désactivé : profil et/ou organisme manquant(s) dans Keycloak (LDAP fait foi).",
                                        )
                                    } else {
                                        // On ne met à jour organisme/profil que si LDAP fait foi et que les deux codes sont présents
                                        val organismeFromKeycloak = organismeRepository.getByCode(userRepresentation.organismeCode!!)?.organismeId
                                        val profilUtilisateurFromKeycloak = profilUtilisateurRepository.getByCode(userRepresentation.profilUtilisateurCode!!)?.profilUtilisateurId

                                        // Si le lien profil/organisme/groupeFonctionnalité n'est pas valide, on garde les valeurs existantes et on log une erreur
                                        if (organismeFromKeycloak != null && profilUtilisateurFromKeycloak != null) {
                                            val profilOrganismeFromKeycloak = organismeRepository.getProfilOrganismeId(organismeFromKeycloak)
                                            if (profilOrganismeFromKeycloak != null && lienProfilFonctionnaliteRepository.get(profilOrganismeFromKeycloak, profilUtilisateurFromKeycloak) != null) {
                                                organismeId = organismeFromKeycloak
                                                profilUtilisateurId = profilUtilisateurFromKeycloak
                                            } else {
                                                logManager.error(
                                                    "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur LDAP '${userRepresentation.username}' n'a pas de lien valide profil/organisme (profil='${userRepresentation.profilUtilisateurCode}', organisme='${userRepresentation.organismeCode}'). Aucune mise à jour profil/organisme.",
                                                )
                                            }
                                        } else {
                                            logManager.error(
                                                "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur LDAP '${userRepresentation.username}' n'a pas de lien valide profil/organisme (profil='${userRepresentation.profilUtilisateurCode}', organisme='${userRepresentation.organismeCode}'). Aucune mise à jour profil/organisme.",
                                            )
                                        }
                                    }
                                }

                                val utilisateurDoitEtreMisAJour =
                                    utilisateurExistant.utilisateurEmail != email ||
                                        utilisateurExistant.utilisateurNom != userRepresentation.lastName ||
                                        utilisateurExistant.utilisateurPrenom != userRepresentation.firstName ||
                                        utilisateurExistant.utilisateurOrganismeId != organismeId ||
                                        utilisateurExistant.utilisateurProfilUtilisateurId != profilUtilisateurId ||
                                        utilisateurExistant.utilisateurActif != actif

                                if (utilisateurDoitEtreMisAJour) {
                                    utilisateurRepository.updateUtilisateur(
                                        idUtilisateur = utilisateurExistant.utilisateurId,
                                        nom = userRepresentation.lastName,
                                        prenom = userRepresentation.firstName,
                                        email = email,
                                        actif = actif,
                                        organismeId = organismeId,
                                        profilUtilisateurId = profilUtilisateurId,
                                    )
                                    nbUtilisateurUpdate++
                                    logManager.info(
                                        "[TASK_SYNCHRO_UTILISATEUR] L'utilisateur ${utilisateurExistant.utilisateurUsername} a été mis à jour" +
                                            if (!actif) " (désactivé : absent des groupes organisme/profil)" else "",
                                    )
                                } else {
                                    // Synchronise l'état actif même si les autres champs n'ont pas changé
                                    utilisateurRepository.setActif(actif, utilisateurExistant.utilisateurId)
                                }
                            } else {
                                try {
                                    val organismeId = if (isLdapUser && ldapFaitFoi && !userRepresentation.organismeCode.isNullOrBlank()) {
                                        // Si LDAP fait foi et qu'on a un code Keycloak, on le prioritise
                                        organismeRepository.getByCode(userRepresentation.organismeCode)?.organismeId
                                            ?: parametres.getParametreString(ParametreEnum.ORGANISME_DEFAUT.name)
                                                ?.let { defCode -> organismeRepository.getByCode(defCode)?.organismeId }
                                    } else {
                                        // Sinon on utilise la valeur par défaut
                                        parametres.getParametreString(ParametreEnum.ORGANISME_DEFAUT.name)
                                            ?.let { defCode -> organismeRepository.getByCode(defCode)?.organismeId }
                                    }

                                    val profilId = if (isLdapUser && ldapFaitFoi && !userRepresentation.profilUtilisateurCode.isNullOrBlank()) {
                                        // Si LDAP fait foi et qu'on a un code Keycloak, on le prioritise
                                        profilUtilisateurRepository.getByCode(userRepresentation.profilUtilisateurCode)?.profilUtilisateurId
                                            ?: parametres.getParametreString(ParametreEnum.PROFIL_UTILISATEUR_DEFAUT.name)
                                                ?.let { defCode -> profilUtilisateurRepository.getByCode(defCode)?.profilUtilisateurId }
                                    } else {
                                        // Sinon on utilise la valeur par défaut
                                        parametres.getParametreString(ParametreEnum.PROFIL_UTILISATEUR_DEFAUT.name)
                                            ?.let { defCode -> profilUtilisateurRepository.getByCode(defCode)?.profilUtilisateurId }
                                    }

                                    val utilisateur = utilisateurRepository.insertUtilisateur(
                                        id = UUID.randomUUID(),
                                        email = email,
                                        prenom = userRepresentation.firstName,
                                        nom = userRepresentation.lastName,
                                        username = userRepresentation.username,
                                        actif = userRepresentation.enabled && !manqueGroupes,
                                        keycloakId = userRepresentation.id,
                                        organismeId = organismeId,
                                        profilUtilisateurId = profilId,
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
