package org.dan.jadalnia.sys.scheduler;

import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

public class CronTriggerFactory
        extends CronTriggerFactoryBean
        implements TriggerFactory {
    public CronTriggerFactory(String schedule) {
        setCronExpression(schedule);
    }
}
