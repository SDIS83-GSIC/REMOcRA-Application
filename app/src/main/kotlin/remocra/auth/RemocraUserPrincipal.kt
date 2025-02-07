package remocra.auth

import java.security.Principal

interface RemocraUserPrincipal : Principal {
    override fun getName(): String = userInfo.username

    val userInfo: UserInfo
}
