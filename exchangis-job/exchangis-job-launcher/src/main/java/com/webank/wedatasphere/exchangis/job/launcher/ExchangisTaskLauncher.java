package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.apache.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;

public interface ExchangisTaskLauncher<T extends LaunchableExchangisTask> {

    String name();

    default void init(ExchangisJobLaunchManager<? extends LaunchableExchangisTask> jobLaunchManager){}

    // void taskLog(Long taskId, Long startLine, int windSize);

    SubmittableSimpleOnceJob launch(T launchTask) throws ExchangisJobException;

}
