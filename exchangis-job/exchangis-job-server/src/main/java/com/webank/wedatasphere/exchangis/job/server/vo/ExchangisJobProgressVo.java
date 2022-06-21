package com.webank.wedatasphere.exchangis.job.server.vo;

import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;

import java.util.*;


public class ExchangisJobProgressVo extends ExchangisProgressVo{

    private Map<TaskStatus, List<ExchangisProgressVo>> tasks = new HashMap<>();

    public ExchangisJobProgressVo(TaskStatus status, Double progress) {
        super(status, progress);
    }

    public ExchangisJobProgressVo(){
        super();
    }

    /**
     * Add task progress
     * @param progressVo progress vo
     */
    public void addTaskProgress(ExchangisProgressVo progressVo){
        TaskStatus status = progressVo.getStatus();
        if (Objects.nonNull(status)){
            tasks.compute(status, (keyStatus, statusTasks) -> {
                if (Objects.isNull(statusTasks)){
                    statusTasks = new ArrayList<>();
                }
                statusTasks.add(progressVo);
                return statusTasks;
            });
        }
    }

    public Map<TaskStatus, List<ExchangisProgressVo>> getTasks() {
        return tasks;
    }

    public void setTasks(Map<TaskStatus, List<ExchangisProgressVo>> tasks) {
        this.tasks = tasks;
    }

    public static class ExchangisTaskProgressVo extends ExchangisProgressVo{

        private String taskId;

        private String name;

        public ExchangisTaskProgressVo(String taskId, String name, TaskStatus status, Double progress) {
            super(status, progress);
            this.taskId = taskId;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
    }
}
