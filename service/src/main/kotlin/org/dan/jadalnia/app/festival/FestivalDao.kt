package org.dan.jadalnia.app.festival

import com.google.common.collect.ImmutableMap
import org.dan.jadalnia.app.festival.menu.MenuItem
import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.festival.pojo.FestivalInfo
import org.dan.jadalnia.app.festival.pojo.FestivalState
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.NewFestival
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.jooq.Tables.FESTIVAL
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.sys.error.JadEx.Companion.notFound
import java.util.Collections

import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture

class FestivalDao : AsyncDao() {
  fun create(newFestival: NewFestival): CompletableFuture<Fid> {
    return execQuery { jooq ->
      jooq.insertInto(FESTIVAL,
          FESTIVAL.STATE,
          FESTIVAL.OPENS_AT,
          FESTIVAL.MENU,
          FESTIVAL.NAME)
          .values(FestivalState.Announce,
              newFestival.opensAt,
              Collections.emptyList(),
              newFestival.name)
          .returning(FESTIVAL.FID)
          .fetchOne()
          .getFid()
    }
  }

  fun setState(fid: Fid, state: FestivalState): CompletableFuture<Void> {
    return execQuery { jooq ->
      jooq.update(FESTIVAL)
          .set(FESTIVAL.STATE, state)
          .where(FESTIVAL.FID.eq(fid))
          .execute()
    }
        .thenAccept { n ->
          if (n == 0) {
            throw internalError("state :state is not set for festival :fid",
                ImmutableMap.of("state", state, "fid", fid));
          }
        }
  }

  fun setMenu(fid: Fid, items: List<MenuItem>): CompletableFuture<Int> {
    return execQuery { jooq ->
      jooq.update(FESTIVAL)
          .set(FESTIVAL.MENU, items)
          .where(FESTIVAL.FID.eq(fid))
          .execute()
    }
  }

  fun getById(fid: Fid): CompletableFuture<FestivalInfo> {
    return execQuery { jooq ->
      ofNullable(jooq.select()
          .from(FESTIVAL)
          .where(FESTIVAL.FID.eq(fid))
          .fetchOne())
          .map { r ->
            FestivalInfo(
                fid = fid,
                name = r.get(FESTIVAL.NAME),
                state = r.get(FESTIVAL.STATE),
                menu = r.get(FESTIVAL.MENU),
                params = r.get(FESTIVAL.PARAMS) ?: FestParams(),
                opensAt = r.get(FESTIVAL.OPENS_AT))
          }
          .orElseThrow { notFound("Festival not found", "fid", fid) }
    }
  }

  fun updateParams(fid: Fid, params: FestParams)
      = execQuery { jooq ->
    jooq.update(FESTIVAL)
        .set(FESTIVAL.PARAMS, params)
        .where(FESTIVAL.FID.eq(fid))
        .execute()
  }
}
