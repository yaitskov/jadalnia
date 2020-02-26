package org.dan.jadalnia.app.festival

import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE

import org.dan.jadalnia.app.festival.menu.MenuItem
import org.dan.jadalnia.app.festival.pojo.CreatedFestival
import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.FestivalInfo
import org.dan.jadalnia.app.festival.pojo.FestivalState
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.NewFestival
import org.dan.jadalnia.app.user.UserDao
import org.dan.jadalnia.app.ws.PropertyUpdated
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.ctx.ExecutorCtx
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Named

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicReference

import java.util.concurrent.CompletableFuture.completedFuture

import org.dan.jadalnia.app.user.UserState.Approved
import org.dan.jadalnia.app.user.UserType.Admin

public class FestivalService @Inject constructor(
    val festivalDao: FestivalDao,
    val userDao: UserDao,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val wsBroadcast: WsBroadcast,
    @Named(ExecutorCtx.DEFAULT_EXECUTOR)
    val executorService: ExecutorService) {

  companion object {
    val FESTIVAL_STATE = "festival.state"
    val log = LoggerFactory.getLogger(FestivalService::class.java)
  }

  fun create(newFestival: NewFestival): CompletableFuture<CreatedFestival> {
    return festivalDao
        .create(newFestival)
        .thenCompose { fid ->
          log.info("Register admin for fid {}", fid)
          userDao.register(fid, Admin, Approved,
              newFestival.userName, newFestival.userKey)
              .thenApply { userSession ->
                CreatedFestival(fid = fid, session = userSession)
              }
        }
  }

  fun setState(festival: Festival, newState: FestivalState): CompletableFuture<Boolean> {
    log.info("Update festival state to {}", newState)
    val oldV = festival.info.getAndUpdate { info ->
      log.info("Update state {} => {} in {}", info.state, newState, info.fid)
      info.withState(newState)
    }
    festivalDao.setState(festival.info.get().fid, newState)
        .thenRunAsync {
          wsBroadcast.broadcast(festival.info.get().fid,
              PropertyUpdated(name = FESTIVAL_STATE, newValue = newState))
        }
    return completedFuture(oldV.state != newState)
  }

  fun getState(fid: Fid): CompletableFuture<FestivalState> {
    return festivalCache.get(fid)
        .thenApply(Festival::info)
        .thenApply(AtomicReference<FestivalInfo>::get)
        .thenApply { info ->
          log.info("Get state {} for {}", info.state, info.fid)
          info.state
        }
  }

  fun listMenu(fid: Fid): CompletableFuture<List<MenuItem>> {
    return festivalCache.get(fid)
        .thenApply { fest ->
          log.info("Load festival menu from {}", fest.hashCode())
          fest.info
        }
        .thenApply { info -> info.get() }
        .thenApply(FestivalInfo::menu)
  }

  fun updateMenu(festival: Festival, items: List<MenuItem>)
      : CompletableFuture<Int> {
    log.info("Update festival menu in {}", festival.hashCode())
    festival.info.updateAndGet { info -> info.withMenu(items) }
    return festivalDao.setMenu(festival.info.get().fid, items)
  }

  fun updateParams(fest: Festival, params: FestParams)
      : CompletableFuture<Void> {
    val fid = fest.fid()
    val info = fest.info.get()
    log.info("Update fest params {} {}/{}", fid, info.params , params)
    if (!fest.info.compareAndSet(info, info.copy(params = params))) {
      log.info("Params are same")
      return completedFuture(null)
    }
    return festivalDao.updateParams(fid, params).thenAccept { updated ->
      log.info("Fest params are updated {} in DB {}", updated, fid)
    }
  }

  fun getParams(festival: Festival) = completedFuture(festival.info.get().params)
}
