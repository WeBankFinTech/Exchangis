package com.webank.wedatasphere.exchangis.job.server.execution.parallel;

import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tenancy parallel manager
 */
public class TenancyTaskParallelManager implements TaskParallelManager{

    private static final Logger LOG = LoggerFactory.getLogger(TenancyTaskParallelManager.class);

    private static final CommonVars<Integer> PARALLEL_MAX_PER_TENANCY =
            CommonVars.apply("wds.exchangis.job.task.parallel.max-per-tenancy", 20);

    private static final CommonVars<String> PARALLEL_TENANCIES =
            CommonVars.apply("wds.exchangis.job.task.parallel.tenancies", "");

    /**
     * Parallel rules
     */
    private Map<String, TaskParallelRule> parallelRules = new ConcurrentHashMap<>();

    public TenancyTaskParallelManager(){
       String parallelTenancies = PARALLEL_TENANCIES.getValue();
       if (StringUtils.isNotBlank(parallelTenancies)){
           for (String parallelTenancy : parallelTenancies.split(",")) {
               String[] defineParts = parallelTenancy.split(":");
               if (defineParts.length >= 3) {
                   try {
                       Operation operation = Operation.valueOf(defineParts[2]);
                       int maxParallel = Integer.parseInt(defineParts[3]);
                       parallelRules.put(defineParts[0] + ":" + operation,
                               new TaskParallelRule(defineParts[0], operation, maxParallel));
                   } catch (Exception e) {
                       LOG.warn("Unknown parallel tenancy definition: [{}]",parallelTenancy, e);
                   }
               }
           }
       }
    }
    @Override
    public TaskParallelRule getOrCreateRule(String name, Operation operation) {
        return this.parallelRules.computeIfAbsent(name + ":" + operation,
                key -> new TaskParallelRule(name, operation, PARALLEL_MAX_PER_TENANCY.getValue()));
    }

}
