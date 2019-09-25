package org.dan.jadalnia.app.user


import com.fasterxml.jackson.databind.ObjectMapper
import org.dan.jadalnia.app.auth.ctx.UserCacheFactory
import org.dan.jadalnia.app.festival.FestivalService
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.user.UserType.Kelner
import org.dan.jadalnia.app.user.UserType.Kasier
import org.dan.jadalnia.app.user.UserType.Admin
import org.dan.jadalnia.app.user.UserType.Cook
import org.dan.jadalnia.app.user.customer.CustomerWsListener.Companion.formatCloseReason
import org.dan.jadalnia.app.ws.FestivalListeners
import org.dan.jadalnia.app.ws.PropertyUpdated
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.app.ws.WsHandlerConfigurator
import org.dan.jadalnia.app.ws.WsListener
import org.dan.jadalnia.app.ws.WsSession
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.Futures
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory

import javax.websocket.OnOpen
import javax.websocket.Session
import java.util.Optional
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Named
import javax.websocket.CloseReason
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.server.ServerEndpoint

@ServerEndpoint(
        value = "/ws/user",
        configurator = WsHandlerConfigurator::class)
class VolunteerWsListener
@Inject constructor(
        private val wsBroadcast: WsBroadcast,
        @Named(UserCacheFactory.USER_SESSIONS)
        val userSessions: AsyncCache<UserSession, UserInfo>,
        @Named(FESTIVAL_CACHE)
        val festivalCache: AsyncCache<Fid, Festival>,
        private val objectMapper: ObjectMapper,
        private val festivalService: FestivalService)
    : WsListener {

    companion object {
        val validUserTypes = hashSetOf(Kelner, Kasier, Admin, Cook)
        val log = LoggerFactory.getLogger(VolunteerWsListener::class.java)
    }

    var oSession: Optional<WsSession> = Optional.empty()
    var oUserSession: Optional<UserSession> = Optional.empty()
    var oUserInfo: Optional<UserInfo> = Optional.empty()

    override fun send(message: ByteArray): CompletableFuture<Void> {
        return handleException { getSession().send(message) }
    }

    private fun getSession(): WsSession {
        return oSession.orElseThrow{
            internalError("WS connection without [$SESSION ] header")
        }
    }

    private fun getUserSession(): UserSession {
        return oUserSession.orElseThrow {
            internalError("WS connection without user session")
        }
    }

    @OnOpen
    fun onConnected(httpSession: Session) {
        oSession = Optional.of(WsSession.wrap(httpSession))
        handleException {
            oUserSession = Optional.of(
                    getSession().header(SESSION, UserSession.Companion::valueOf))

            userSessions.get(getUserSession()).thenCompose { userInfo ->
                if (!validUserTypes.contains(userInfo.userType)) {
                    throw badRequest("Only $validUserTypes are expected")
                }

                log.info("Connected {} {} of festival {}",
                        userInfo.userType, getUserSession().uid, userInfo.fid)

                val listeners = wsBroadcast.getListeners(userInfo.fid)
                registerVolunteerConnection(listeners, userInfo)
                val earlier = listeners.volunteerListeners
                        .putIfAbsent(userInfo.uid, this)
                if (earlier != null) {
                    throw badRequest("Multiple web sockets are not allowed")
                }
                log.info("Online volunteers {} for {}",
                        listeners.volunteerListeners.size,
                        userInfo.fid)

                oUserInfo = Optional.of(userInfo)

                sendFestivalStatus()
            }
        }
    }

    private fun registerVolunteerConnection(
            listeners: FestivalListeners, userInfo: UserInfo) {
        listeners.addUid(userInfo.uid, userInfo.userType)

        if (userInfo.userType == Kelner) {
            festivalCache.get(userInfo.fid).thenAccept { festival ->
                festival.freeKelners[userInfo.uid] = userInfo.uid
            }
        }
    }

    private fun sendFestivalStatus(): CompletableFuture<Void> {
        return oUserInfo.map(UserInfo::fid).map { fid ->
            festivalService.getState(fid).thenCompose { state ->
                send(PropertyUpdated(
                        name = FestivalService.FESTIVAL_STATE,
                        newValue = state))
            }
        }.orElseGet(Futures.Companion::voidF)
    }

    fun <T> send(msgToClient: T): CompletableFuture<Void> {
        log.info("Send [{}] to ws client {}",
                msgToClient, oUserInfo.map(UserInfo::uid))
        return send(objectMapper.writeValueAsBytes(msgToClient))
    }

    @OnMessage
    fun onMessage(message: String) {
        log.info("WS message [{}] from {} in festival {}", message,
                oUserInfo.map(UserInfo::uid),
                oUserInfo.map(UserInfo::fid))
    }

    @OnError
    fun onError(e: Throwable) {
        log.error("WS for uid {} failed: {}",
                oUserInfo.map(UserInfo::uid), e.message, e)
        oSession.ifPresent {
            session -> closeWs(session, e)
        }
    }

    private fun unregister() {
        oUserInfo.ifPresent { userInfo ->
            log.info("Unbind {} from WS", userInfo)
            val listeners = wsBroadcast.getListeners(userInfo.fid)

            listeners.removeUid(userInfo.uid, userInfo.userType)

            if (userInfo.userType == Kelner) {
                festivalCache.get(userInfo.fid).thenAccept { festival ->
                    festival.freeKelners.remove(userInfo.uid)
                }
            }
        }
    }

    private fun closeWs(session: WsSession, e: Throwable) {
        try {
            unregister()
            log.info("Close WS of {}", oUserInfo)
            session.session.close(
                    CloseReason(
                            CloseReason.CloseCodes.VIOLATED_POLICY,
                            formatCloseReason(e)))
            oSession = Optional.empty()
        } catch (eee: Exception) {
            log.error("Failed to close WS {} with message {}",
                    oUserInfo, e.message, eee)
        }
    }

    fun <T> handleException(
            futureFactory: () -> CompletableFuture<T>)
            : CompletableFuture<T> {
        try {
            return futureFactory().exceptionally { e ->
                onError(e)
                null
            }
        } catch (e: Throwable) {
            onError(e)
            val failure = CompletableFuture<T>()
            failure.completeExceptionally(e)
            return failure
        }
    }
}
