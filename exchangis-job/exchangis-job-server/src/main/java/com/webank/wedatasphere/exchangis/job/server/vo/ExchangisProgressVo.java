package com.webank.wedatasphere.exchangis.job.server.vo;

import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;


public class ExchangisProgressVo {
    protected TaskStatus status;

    protected Double progress = 0.0d;

    public ExchangisProgressVo(TaskStatus status, Double progress){
        this.status = status;
        this.progress = progress;
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
}
