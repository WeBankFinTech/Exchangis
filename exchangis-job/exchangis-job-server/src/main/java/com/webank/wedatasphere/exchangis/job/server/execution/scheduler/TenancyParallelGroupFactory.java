package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.queue.AbstractGroup;
import org.apache.linkis.scheduler.queue.SchedulerEvent;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOGroup;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOGroupFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Multi-tenancy group factory
 */
public class TenancyParallelGroupFactory extends FIFOGroupFactory {

    private static final int DEFAULT_PARALLEL_PER_TENANCY = 1;

    public static final String GROUP_NAME_PREFIX = "Multi-Tenancy-Group-";

    public static final String DEFAULT_TENANCY = "default";

    private static final Pattern TENANCY_IN_GROUP_PATTERN = Pattern.compile("^" + GROUP_NAME_PREFIX + "([-_\\w\\W]+)?_\\d$");

    private int parallelPerTenancy = DEFAULT_PARALLEL_PER_TENANCY;

    private List<String> tenancies = new ArrayList<>();

    public int getParallelPerTenancy() {
        return parallelPerTenancy;
    }

    public void setParallelPerTenancy(int parallelPerTenancy) {
        if (parallelPerTenancy < 0)
        this.parallelPerTenancy = parallelPerTenancy;
    }

    public List<String> getTenancies() {
        return tenancies;
    }

    public void setTenancies(List<String> tenancies) {
        this.tenancies = tenancies;
    }

    @Override
    public AbstractGroup createGroup(String groupName) {
        // Also use the fifo group
        return new FIFOGroup(groupName, getInitCapacity(groupName), getMaxCapacity(groupName));
    }


    @Override
    public String getGroupNameByEvent(SchedulerEvent event) {
        String tenancy = "";
        if (Objects.nonNull(event) && (event instanceof AbstractExchangisSchedulerTask)){
            String tenancyInSchedule = ((AbstractExchangisSchedulerTask)event).getTenancy();
            if (tenancies.contains(tenancyInSchedule)){
                tenancy = tenancyInSchedule;
            }
        }
        return StringUtils.isNotBlank(tenancy)? GROUP_NAME_PREFIX + tenancy + "_" + (event.getId().hashCode() % parallelPerTenancy) : GROUP_NAME_PREFIX + DEFAULT_TENANCY;
    }

    public String getTenancyByGroupName(String groupName){
        String tenancy = null;
        if (StringUtils.isNotBlank(groupName)){
            Matcher matcher = TENANCY_IN_GROUP_PATTERN.matcher(groupName);
            if (matcher.find()){
                tenancy = matcher.group(1);
            }
        }
        return tenancy;
    }

}
