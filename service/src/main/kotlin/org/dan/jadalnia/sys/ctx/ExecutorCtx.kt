package org.dan.jadalnia.sys.ctx;

import org.springframework.context.annotation.Bean;
import java.util.concurrent.ExecutorService

import java.util.concurrent.Executors.newCachedThreadPool
import javax.inject.Named

class ExecutorCtx {
    companion object {
        final const val DEFAULT_EXECUTOR = "default-executor"
    }

    @Bean(name = [DEFAULT_EXECUTOR])
    fun defaultExecutor() = newCachedThreadPool()

    @Bean
    fun futureExecutor(@Named(DEFAULT_EXECUTOR) executor: ExecutorService)
            =  FutureExecutor(executor)
}
