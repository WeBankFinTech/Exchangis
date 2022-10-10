package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;

/**
 * Max parallel number of tenancy in choose ruler
 */
public class MaxParallelChooseRuler extends MaxUsageTaskChooseRuler{

    @Override
    public List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidates, Scheduler scheduler) {
        List<LaunchableExchangisTask> usageChosen = super.choose(candidates, scheduler);
        return null;
    }
}
