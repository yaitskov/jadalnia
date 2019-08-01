package org.dan.jadalnia.sys.ctx;

import org.dan.jadalnia.sys.scheduler.LocalSchedulerFactoryProvider;
import org.dan.jadalnia.sys.scheduler.QuartzContext;
import org.dan.jadalnia.sys.scheduler.SpringJobFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Import({QuartzContext.class,
        LocalSchedulerFactoryProvider.class,
        SpringJobFactory.class})
public class CronContext {
    @Bean
    public SchedulerFactoryBean localSchedulerFactoryBean(
            LocalSchedulerFactoryProvider provider) {
        return provider.get();
    }
}
