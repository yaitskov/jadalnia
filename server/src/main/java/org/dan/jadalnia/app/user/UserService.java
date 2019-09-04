package org.dan.jadalnia.app.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserService {
    UserDao userDao;

    public CompletableFuture<UserSession> register(UserRegRequest regRequest) {
        return userDao.register(regRequest.getFestivalId(), regRequest.getUserType(),
                UserState.Approved, regRequest.name, regRequest.getSession());
    }
}
