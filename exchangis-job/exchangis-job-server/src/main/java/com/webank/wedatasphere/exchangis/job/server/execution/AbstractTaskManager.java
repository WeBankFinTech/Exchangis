package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.events.*;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCacheUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Launched task manager
 */
public abstract class AbstractTaskManager implements TaskManager<LaunchedExchangisTask> {

    /**
     * Execution listener
     */
    private TaskExecutionListener executionListener;

    /**
     * Contains the job_execution_id
     */
    private List<String> jobExecutionIds = new CopyOnWriteArrayList<>();

    /**
     * Task id => Running task
     */
    private ConcurrentHashMap<String, TaskContext> runningTasks = new ConcurrentHashMap<>();

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
        return runningTasks.values().stream().map(ctx ->
                ctx.task).collect(Collectors.toList());
    }

    @Override
    public void cancelRunningTask(String taskId) {
        TaskContext context = runningTasks.get(taskId);
        if (Objects.nonNull(context)){
            LaunchedExchangisTask task = context.task;
            context.access(() -> {
                onEvent(new TaskStatusUpdateEvent(task, TaskStatus.Cancelled));
                info(task, "Status of task: [name: {}, id: {}] change {} => {}",
                        task.getName(), task.getTaskId(), task.getStatus(), TaskStatus.Cancelled);
                JobLogCacheUtils.flush(task.getJobExecutionId(), false);
                runningTasks.remove(taskId);
            });
            JobWrapper wrapper = jobWrappers.get(task.getJobExecutionId());
            if (Objects.nonNull(wrapper)){
                wrapper.removeTask(task);
            }
        }
    }

    @Override
    public void addRunningTask(LaunchedExchangisTask task) {
        TaskStatus beforeStatus = task.getStatus();
        task.setStatus(TaskStatus.Running);
        task.setRunningTime(Calendar.getInstance().getTime());
        onEvent(new TaskLaunchEvent(task));
        if (beforeStatus != task.getStatus()) {
            info(task, "Status of task: [name: {}, id: {}] change to {}, info: [{}]", task.getName(), task.getTaskId(), task.getStatus(), "");
        }
        if (Objects.isNull(runningTasks.putIfAbsent(task.getTaskId(), new TaskContext(task)))){
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
        removeRunningTaskInner(taskId, true);
    }

    @Override
    public boolean refreshRunningTaskMetrics(LaunchedExchangisTask task, Map<String, Object> metricsMap) {
        TaskContext context = runningTasks.get(task.getTaskId());
        if (Objects.nonNull(context)) {
            refreshRunningTaskMetrics(context, metricsMap);
            return true;
        }
        return false;
    }

    @Override
    public boolean refreshRunningTaskStatus(LaunchedExchangisTask task, TaskStatus status) {
        return refreshRunningTaskStatusAndMetrics(task, status, null);
    }

    @Override
    public boolean refreshRunningTaskStatusAndMetrics(LaunchedExchangisTask task, TaskStatus status, Map<String, Object> metricsMap) {
        TaskStatus beforeStatus = task.getStatus();
        TaskContext context = runningTasks.get(task.getTaskId());
        if (Objects.nonNull(context)){
            task = context.task;
            LaunchedExchangisTask finalTask = task;
            context.access( () -> {
                if (Objects.nonNull(metricsMap)){
                    refreshRunningTaskMetrics(context, metricsMap);
                }
                if (TaskStatus.isCompleted(status)){
                    info(finalTask, "Status of task: [name: {}, id: {}] change {} => {}",
                            finalTask.getName(), finalTask.getTaskId(), beforeStatus, status);
                    onEvent(new TaskStatusUpdateEvent(finalTask, status));
                    removeRunningTaskInner(finalTask.getTaskId(), false);
                } else {
                    onEvent(new TaskStatusUpdateEvent(finalTask, status));
                    if (isTransition(finalTask, status)) {
                        info(finalTask, "Status of task: [name: {}, id: {}] change {} => {}",
                                finalTask.getName(), finalTask.getTaskId(), beforeStatus, status);
                    }
                }
                finalTask.setStatus(status);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean refreshRunningTaskProgress(LaunchedExchangisTask task, TaskProgressInfo progressInfo) {
        TaskContext context = runningTasks.get(task.getTaskId());
        if (Objects.nonNull(context)){
            task = context.task;
            LaunchedExchangisTask finalTask = task;
            context.access(() -> {
                onEvent(new TaskProgressUpdateEvent(finalTask, progressInfo));
                if (finalTask.getProgress() != progressInfo.getProgress()){
                    info(finalTask, "Progress of task: [{}] change {} => {}",
                            finalTask.getTaskId(), finalTask.getProgress(), progressInfo.getProgress());
                }
                finalTask.setProgress(progressInfo.getProgress());
            });
            return true;
        }
        return false;
    }

    @Override
    public LaunchedExchangisTask getRunningTask(String taskId) {
        TaskContext context = runningTasks.get(taskId);
        return context != null ? context.task : null;
    }

    public TaskExecutionListener getExecutionListener() {
        return executionListener;
    }

    public void setExecutionListener(TaskExecutionListener executionListener) {
        this.executionListener = executionListener;
    }

    /**
     * Refresh running task metrics
     * @param context context
     * @param metricsMap metric map
     */
    private void refreshRunningTaskMetrics(TaskContext context, Map<String, Object> metricsMap){
        LaunchedExchangisTask finalTask = context.task;
        context.access(() -> {
            if (!TaskStatus.isCompleted(finalTask.getStatus())) {
                onEvent(new TaskMetricsUpdateEvent(finalTask, metricsMap));
                finalTask.setMetrics(null);
                finalTask.setMetricsMap(metricsMap);
                trace(finalTask, "Metrics info of task: [{}]", Json.toJson(metricsMap, null));
            }
        });
    }

    /**
     * Remove inner
     * @param taskId task id
     * @param updateStatus if update status
     */
    private void removeRunningTaskInner(String taskId, boolean updateStatus){
        TaskContext context = runningTasks.get(taskId);
        if (Objects.nonNull(context)){
            LaunchedExchangisTask task = context.task;
            context.access(() -> {
                if (updateStatus) {
                    onEvent(new TaskStatusUpdateEvent(task, task.getStatus()));
                }
                runningTasks.remove(taskId);
            });
            JobWrapper wrapper = jobWrappers.get(task.getJobExecutionId());
            if (Objects.nonNull(wrapper)){
                wrapper.removeTask(task);
            }
        }
    }
    /**
     * OnEvent
     * @param event event entity
     */
    public void onEvent(TaskExecutionEvent event){
        try {
            executionListener.onEvent(event);
        } catch (Exception e) {
            throw new ExchangisTaskExecuteException.Runtime("Fail to call 'onEvent' event: [id: " + event.eventId() +", type:" + event.getClass().getSimpleName() +"]", e);
        }
    }

    private boolean isTransition(LaunchedExchangisTask task, TaskStatus status){
        if (Objects.nonNull(task)){
            return !task.getStatus().equals(status);
        }
        return false;
    }

    @Override
    public JobLogEvent getJobLogEvent(JobLogEvent.Level level, LaunchedExchangisTask task, String message, Object... args) {
        return new JobLogEvent(level, task.getExecuteUser(), task.getJobExecutionId(), message, args);
    }

    private static class TaskContext{
        /**
         * Access lock
         */
        private final ReentrantLock accessLock = new ReentrantLock();

        private final LaunchedExchangisTask task;

        public TaskContext(LaunchedExchangisTask task){
            this.task = task;
        }
        /**
         * Access the process
         * @param exec exec process
         */
        private void access(Runnable exec){
            accessLock.lock();
            try{
                exec.run();
            }finally {
                accessLock.unlock();
            }
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
                        // Flush the job log cache
                        JobLogCacheUtils.flush(jobExecutionId, true);
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
