package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;

import java.util.List;

public class MaxUsageTaskChooseRuler implements TaskChooseRuler{
    @Override
    public List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidates, TaskScheduler taskScheduler) {
        return null;
    }
}
