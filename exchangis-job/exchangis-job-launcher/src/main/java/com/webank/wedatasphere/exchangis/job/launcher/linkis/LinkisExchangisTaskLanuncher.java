package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.google.gson.*;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobConfiguration;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchableExchangisTask;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.computation.client.LinkisJobBuilder;
import org.apache.linkis.computation.client.LinkisJobClient;
import org.apache.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;
import org.apache.linkis.computation.client.utils.LabelKeyUtils;

import java.util.*;

public class LinkisExchangisTaskLanuncher implements ExchangisTaskLauncher<LaunchableExchangisTask> {

    @Override
    public String name() {
        return "Linkis";
    }

    @Override
    public void init(ExchangisJobLaunchManager<? extends LaunchableExchangisTask> jobLaunchManager) {
        LinkisJobClient.config().setDefaultServerUrl(ExchangisJobConfiguration.LINKIS_SERVER_URL.getValue());
    }

    @Override
    public SubmittableSimpleOnceJob launch(LaunchableExchangisTask launcherJob) throws ExchangisJobException {

        Date createTime = Calendar.getInstance().getTime();

        SubmittableSimpleOnceJob onceJob = null;

        if (launcherJob.getEngineType().equalsIgnoreCase("SQOOP")) {
            onceJob = this.submitSqoopJob(launcherJob);
        } else if (launcherJob.getEngineType().equalsIgnoreCase("DATAX")) {
            onceJob = this.submitDataxJob(launcherJob);
        } else {
            throw new ExchangisJobException(ExchangisJobExceptionCode.UNSUPPORTED_JOB_EXECUTION_ENGINE.getCode(), "Unsupported job execution engine: '" + launcherJob.getEngineType() + "'.");
        }
        onceJob.kill();
        return onceJob;

//        LinkisJobClient.config().setDefaultServerUrl(ExchangisJobConfiguration.LINKIS_SERVER_URL.getValue());
//        SubmittableInteractiveJob job =
//            LinkisJobClient.interactive().builder().setEngineType(launchTask.getEngineType())
//                .setRunTypeStr(launchTask.getRunType()).setCreator(launchTask.getCreator())
//                .setCode(launchTask.getCode()).addExecuteUser(launchTask.getExecuteUser()).build();
//        // 3. Submit Job to Linkis
//        job.submit();
//        // 4. Wait for Job completed
//        job.waitForCompleted();
//        // 5. Get results from iterators.
//        ResultSetIterator iterator = job.getResultSetIterables()[0].iterator();
//        System.out.println(iterator.getMetadata());
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
    }

    private SubmittableSimpleOnceJob submitSqoopJob(LaunchableExchangisTask launcherJob) {
        LinkisJobBuilder<SubmittableSimpleOnceJob> jobBuilder = LinkisJobClient.once().simple().builder()
                .setCreateService("Sqoop")
                .setMaxSubmitTime(300000)
                .addLabel(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), "sqoop-1.4.7")
                .addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), launcherJob.getProxyUser() + "-sqoop")
                .addLabel(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY(), "once")
                .addStartupParam(Configuration.IS_TEST_MODE().key(), false)
                .addExecuteUser(launcherJob.getProxyUser())
                .addJobContent("runType", "sqoop")
                .addJobContent("sqoop-params", launcherJob.getJobContent().get("sqoop-params"));
                //TODO getTaskName
//                .addSource("jobName", launcherJob.getName() + "." + launcherJob.getTaskName());

        Optional.ofNullable(launcherJob.getRuntimeMap()).ifPresent(rm -> rm.forEach(jobBuilder::addRuntimeParam));

        SubmittableSimpleOnceJob onceJob = jobBuilder.build();

        onceJob.submit();

        return onceJob;
    }

    private SubmittableSimpleOnceJob submitDataxJob(LaunchableExchangisTask launcherJob) {

        Map<String, Object> jobContent = launcherJob.getJobContent();

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
                .addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), launcherJob.getProxyUser() + "-datax")
                .addLabel(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY(), "once")
                .addStartupParam(Configuration.IS_TEST_MODE().key(), false)
                .addExecuteUser(launcherJob.getProxyUser())
                .addJobContent("runType", "appconn")
                .addJobContent("code", job.toString());
                //TODO getTaskName
//                .addSource("jobName", launcherJob.getName() + "." + launcherJob.getTaskName());

        Optional.ofNullable(launcherJob.getRuntimeMap()).ifPresent(rm -> rm.forEach(jobBuilder::addRuntimeParam));

        SubmittableSimpleOnceJob onceJob = jobBuilder.build();

        onceJob.submit();

        return onceJob;
    }
}
