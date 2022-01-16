package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.TaskStatus;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskExecutionEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskMetricsUpdateEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskStatusUpdateEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Launched task manager
 */
public abstract class AbstractTaskManager implements TaskManager<LaunchedExchangisTask> {

    /**
     * Execution listener
     */
    private TaskExecutionListener listener;

    public AbstractTaskManager(TaskExecutionListener listener){
        this.listener = listener;
    }
    /**
     * Contains the job_execution_id
     */
    private List<String> jobExecutionIds = new CopyOnWriteArrayList<>();

    /**
     * Task id => Running task
     */
    private ConcurrentHashMap<String, LaunchedExchangisTask> runningTasks = new ConcurrentHashMap<>();

    /**
     * job_execution_id => List(Running tasks)
     */
    private ConcurrentHashMap<String, JobWrapper> jobWrappers = new ConcurrentHashMap<>();

    /**
     * Collect the job_execution_id from running tasks
     * @return list
     */
    public List<String> getJobExecutionIds(){
        return jobExecutionIds;
    }

    @Override
    public List<LaunchedExchangisTask> getRunningTasks() {
        return new ArrayList<>(runningTasks.values());
    }

    @Override
    public void cancelRunningTask(String taskId) {
        LaunchedExchangisTask task = runningTasks.get(taskId);
        if (Objects.nonNull(task)){
            onEvent(new TaskStatusUpdateEvent(task, TaskStatus.Cancelled));
            runningTasks.remove(taskId);
            JobWrapper wrapper = jobWrappers.get(task.getJobExecutionId());
            if (Objects.nonNull(wrapper)){
                wrapper.removeTask(task);
            }
        }
    }

    @Override
    public void addRunningTask(LaunchedExchangisTask task) {
        onEvent(new TaskStatusUpdateEvent(task, TaskStatus.Running));
        if (Objects.isNull(runningTasks.putIfAbsent(task.getTaskId(), task))){
            jobWrappers.compute(task.getJobExecutionId(), (jobExecutionId, jobWrapper) -> {
                if (Objects.nonNull(jobWrapper) && jobWrapper.addTask(task)){
                    return jobWrapper;
                }
                jobWrapper = new JobWrapper(jobExecutionId);
                jobWrapper.addTask(task);
                return jobWrapper;
            });
        }
    }

    @Override
    public void removeRunningTask(String taskId) {
        LaunchedExchangisTask task = runningTasks.get(taskId);
        if (Objects.nonNull(task)){
            onEvent(new TaskStatusUpdateEvent(task, task.getStatus()));
            runningTasks.remove(taskId);
            JobWrapper wrapper = jobWrappers.get(task.getJobExecutionId());
            if (Objects.nonNull(wrapper)){
                wrapper.removeTask(task);
            }
        }
    }

    @Override
    public boolean refreshRunningTaskMetrics(LaunchedExchangisTask task, Map<String, Object> metricsMap) {
        task = runningTasks.computeIfPresent(task.getTaskId(), (taskId, runningTask) ->{
            // Empty the previous value
            runningTask.setMetrics(null);
            runningTask.setMetricsMap(metricsMap);
            return runningTask;
        });
        if (Objects.nonNull(task)) {
            onEvent(new TaskMetricsUpdateEvent(task, metricsMap));
            return true;
        }
        return false;
    }

    @Override
    public boolean refreshRunningTaskStatus(LaunchedExchangisTask task, TaskStatus status) {
        if (TaskStatus.isCompleted(status)){
            onEvent(new TaskStatusUpdateEvent(task, status));
        }
        task = runningTasks.computeIfPresent(task.getTaskId(), (taskId, runningTask) -> {
            runningTask.setStatus(status);
            return runningTask;
        });
        if (Objects.nonNull(task)) {
            onEvent(new TaskStatusUpdateEvent(task, status));
            return true;
        }
        return false;
    }

    @Override
    public LaunchedExchangisTask getRunningTask(String taskId) {
        return runningTasks.get(taskId);
    }

    /**
     * OnEvent
     * @param event event entity
     */
    private void onEvent(TaskExecutionEvent event){
        try {
            listener.onEvent(event);
        } catch (Exception e) {
            throw new ExchangisTaskExecuteException.Runtime("Fail to call 'onEvent' event: [id: " + event.eventId() +", type:" + event.getClass().getSimpleName() +"]", e);
        }
    }

    private class JobWrapper{

        /**
         * job_execution_id
         */
        String jobExecutionId;

        boolean destroy = false;

        JobWrapper(String jobExecutionId){
            this.jobExecutionId = jobExecutionId;
            jobExecutionIds.add(jobExecutionId);
        }

        Map<String, LaunchedExchangisTask> tasksInJob = new HashMap<>();

        final AtomicInteger taskNum = new AtomicInteger(0);

        /**
         * Remove task (if the task list is empty, remove self from the map)
         * @param task task
         */
        public void removeTask(LaunchedExchangisTask task) {
            synchronized (taskNum) {
                if (Objects.nonNull(tasksInJob.remove(task.getTaskId()))) {
                    if (taskNum.decrementAndGet() == 0) {
                        jobWrappers.remove(jobExecutionId);
                        jobExecutionIds.remove(jobExecutionId);
                        destroy = true;
                    }
                }
            }
        }

        public boolean addTask(LaunchedExchangisTask task){
            synchronized (taskNum) {
                if (!destroy) {
                    if (Objects.isNull(tasksInJob.put(task.getTaskId(), task))) {
                        taskNum.getAndIncrement();
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
