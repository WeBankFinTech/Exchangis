package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherJob;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Launcher job builder
 */

public class LinkisExchangisLauncherJobBuilder extends AbstractExchangisJobBuilder<ExchangisEngineJob, ExchangisLauncherJob> {

    private static final String LAUNCHER_NAME = "Linkis";
    @Override
    public ExchangisLauncherJob buildJob(ExchangisEngineJob inputJob, ExchangisLauncherJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        ExchangisLauncherJob launcherJob = new ExchangisLauncherJob();
        ExchangisJobVO exchangisJob = ctx.getOriginalJob();
        launcherJob.setId(inputJob.getId());
        launcherJob.setTaskName(inputJob.getTaskName());
        launcherJob.setCreateUser(inputJob.getCreateUser());
        launcherJob.setExecuteNode(exchangisJob.getExecuteNode());
        launcherJob.setProxyUser(exchangisJob.getProxyUser());
        launcherJob.setJobContent(inputJob.getJobContent());
        launcherJob.setRuntimeMap(inputJob.getRuntimeParams());
        launcherJob.setEngine(inputJob.getEngine());
        launcherJob.setName(inputJob.getName());
        launcherJob.setLaunchName(LAUNCHER_NAME);
        return launcherJob;
    }
}
