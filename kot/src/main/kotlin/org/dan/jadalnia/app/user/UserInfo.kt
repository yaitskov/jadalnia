package org.dan.jadalnia.app.user;

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.sys.error.Error
import org.dan.jadalnia.sys.error.JadEx

data class UserInfo(
        override val uid: Uid,
        override val name: String,
        val fid: Fid,
        val userType: UserType,
        val userState: UserState)
    :
        UserLinkIf {

    fun ensureAdmin(): UserInfo {
        if (userType != UserType.Admin) {
            throw JadEx(401, Error("user is not admin"))
        }
        return this
    }

    fun ensureCustomer(): UserInfo {
        if (userType != UserType.Customer) {
            throw JadEx(401, Error("user is not customer"))
        }
        return this
    }
}
