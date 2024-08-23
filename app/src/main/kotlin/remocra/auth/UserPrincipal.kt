package remocra.auth

import org.pac4j.core.profile.Pac4JPrincipal

class UserPrincipal(val userInfo: UserInfo) : Pac4JPrincipal(userInfo) {

    override fun getName(): String {
        return userInfo.username
    }
}
