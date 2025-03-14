package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.SchedulerThread;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;


/**
 * Subscribe the ExchangisTask
 * @param <T> extends ExchangisTask
 */
public interface TaskObserver<T extends ExchangisTask> extends SchedulerThread {

    /**
     * Observer name
     * @return name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    default String getInstance(){
        return null;
    }
    /**
     * Subscribe method
     * @param publishedTasks published tasks
     */
    int subscribe(List<T> publishedTasks) throws ExchangisTaskObserverException;

    /**
     * Discard to unsubscribe tasks
     * @param unsubscribeTasks tasks
     */
    default void discard(List<T> unsubscribeTasks) {
        // Do nothing
    }
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

    void setTaskObserverService(TaskObserverService observerService);

    TaskObserverService getTaskObserverService();

}
