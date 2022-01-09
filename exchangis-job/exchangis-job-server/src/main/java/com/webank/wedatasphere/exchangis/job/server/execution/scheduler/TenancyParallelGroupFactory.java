package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.scheduler.queue.AbstractGroup;
import org.apache.linkis.scheduler.queue.SchedulerEvent;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOGroupFactory;

import java.util.regex.Pattern;

/**
 * Multi-tenancy group factory
 */
public class TenancyParallelGroupFactory extends FIFOGroupFactory {

    private static final int DEFAULT_PARALLEL_PER_TENANCY = 1;

    private int parallel = DEFAULT_PARALLEL_PER_TENANCY;

    private Pattern tenancyPattern = Pattern.compile("[\\s\\S]+");

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        if (parallel < 0)
        this.parallel = parallel;
    }

    public Pattern getTenancyPattern() {
        return tenancyPattern;
    }

    public void setTenancyPattern(Pattern tenancyPattern) {
        this.tenancyPattern = tenancyPattern;
    }

    @Override
    public AbstractGroup createGroup(String groupName) {
        return super.createGroup(groupName);
    }

    @Override
    public String getGroupNameByEvent(SchedulerEvent event) {
        return super.getGroupNameByEvent(event);
    }
}
