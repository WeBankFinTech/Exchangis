package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.server.log.JobServerLogging;

import java.util.List;
import java.util.Map;

/**
 * Task manager
 */
public interface TaskManager<T extends ExchangisTask> extends JobServerLogging<T> {


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
     * Refresh progress
     * @param task
     * @param progressInfo
     * @return
     */
    boolean refreshRunningTaskProgress(T task, TaskProgressInfo progressInfo);
    /**
     * Get running task
     * @param taskId task id
     * @return T
     */
    T getRunningTask(String taskId);

    JobLogListener getJobLogListener();

}
