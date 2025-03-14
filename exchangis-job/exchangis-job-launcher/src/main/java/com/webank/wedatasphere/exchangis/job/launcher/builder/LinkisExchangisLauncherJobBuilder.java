package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.content.JobSettingsDefine;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.utils.MemUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.datasourcemanager.common.exception.JsonErrorException;
import org.apache.linkis.datasourcemanager.common.util.PatternInjectUtils;
import org.apache.linkis.datasourcemanager.common.util.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
        String engine = inputJob.getEngineType().toLowerCase(Locale.ROOT);
        LaunchableExchangisTask launchableTask = new LaunchableExchangisTask();
        launchableTask.setName(inputJob.getName());
        launchableTask.setJobId(inputJob.getId());
        // Set identify
        launchableTask.setSourceType(inputJob.getSourceType());
        launchableTask.setSinkType(inputJob.getSinkType());
        launchableTask.setSourceId(inputJob.getSourceId());
        launchableTask.setSinkId(inputJob.getSinkId());
        launchableTask.setContent(inputJob.getContent());
        launchableTask.setExecuteUser(inputJob.getCreateUser());
        // Set server address
        launchableTask.setInstance(EnvironmentUtils.getServerAddress());
        launchableTask.setLinkisContentMap(inputJob.getJobContent());
        Map<String, Object> linkisParams = new HashMap<>();
        Map<String, Object> startUpParams = new HashMap<>();
        Map<String, Object> rateParamMap = new HashMap<>();
        linkisParams.put(LAUNCHER_LINKIS_STARTUP_PARAM_NAME, startUpParams);
        try {
            String customParamPrefix = PatternInjectUtils.inject(LAUNCHER_LINKIS_CUSTOM_PARAM_PREFIX, new String[]{engine});
            // Add the runtime params to startup params for once job
            startUpParams.putAll(appendPrefixToParams(customParamPrefix, inputJob.getRuntimeParams()));
            rateParamMap.putAll(appendRateParamsToParams(inputJob.getRuntimeParams()));
        } catch (JsonErrorException e) {
            throw new ExchangisJobException(TASK_EXECUTE_ERROR.getCode(), "Fail to convert custom params for launching", e);
        }
        long memoryUsed = Optional.ofNullable(inputJob.getMemoryUsed()).orElse(0L);
        if (!inputJob.isMemoryUnitLock() && memoryUsed > 0){
            memoryUsed = MemUtils.convertToGB(inputJob.getMemoryUsed(), inputJob.getMemoryUnit());
            inputJob.setMemoryUnit("G");
        }
        startUpParams.put(LAUNCHER_LINKIS_REQUEST_MEMORY, (memoryUsed <= 0 ? 1 : memoryUsed) + inputJob.getMemoryUnit());
        List<EngineResource> resources = inputJob.getResources();
        if (!resources.isEmpty()){
            try {
                LOG.info("Use the engine resources: {} for job/task: [{}]", Json.toJson(resources, null), inputJob.getName());
                startUpParams.put(PatternInjectUtils.inject(LAUNCHER_LINKIS_RESOURCES, new String[]{engine}), Json.toJson(resources, null));
            } catch (JsonErrorException e) {
                throw new ExchangisJobException(TASK_EXECUTE_ERROR.getCode(), "Fail to use engine resources", e);
            }
        }
        launchableTask.setLinkisParamsMap(linkisParams);
        launchableTask.setRateParamsMap(rateParamMap);
        try {
            launchableTask.setRateParams(Json.toJson(inputJob.getRateParams(), null));
        } catch (JsonErrorException e) {
            LOG.error(e.getMessage());
        }
        launchableTask.setEngineType(inputJob.getEngineType());
        launchableTask.setLabels(inputJob.getJobLabel());
        launchableTask.setName(inputJob.getName());
        // Use launcher name placeholder
        launchableTask.setLinkisJobName(LAUNCHER_NAME);
        return launchableTask;
    }

    /**
     * Append prefix to params
     * @param prefix prefix
     * @param customParams custom params
     * @return params
     */
    private Map<String, Object> appendPrefixToParams(String prefix, Map<String, Object> customParams){
        return customParams.entrySet().stream().collect(Collectors.toMap(entry -> prefix + entry.getKey(),
                Map.Entry::getValue));
    }

    private Map<String, Object> appendRateParamsToParams(Map<String, Object> runtimeParams){
        Map<String, Object> rateParamMap = new HashMap<>();
        if (Objects.nonNull(runtimeParams) && !runtimeParams.isEmpty()) {
            for (String key : runtimeParams.keySet()) {
                if (StringUtils.equals(key, JobSettingsDefine.SETTING_SPEED_BYTE) ||
                        StringUtils.equals(key, JobSettingsDefine.SETTING_SPEED_RECORD) ||
                        StringUtils.equals(key, JobSettingsDefine.SETTING_SPEED_CHANNEL)) {
                    rateParamMap.put(key, Long.valueOf(String.valueOf(runtimeParams.get(key))));
                }
            }
        }
        return rateParamMap;
    }
}
