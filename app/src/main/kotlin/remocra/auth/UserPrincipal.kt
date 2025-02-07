package remocra.auth

import org.pac4j.core.profile.Pac4JPrincipal

class UserPrincipal(override val userInfo: UserInfo) : Pac4JPrincipal(userInfo), RemocraUserPrincipal {

    override fun getName(): String {
        return userInfo.username
    }
}
