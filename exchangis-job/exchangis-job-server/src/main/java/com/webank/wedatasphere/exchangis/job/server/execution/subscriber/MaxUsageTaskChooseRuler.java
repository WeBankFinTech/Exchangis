package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelConsumerManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelGroupFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Max usage of queue in executor service
 */
public class MaxUsageTaskChooseRuler implements TaskChooseRuler<LaunchableExchangisTask>{

    @Override
    public List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidates, Scheduler scheduler) {
        ConsumerManager consumerManager = scheduler.getSchedulerContext().getOrCreateConsumerManager();
        if (consumerManager instanceof TenancyParallelConsumerManager){
            TenancyParallelConsumerManager tenancyConsumerManager = (TenancyParallelConsumerManager)consumerManager;
            Map<String, ExecutorService> tenancyExecutorServices = tenancyConsumerManager.getTenancyExecutorServices();
            Map<String, AtomicInteger> candidateCounter = new HashMap<>();
            return candidates.stream().filter( task -> {
                String tenancy = StringUtils.isNotBlank(task.getExecuteUser())?
                        task.getExecuteUser(): TenancyParallelGroupFactory.DEFAULT_TENANCY;
                ExecutorService executorService = tenancyExecutorServices.get(tenancy);
                AtomicInteger counter = candidateCounter.computeIfAbsent(tenancy, (key) -> new AtomicInteger(0));
                // TODO complete the choose rule
                return Objects.isNull(executorService) || ((ThreadPoolExecutor)executorService).getQueue().remainingCapacity() >= counter.incrementAndGet();
            }).collect(Collectors.toList());
        }
        return candidates;
    }
}
