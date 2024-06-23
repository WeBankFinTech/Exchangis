package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.parallel.TaskParallelManager;
import com.webank.wedatasphere.exchangis.job.server.execution.parallel.TaskParallelRule;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelConsumerManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelGroupFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.ConsumerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Max parallel number of tenancy in choose ruler
 */
public class MaxParallelChooseRuler extends MaxUsageTaskChooseRuler{

    private final TaskParallelManager parallelManager;

    public MaxParallelChooseRuler(TaskParallelManager parallelManager){
        this.parallelManager = parallelManager;
    }
    @Override
    public List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidates, Scheduler scheduler) {
        Map<String, AtomicInteger> candidateCounter = new HashMap<>();
        Map<String, ExecutorService> tenancyExecutorServices = new HashMap<>();
        ConsumerManager consumerManager = scheduler.getSchedulerContext().getOrCreateConsumerManager();
        if (consumerManager instanceof TenancyParallelConsumerManager){
            tenancyExecutorServices = ((TenancyParallelConsumerManager) consumerManager).getTenancyExecutorServices();
        }
        Map<String, ExecutorService> finalTenancyExecutorServices = tenancyExecutorServices;
        return candidates.stream().filter(task -> {
            String tenancy = task.getExecuteUser();
            tenancy = StringUtils.isNotBlank(tenancy)?
                    tenancy: TenancyParallelGroupFactory.DEFAULT_TENANCY;
            AtomicInteger counter = candidateCounter.compute(tenancy, (key, cnt) -> {
                if (null == cnt) {
                    cnt = new AtomicInteger(0);
                }
                cnt.incrementAndGet();
                return cnt;
            });
            TaskParallelRule rule = parallelManager.getOrCreateRule(tenancy, TaskParallelManager.Operation.SUBMIT);
            if (Objects.nonNull(rule) && rule.getRemaining() < counter.get()){
                return false;
            }
            ExecutorService executorService = finalTenancyExecutorServices.get(tenancy);
            return Objects.isNull(executorService) || ((ThreadPoolExecutor) executorService).getQueue().remainingCapacity() >= counter.get();
        }).collect(Collectors.toList());
    }
}
