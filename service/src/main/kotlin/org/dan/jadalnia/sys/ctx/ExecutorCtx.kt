package org.dan.jadalnia.sys.ctx;

import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors.newCachedThreadPool

class ExecutorCtx {
    companion object {
        final const val DEFAULT_EXECUTOR = "default-executor"
    }

    @Bean(name = [DEFAULT_EXECUTOR])
    fun defaultExecutor() = newCachedThreadPool()
}
