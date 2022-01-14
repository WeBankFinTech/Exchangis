package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;

import java.util.List;

/**
 * Task manager
 */
public interface TaskManager<T extends ExchangisTask> {


    List<T> getRunningTasks();

    /**
     * Cancel running task
     * @param taskId task id
     */
    void cancelRunningTask(String taskId);

    /**
     * Add running task to manager
     * @param task running task
     */
    void addRunningTask(T task);

    /**
     * Remove the running task
     * @param taskId
     */
    void removeRunningTask(String taskId);

    ExchangisJobLogListener getJobLogListener();

}
