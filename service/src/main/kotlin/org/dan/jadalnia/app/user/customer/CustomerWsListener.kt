package org.dan.jadalnia.app.user.customer

import com.fasterxml.jackson.databind.ObjectMapper

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory
import org.dan.jadalnia.app.festival.FestivalService
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.user.UserType
import org.dan.jadalnia.app.ws.PropertyUpdated
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.app.ws.WsHandlerConfigurator
import org.dan.jadalnia.app.ws.WsListener
import org.dan.jadalnia.app.ws.WsSession

import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.sys.error.Exceptions
import org.dan.jadalnia.sys.error.JadEx
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.Futures

import org.dan.jadalnia.util.Strings
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Named

import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import javax.websocket.CloseReason
import kotlin.text.Charsets.UTF_8


@ServerEndpoint(
        value = "/ws/customer",
        configurator = WsHandlerConfigurator::class)
class CustomerWsListener
@Inject constructor(
        val wsBroadcast: WsBroadcast,
        @Named(UserCacheFactory.USER_SESSIONS)
        val userSessions: AsyncCache<UserSession, UserInfo>,
        val objectMapper: ObjectMapper,
        val festivalService: FestivalService
)
    : WsListener {
    companion object {
        const val WS_CLOSE_REASON_LIMIT = 120
        val log = LoggerFactory.getLogger(CustomerWsListener::class.java)

        @JvmStatic
        fun extractExceptionMessage(e: Throwable): String {
            if (e is JadEx) {
                return e.error.message
            }
            return e.message as String
        }

        fun formatCloseReason(e: Throwable) =
                Strings.cutLongerThan(
                        extractExceptionMessage(Exceptions.rootCause(e)),
                        WS_CLOSE_REASON_LIMIT)
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
                if (userInfo.userType != UserType.Customer) {
                    throw badRequest("Just Customers are expected")
                }
                log.info("Connected customer {} of festival {}",
                        getUserSession().uid, userInfo.fid)

                val listeners = wsBroadcast.getListeners(userInfo.fid)
                val earlier = listeners.customerListeners
                        .putIfAbsent(userInfo.uid, this)
                if (earlier != null) {
                    throw badRequest("Multiple web sockets are not allowed")
                }
                log.info("Online customers {} for {}",
                        listeners.customerListeners.size,
                        userInfo.fid)

                oUserInfo = Optional.of(userInfo)

                sendFestivalStatus()
            }
        }
    }

    private fun sendFestivalStatus(): CompletableFuture<Void> {
        return oUserInfo.map(UserInfo::fid)
                .map({ fid -> festivalService.getState(fid).thenCompose(
                        { state ->
                            send(PropertyUpdated(
                                    name = FestivalService.FESTIVAL_STATE,
                                    newValue = state))
                        })
                })
                .orElseGet(Futures::voidF)
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
        oSession.ifPresent({
            session -> closeWs(session, e)
        })
    }

    private fun closeWs(session: WsSession, e: Throwable) {
        try {
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
