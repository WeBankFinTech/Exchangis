package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLauncher;

public class LinkisExchangisJobLanuncher implements ExchangisJobLauncher<ExchangisLaunchTask> {

    @Override
    public void launch(ExchangisLaunchTask launchTask) {
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
}
