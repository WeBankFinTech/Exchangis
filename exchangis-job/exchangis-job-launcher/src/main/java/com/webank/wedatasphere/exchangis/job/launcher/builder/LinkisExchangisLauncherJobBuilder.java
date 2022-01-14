package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Launcher job builder
 */

public class LinkisExchangisLauncherJobBuilder extends AbstractExchangisJobBuilder<ExchangisEngineJob, LaunchableExchangisTask> {

    private static final String LAUNCHER_NAME = "Linkis";
    @Override
    public LaunchableExchangisTask buildJob(ExchangisEngineJob inputJob, LaunchableExchangisTask expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        LaunchableExchangisTask launcherJob = new LaunchableExchangisTask();
        ExchangisJobInfo exchangisJob = ctx.getOriginalJob();
        launcherJob.setId(inputJob.getId());
        launcherJob.setName(inputJob.getName());
        launcherJob.setCreateUser(inputJob.getCreateUser());
//        launcherJob.setExecuteNode(exchangisJob.getExecuteNode());
//        launcherJob.setProxyUser(exchangisJob.getProxyUser());
        launcherJob.setJobContent(inputJob.getJobContent());
        launcherJob.setRuntimeMap(inputJob.getRuntimeParams());
//        launcherJob.setEngine(inputJob.getEngine());
        launcherJob.setName(inputJob.getName());
        launcherJob.setLaunchName(LAUNCHER_NAME);
        return launcherJob;
    }
}
