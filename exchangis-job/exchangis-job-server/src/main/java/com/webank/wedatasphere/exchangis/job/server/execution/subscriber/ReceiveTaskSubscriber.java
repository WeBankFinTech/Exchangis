package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedTaskDao;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Just receive the task (not subscribe actually) '
 */
@Component
public class ReceiveTaskSubscriber extends AbstractTaskObserver<LaunchableExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(ReceiveTaskSubscriber.class);
    /**
     * Observer publish interval
     */
    private static final CommonVars<Integer> PUBLISH_INTERVAL =
            CommonVars.apply("wds.exchangis.job.task.observer.receive.publish.interval-in-mills",
                    1000 * 60 * 30);

    private static final CommonVars<Integer> EXPIRE_TIME =
            CommonVars.apply("wds.exchangis.job.task.observer.receive.expire-time-in-mills",
                    1000 * 60 * 30);
    @Resource
    private NewInTaskObserver newInTaskObserver;

    @Resource
    private LaunchedTaskDao launchedTaskDao;

    private long startupTime = -1;

    public ReceiveTaskSubscriber(){
        this.publishInterval = PUBLISH_INTERVAL.getValue();
    }

    @Override
    public String getInstance() {
        return EnvironmentUtils.getServerAddress();
    }

    @Override
    public void setTaskChooseRuler(TaskChooseRuler<LaunchableExchangisTask> ruler) {
        // Not use chooser
    }

    @Override
    protected boolean nextPublish(int publishedSize, long observerWait) {
        return publishedSize > 0;
    }

    @Override
    protected List<LaunchableExchangisTask> onPublish(String instance, int batchSize) throws ExchangisTaskObserverException {
        if (observerService == null){
            return null;
        }
        // Means the first time to publish
        if (this.lastPublishTime <= 0){
            // Get the server startup time
            startupTime = EnvironmentUtils.getStartupTime();
            LOG.info("Container startup time: {}", startupTime);
        }
        List<LaunchableExchangisTask> tasks;
        // Recover the tasks in 'Scheduled' state for a long time which scheduled by instance
        Date expireTime;
        if (startupTime > 0){
            expireTime = new Date(startupTime);
            tasks = this.observerService
                    .onPublishLaunchAbleTaskInExpire(instance, expireTime, batchSize);
            if (tasks.size() < batchSize){
                // Success to recover the tasks before startup
                startupTime = -1;
            }
        } else {
            // calculate the expire time
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, -EXPIRE_TIME.getValue());
            expireTime = calendar.getTime();
            tasks = this.observerService
                    .onPublishLaunchAbleTaskInExpire(instance, expireTime, batchSize);
        }
        if (!tasks.isEmpty()) {
            LOG.info("Publish the launch-able tasks expired with 'Scheduled' status to be launched from database, size: [{}], last_task_id: [{}], expire_time: [{}]",
                    tasks.size(), tasks.get(0).getId(), expireTime);
        }
        return tasks;
    }



    @Override
    public int subscribe(List<LaunchableExchangisTask> publishedTasks) throws ExchangisTaskObserverException {
        int count = 0;
        for (LaunchableExchangisTask task : publishedTasks){
            // Drop the related launched task in version
            if (launchedTaskDao.deleteLaunchedTaskInVersion(task.getId() + "",
                    task.getCommitVersion()) > 0){
                // Simplify the task content
                task.simplify();
                // Offer to other task observer to subscribe
                this.newInTaskObserver.getCacheQueue().offer(task);
                count ++;
            }
        }
        return count;
    }
}
