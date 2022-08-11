package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.utils.MemUtils;
import org.apache.linkis.datasourcemanager.common.exception.JsonErrorException;
import org.apache.linkis.datasourcemanager.common.util.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_EXECUTE_ERROR;
import static com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration.*;

/**
 * Launcher job builder
 */

public class LinkisExchangisLauncherJobBuilder extends AbstractExchangisJobBuilder<ExchangisEngineJob, LaunchableExchangisTask> {

    private static final String LAUNCHER_NAME = "Linkis";

    private static final Logger LOG = LoggerFactory.getLogger(LinkisExchangisLauncherJobBuilder.class);

    @Override
    public LaunchableExchangisTask buildJob(ExchangisEngineJob inputJob, LaunchableExchangisTask expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        LaunchableExchangisTask launchableTask = new LaunchableExchangisTask();
        launchableTask.setName(inputJob.getName());
        launchableTask.setJobId(inputJob.getId());
        launchableTask.setExecuteUser(inputJob.getCreateUser());
//        launcherJob.setExecuteNode(exchangisJob.getExecuteNode());
        launchableTask.setLinkisContentMap(inputJob.getJobContent());
        Map<String, Object> linkisParams = new HashMap<>();
        //        linkisParams.put(LAUNCHER_LINKIS_RUNTIME_PARAM_NAME, inputJob.getRuntimeParams());
        // Add the runtime params to startup params for once job
        Map<String, Object> startUpParams = new HashMap<>(inputJob.getRuntimeParams());
        linkisParams.put(LAUNCHER_LINKIS_STARTUP_PARAM_NAME, startUpParams);
        long memoryUsed = Objects.nonNull(inputJob.getMemoryUsed())? MemUtils.convertToGB(inputJob.getMemoryUsed(),
                inputJob.getMemoryUnit()) : 0;
        startUpParams.put(LAUNCHER_LINKIS_REQUEST_MEMORY, String.valueOf(memoryUsed <= 0 ? 1 : memoryUsed));
        List<EngineResource> resources = inputJob.getResources();
        if (!resources.isEmpty()){
            try {
                LOG.info("Use the engine resources: {} for job/task: [{}]", Json.toJson(resources, null), inputJob.getName());
                startUpParams.put(LAUNCHER_LINKIS_RESOURCES, resources);
            } catch (JsonErrorException e) {
                throw new ExchangisJobException(TASK_EXECUTE_ERROR.getCode(), "Fail to use engine resources", e);
            }
        }
        launchableTask.setLinkisParamsMap(linkisParams);
        launchableTask.setEngineType(inputJob.getEngineType());
        launchableTask.setLabels(inputJob.getJobLabel());
        launchableTask.setName(inputJob.getName());
        // Use launcher name placeholder
        launchableTask.setLinkisJobName(LAUNCHER_NAME);
        return launchableTask;
    }
}
