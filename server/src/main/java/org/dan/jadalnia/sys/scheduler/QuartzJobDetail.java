package org.dan.jadalnia.sys.scheduler;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jooq.tools.StringUtils.EMPTY;

import org.quartz.Job;
import org.quartz.JobDataMap;

import java.util.concurrent.TimeUnit;


public abstract class QuartzJobDetail {
    public String getCron() {
        return EMPTY;
    }

    public String getGroup() {
        return "DEFAULT";
    }

    public String getName() {
        return jobName(getJobClass());
    }

    public String getInterval() {
        return EMPTY;
    }

    public TimeUnit getIntervalUnit() {
        return SECONDS;
    }

    public JobDataMap getJobDataMap() {
        return null;
    }

    public abstract Class<? extends Job> getJobClass();

    public static String jobName(Class<? extends Job> jobClass) {
        return jobClass.getSimpleName();
    }

}
