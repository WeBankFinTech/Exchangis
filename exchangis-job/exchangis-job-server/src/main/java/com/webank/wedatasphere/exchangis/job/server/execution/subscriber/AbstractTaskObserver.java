package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;

import java.util.List;

public abstract class AbstractTaskObserver implements TaskObserver<LaunchableExchangisTask> {

    protected int publishBatch;

    public AbstractTaskObserver(int publishBatch){
        if (publishBatch <= 0){
            throw new IllegalArgumentException("Batch size of task subscribed cannot be less than(<) 0");
        }
        this.publishBatch = publishBatch;
    }


    @Override
    public void run() {
        List<LaunchableExchangisTask> publishedTasks = onPublish(publishBatch);
        choose(publishedTasks, null, null);
    }

    protected abstract List<LaunchableExchangisTask> onPublish(int batchSize);

    protected List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidateTasks,
                                                            TaskChooseRuler chooseRuler, TaskScheduler taskScheduler){
        return chooseRuler.choose(candidateTasks, taskScheduler);
    }

    @Override
    public TaskChooseRuler getTaskChooseRuler() {
        return null;
    }

    @Override
    public void setTaskChooseRuler(TaskChooseRuler ruler) {

    }

    @Override
    public TaskScheduler getTaskScheduler() {
        return null;
    }

    @Override
    public void setTaskScheduler(TaskScheduler taskScheduler) {

    }
}
