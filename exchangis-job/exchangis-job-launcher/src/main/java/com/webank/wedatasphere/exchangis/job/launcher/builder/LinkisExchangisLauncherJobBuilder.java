package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;

/**
 * Launcher job builder
 */

public class LinkisExchangisLauncherJobBuilder extends AbstractExchangisJobBuilder<ExchangisEngineJob, LaunchableExchangisTask> {

    private static final String LAUNCHER_NAME = "Linkis";
    @Override
    public LaunchableExchangisTask buildJob(ExchangisEngineJob inputJob, LaunchableExchangisTask expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        LaunchableExchangisTask launchableTask = new LaunchableExchangisTask();
        launchableTask.setName(inputJob.getName());
        launchableTask.setJobId(inputJob.getId());
        launchableTask.setExecuteUser(inputJob.getCreateUser());
//        launcherJob.setExecuteNode(exchangisJob.getExecuteNode());
        launchableTask.setLinkisContentMap(inputJob.getJobContent());
        launchableTask.setLinkisParamsMap(inputJob.getRuntimeParams());
        launchableTask.setEngineType(inputJob.getEngineType());
        launchableTask.setLabels(inputJob.getJobLabel());
        launchableTask.setName(inputJob.getName());
        // Use launcher name placeholder
        launchableTask.setLinkisJobName(LAUNCHER_NAME);
        return launchableTask;
    }
}
