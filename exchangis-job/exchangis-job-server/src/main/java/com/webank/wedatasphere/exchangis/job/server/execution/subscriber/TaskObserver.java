package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;

import java.util.List;


/**
 * Subscribe the ExchangisTask
 * @param <T> extends ExchangisTask
 */
public interface TaskObserver<T extends ExchangisTask> extends Runnable{


    /**
     * Subscribe method
     * @param publishedTasks
     */
    void subscribe(List<T> publishedTasks);

    /**
     * Task choose ruler
     * @return ruler
     */
    TaskChooseRuler getTaskChooseRuler();

    void setTaskChooseRuler(TaskChooseRuler ruler);

    /**
     * Task scheduler
     * @return
     */
    TaskScheduler getTaskScheduler();

    void setTaskScheduler(TaskScheduler taskScheduler);
}
