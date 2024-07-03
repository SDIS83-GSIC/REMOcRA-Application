package fr.sdis83.remocra.authn

import java.util.UUID

// TODO simple rustine temporaire pour attaquer la migration de l'API ; puisqu'elle rejoint REMOcRA, on ne peut pas considérer le UserInfo, car il a déjà un sens dans l'appli web.
// On crée donc un "ApiUserInfo", et on verra comment on l'adosse à keycloak ou non (on peut aussi imaginer un provider conditionnel, quand on est dans remocra c'est User, quand c'est l'api c'est un organisme)
interface ApiUserInfo {
    fun userId(): UUID

    fun roles(): Set<ApiRole>

    fun type(): String
}
