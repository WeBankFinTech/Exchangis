package com.webank.wedatasphere.exchangis.job.server.vo;

import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;


public class ExchangisProgressVo {
    protected TaskStatus status;

    protected Double progress = 0.0d;

    protected Boolean allTaskStatus;

    public ExchangisProgressVo(){

    }

    public ExchangisProgressVo(TaskStatus status, Double progress){
        this.status = status;
        this.progress = progress;
    }

    public ExchangisProgressVo(TaskStatus status, Double progress, Boolean allTaskStatus){
        this.status = status;
        this.progress = progress;
        this.allTaskStatus = allTaskStatus;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Boolean getAllTaskStatus() {
        return allTaskStatus;
    }

    public void setAllTaskStatus(Boolean allTaskStatus) {
        this.allTaskStatus = allTaskStatus;
    }
}
