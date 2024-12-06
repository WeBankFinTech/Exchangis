package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.common.linkis.client.config.ExchangisClientConfig;
import com.webank.wedatasphere.exchangis.common.linkis.client.config.ExchangisClientConfigBuilder;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.linkis.client.ExchangisLaunchClient;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.LinkisRetryException;
import org.apache.linkis.common.utils.DefaultRetryHandler;
import org.apache.linkis.common.utils.RetryHandler;
import org.apache.linkis.computation.client.LinkisJobBuilder$;
import org.apache.linkis.computation.client.LinkisJobClient$;
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder$;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Linkis task launcher
 */
public class LinkisExchangisTaskLauncher implements ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> {

    private final String[] STORE_INFO = new String[]{"ecmServiceInstance"};
    /**
     * Engine versions
     */
    private Map<String, String> engineVersions = new HashMap<>();

    @Override
    public String name() {
        return "Linkis";
    }

    @Override
    public void init(ExchangisTaskLaunchManager jobLaunchManager) {
        this.engineVersions.put(EngineTypeEnum.SQOOP.name().toLowerCase(), "1.4.6");
        this.engineVersions.put(EngineTypeEnum.DATAX.name().toLowerCase(), "3.0.0");
        RetryHandler retryHandler = new DefaultRetryHandler(){};
        retryHandler.addRetryException(LinkisRetryException.class);
        ExchangisClientConfigBuilder builder = (ExchangisClientConfigBuilder) ExchangisClientConfig.newBuilder().discoveryEnabled(false)
                .retryEnabled(true)
                .setRetryHandler(retryHandler);
        ExchangisClientConfig clientConfig = builder.build();
        LinkisJobBuilder$.MODULE$.setDefaultAuthToken(clientConfig.getAuthTokenValue());
        // Try to set the static method
        Class<?> clz = SimpleOnceJobBuilder$.MODULE$.getClass();
        Field field;
        boolean setField = false;
        try {
            field = clz.getDeclaredField(SimpleOnceJobBuilder$.class.getName().replace(".", "$") + "$linkisManagerClient");
            field.setAccessible(true);
            try {
                ExchangisLaunchClient client = new ExchangisLaunchClient(clientConfig);
                field.set(SimpleOnceJobBuilder$.MODULE$, client);
                Runtime.getRuntime().addShutdownHook(new Thread(client::close));
                setField = true;
            } catch (IllegalAccessException e) {
                // Ignore
            }
        } catch (NoSuchFieldException e) {
            // Ignore
        }
        LinkisJobClient$.MODULE$.config().setDefaultClientConfig(clientConfig);
    }

    @Override
    public AccessibleLauncherTask launcherTask(LaunchedExchangisTask launchedTask) throws ExchangisTaskLaunchException {
        String linkisJobId = launchedTask.getLinkisJobId();
        String execUser = launchedTask.getExecuteUser();
        Map<String, Object> jobInfoMap = launchedTask.getLinkisJobInfoMap();
        try {
            return LinkisLauncherTask.init(linkisJobId, execUser, jobInfoMap, launchedTask.getEngineType());
        }catch (Exception e){
            throw new ExchangisTaskLaunchException("Unable to build accessible launcher task from launched task: [" + launchedTask.getTaskId() + "]", e);
        }
    }

    @Override
    public LaunchedExchangisTask launch(LaunchableExchangisTask launchableTask) throws ExchangisTaskLaunchException {
        String engineType = launchableTask.getEngineType();
        if (StringUtils.isBlank(engineType)) {
            throw new ExchangisTaskLaunchException("Unsupported job execution engine: '" + launchableTask.getEngineType() + "'.", null);
        }
        LaunchedExchangisTask launchedExchangisTask =  new LaunchedExchangisTask(launchableTask);
        LinkisLauncherTask launcherTask = LinkisLauncherTask.init(launchableTask, this.engineVersions);
        launcherTask.submit();
        launchedExchangisTask.setLinkisJobId(launcherTask.getJobId());
        launchedExchangisTask.setLinkisJobInfoMap(convertJobInfoToStore(launcherTask.getJobInfo(false)));
        launchedExchangisTask.setLauncherTask(launcherTask);
        return launchedExchangisTask;
    }

    /**
     * Convert to store job information
     * @param jobInfo job info
     * @return
     */
    private Map<String, Object> convertJobInfoToStore(Map<String, Object> jobInfo){
        Map<String, Object> storeInfo = new HashMap<>();
        Optional.ofNullable(jobInfo).ifPresent( info -> {
            for (String infoKey : STORE_INFO){
                Optional.ofNullable(info.get(infoKey)).ifPresent(infoItem -> storeInfo.put(infoKey,infoItem));
            }
        });
        return storeInfo;
    }

}

