package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.common.linkis.ClientConfiguration;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.common.conf.Configuration$;
import org.apache.linkis.common.exception.LinkisRetryException;
import org.apache.linkis.common.utils.DefaultRetryHandler;
import org.apache.linkis.common.utils.RetryHandler;
import org.apache.linkis.computation.client.LinkisJobClient;
import org.apache.linkis.computation.client.LinkisJobClient$;
import org.apache.linkis.httpclient.config.ClientConfig;
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder$;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        ClientConfig clientConfig = DWSClientConfigBuilder$.MODULE$
                .newBuilder()
                .setDWSVersion(Configuration.LINKIS_WEB_VERSION().getValue())
                .addServerUrl(ClientConfiguration.LINKIS_SERVER_URL.getValue())
                .connectionTimeout(45000)
                .discoveryEnabled(false)
                .discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(false)
                .maxConnectionSize(ClientConfiguration.LINKIS_DEFAULT_MAX_CONNECTIONS.getValue())
                .retryEnabled(true)
                .setRetryHandler(retryHandler)
                .readTimeout(90000) // We think 90s is enough, if SocketTimeoutException is throw, just set a new clientConfig to modify it.
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(TokenAuthenticationStrategy.TOKEN_KEY())
                .setAuthTokenValue(ClientConfiguration.LINKIS_TOKEN_VALUE.getValue())
                .build();
        LinkisJobClient$.MODULE$.config().setDefaultClientConfig((DWSClientConfig) clientConfig);
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
