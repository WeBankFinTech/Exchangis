package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.builder.ExchangisLauncherJob;
import org.apache.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;

public interface ExchangisJobLauncher<T> {

    String name();

    default void init(ExchangisJobLaunchManager<? extends ExchangisLauncherJob> jobLaunchManager){}

    // void taskLog(Long taskId, Long startLine, int windSize);

    SubmittableSimpleOnceJob launch(T launchTask) throws ExchangisJobException;

}
