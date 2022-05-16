package com.webank.wedatasphere.exchangis.job.launcher.domain.task;

public class TaskProgressInfo {
    /**
     * Total sub tasks
     */
    private int total;

    /**
     * Running sub tasks
     */
    private int running;

    /**
     * Failed sub tasks
     */
    private int failed;

    /**
     * Succeed sub tasks
     */
    private int succeed;

    /**
     * Progress value
     */
    private float progress;

    /**
     * Whole status
     */
    private TaskStatus status = TaskStatus.Undefined;

    public TaskProgressInfo(int total, int running, int failed, int succeed, float progress){
        this.total = total;
        this.running = running;
        this.failed = failed;
        this.succeed = succeed;
        this.progress = progress;
    }
    public TaskProgressInfo(){

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSucceed() {
        return succeed;
    }

    public void setSucceed(int succeed) {
        this.succeed = succeed;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
