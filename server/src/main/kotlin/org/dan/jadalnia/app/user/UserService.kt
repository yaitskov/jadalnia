package org.dan.jadalnia.app.user

import org.dan.jadalnia.app.festival.pojo.Fid
import javax.inject.Inject
import java.util.concurrent.CompletableFuture

class UserService @Inject constructor(val userDao: UserDao) {
  fun register(regRequest: UserRegRequest): CompletableFuture<UserSession> {
    return userDao.register(regRequest.festivalId, regRequest.userType,
        UserState.Approved, regRequest.name, regRequest.session)
  }

  fun listByType(fid: Fid, userType: UserType) =
      userDao.listByType(fid, userType);
}
