package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.TaskStatus;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;

import java.util.List;
import java.util.Map;

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
     * @param taskId task id
     */
    void removeRunningTask(String taskId);

    /**
     *  Refresh running task metrics
     * @param task
     */
    boolean refreshRunningTaskMetrics(T task, Map<String, Object> metricsMap);

    /**
     * Refresh running task status
     * @param task
     * @param status
     * @return
     */
    boolean refreshRunningTaskStatus(T task, TaskStatus status);
    /**
     * Get running task
     * @param taskId task id
     * @return T
     */
    T getRunningTask(String taskId);

    ExchangisJobLogListener getJobLogListener();

}
