package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelConsumerManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelGroupFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
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
            int batchSize = candidates.size();
            return candidates.stream().filter( task -> {
                ExecutorService executorService = tenancyExecutorServices.get(StringUtils.isNotBlank(task.getExecuteUser())?
                        task.getExecuteUser(): TenancyParallelGroupFactory.DEFAULT_TENANCY);
                return Objects.isNull(executorService) || ((ThreadPoolExecutor)executorService).getQueue().remainingCapacity() > batchSize;
            }).collect(Collectors.toList());
        }
        return candidates;
    }
}
