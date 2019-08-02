package org.dan.jadalnia.sys.ctx;

import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorCtx {
    public static final String DEFAULT_EXECUTOR = "default-executor";

    @Bean(name = DEFAULT_EXECUTOR)
    public ExecutorService defaultExecutor() {
        return Executors.newFixedThreadPool(1);
    }
}
