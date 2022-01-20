package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.google.gson.*;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobConfiguration;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.TaskStatus;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.computation.client.LinkisJobBuilder;
import org.apache.linkis.computation.client.LinkisJobClient;
import org.apache.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;
import org.apache.linkis.computation.client.utils.LabelKeyUtils;

import java.util.*;

public class LinkisExchangisTaskLauncher implements ExchangisTaskLauncher<LaunchableExchangisTask> {

    @Override
    public String name() {
        return "Linkis";
    }

    @Override
    public void init(ExchangisTaskLaunchManager jobLaunchManager) {
        LinkisJobClient.config().setDefaultServerUrl(ExchangisJobConfiguration.LINKIS_SERVER_URL.getValue());
    }

    @Override
    public LaunchedExchangisTask launch(LaunchableExchangisTask launchableTask) throws ExchangisJobException {

        SubmittableSimpleOnceJob onceJob;
        String engineType = launchableTask.getEngineType();
        if ("SQOOP".equalsIgnoreCase(engineType)) {
            onceJob = this.submitSqoopJob(launchableTask);
        } else if ("DATAX".equalsIgnoreCase(engineType)) {
            onceJob = this.submitDataxJob(launchableTask);
        } else {
            throw new ExchangisJobException(ExchangisJobExceptionCode.UNSUPPORTED_TASK_LAUNCH_ENGINE.getCode(), "Unsupported job execution engine: '" + launchableTask.getEngineType() + "'.");
        }
        SubmittableSimpleOnceJob finalOnceJob = onceJob;
        LaunchedExchangisTask launchedExchangisTask =  new LaunchedExchangisTask(launchableTask) {
            @Override
            public Map<String, Object> callMetricsUpdate() {
                return super.callMetricsUpdate();
            }

            @Override
            public TaskStatus callStatusUpdate() {
                return super.callStatusUpdate();
            }

            @Override
            public void kill() {
                finalOnceJob.kill();
            }
        };
        launchedExchangisTask.setLinkisJobId(finalOnceJob.getId());
        // Store linkis info (ECMServiceInstance,EngineConnType)
        launchedExchangisTask.setLinkisJobInfoMap(new HashMap<>());
        return launchedExchangisTask;
    }

    private SubmittableSimpleOnceJob submitSqoopJob(LaunchableExchangisTask launchableTask) {
        LinkisJobBuilder<SubmittableSimpleOnceJob> jobBuilder = LinkisJobClient.once().simple().builder()
                .setCreateService("Sqoop")
                .setMaxSubmitTime(300000)
                .addLabel(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), "sqoop-1.4.7")
                .addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), launchableTask.getExecuteUser() + "-sqoop")
                .addLabel(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY(), "once")
                .addStartupParam(Configuration.IS_TEST_MODE().key(), false)
                .addExecuteUser(launchableTask.getExecuteUser())
                .addJobContent("runType", "sqoop")
                .addJobContent("sqoop-params", launchableTask.getLinkisContentMap().get("sqoop-params"));
                //TODO getTaskName
//                .addSource("jobName", launcherJob.getName() + "." + launcherJob.getTaskName());

        Optional.ofNullable(launchableTask.getLinkisParamsMap()).ifPresent(rm -> rm.forEach(jobBuilder::addRuntimeParam));

        SubmittableSimpleOnceJob onceJob = jobBuilder.build();

        onceJob.submit();

        return onceJob;
    }

    private SubmittableSimpleOnceJob submitDataxJob(LaunchableExchangisTask launchableTask) {

        Map<String, Object> jobContent = launchableTask.getLinkisContentMap();

        JsonElement code = new JsonParser().parse(jobContent.get("code").toString());

        JsonObject job = new JsonObject();
        job.add("job", code);

        JsonElement content0 = code.getAsJsonObject().get("content").getAsJsonArray().get(0);
        String reader = content0.getAsJsonObject().get("reader").getAsJsonObject().get("name").getAsString();
        String writer = content0.getAsJsonObject().get("writer").getAsJsonObject().get("name").getAsString();
        Map<String, String> rwMaps = new HashMap<>();
        rwMaps.put("reader", reader);
        rwMaps.put("writer", writer);

        LinkisJobClient.once().simple().builder();

        LinkisJobBuilder<SubmittableSimpleOnceJob> jobBuilder = LinkisJobClient.once().simple().builder().setDescription(new Gson().toJson(rwMaps)).setCreateService("DataX")
                .setMaxSubmitTime(300000)
                .addLabel(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), "datax-3.0.0")
                .addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), launchableTask.getExecuteUser() + "-datax")
                .addLabel(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY(), "once")
                .addStartupParam(Configuration.IS_TEST_MODE().key(), false)
                .addExecuteUser(launchableTask.getExecuteUser())
                .addJobContent("runType", "appconn")
                .addJobContent("code", job.toString());
                //TODO getTaskName
//                .addSource("jobName", launcherJob.getName() + "." + launcherJob.getTaskName());

        Optional.ofNullable(launchableTask.getLinkisParamsMap()).ifPresent(rm -> rm.forEach(jobBuilder::addRuntimeParam));

        SubmittableSimpleOnceJob onceJob = jobBuilder.build();

        onceJob.submit();

        return onceJob;
    }
}
