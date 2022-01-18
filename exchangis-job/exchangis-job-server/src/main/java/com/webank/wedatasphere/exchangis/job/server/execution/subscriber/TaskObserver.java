package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.SchedulerThread;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;


/**
 * Subscribe the ExchangisTask
 * @param <T> extends ExchangisTask
 */
public interface TaskObserver<T extends ExchangisTask> extends SchedulerThread {


    /**
     * Subscribe method
     * @param publishedTasks
     */
    void subscribe(List<T> publishedTasks) throws ExchangisTaskObserverException;

    /**
     * Task choose ruler
     * @return ruler
     */
    TaskChooseRuler<T> getTaskChooseRuler();

    void setTaskChooseRuler(TaskChooseRuler<T> ruler);

    /**
     * Scheduler
     * @return
     */
    Scheduler getScheduler();

    void setScheduler(Scheduler scheduler);

    /**
     * Task manager
     * @return
     */
    TaskManager<T> getTaskManager();

    void setTaskManager(TaskManager<T> taskManager);

    void setTaskExecution(TaskExecution<T> execution);

    TaskExecution<T> getTaskExecution();

}
