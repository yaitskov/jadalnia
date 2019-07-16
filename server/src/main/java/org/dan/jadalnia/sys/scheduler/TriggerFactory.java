package org.dan.jadalnia.sys.scheduler;

import java.text.ParseException;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface TriggerFactory {
    void setName(String name);
    void setGroup(String group);
    void setJobDetail(JobDetail detail);
    void afterPropertiesSet() throws ParseException;
    Trigger getObject();
}
