package org.dan.jadalnia.app.label

import org.dan.jadalnia.sys.ctx.FutureExecutor
import org.jooq.DSLContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

open class AsyncDao {
  @Inject
  protected var tx: PlatformTransactionManager? = null
  @Inject
  protected var jooq: DSLContext? = null
  @Inject
  protected var executor: FutureExecutor? = null

  protected fun <T> execQuery(query: (DSLContext) -> T): CompletableFuture<T> {
    val jq = jooq as DSLContext
    return (executor as FutureExecutor).run {
      val tt = TransactionTemplate(tx!!)
      tt.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED)
      tt.execute { query(jq) }
    }
  }
}
