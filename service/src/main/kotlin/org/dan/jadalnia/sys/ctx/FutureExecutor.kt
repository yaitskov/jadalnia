package org.dan.jadalnia.sys.ctx

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ExecutorService
import java.util.function.Supplier
import javax.inject.Inject

class FutureExecutor @Inject constructor (val executorService: ExecutorService) {
     fun <T>run(supplier: () -> T): CompletableFuture<T>
             = supplyAsync(Supplier(supplier), executorService)
}