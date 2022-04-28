package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.utils.MemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration.*;

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
        Map<String, Object> linkisParams = new HashMap<>();
        Map<String, Object> startUpParams = new HashMap<>();
        linkisParams.put(LAUNCHER_LINKIS_RUNTIME_PARAM_NAME, inputJob.getRuntimeParams());
        linkisParams.put(LAUNCHER_LINKIS_STARTUP_PARAM_NAME, startUpParams);
        long memoryUsed = Objects.nonNull(inputJob.getMemoryUsed())? MemUtils.convertToGB(inputJob.getMemoryUsed(),
                inputJob.getMemoryUnit()) : 0;
        startUpParams.put(LAUNCHER_LINKIS_REQUEST_MEMORY, String.valueOf(memoryUsed <= 0 ? 1 : memoryUsed));
        launchableTask.setLinkisParamsMap(linkisParams);
        launchableTask.setEngineType(inputJob.getEngineType());
        launchableTask.setLabels(inputJob.getJobLabel());
        launchableTask.setName(inputJob.getName());
        // Use launcher name placeholder
        launchableTask.setLinkisJobName(LAUNCHER_NAME);
        return launchableTask;
    }
}
