package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.executer.ExecuteRequest;
import org.apache.linkis.scheduler.queue.Job;

import java.io.IOException;

/**
 * Inheritable scheduler task for exchangis
 */
public abstract class ExchangisSchedulerJob extends Job {

    public static final int MAX_RETRY_NUM = 3;

    private int maxRetryNum = MAX_RETRY_NUM;

    /**
     * Tenancy name
     */
    private String tenancy;

    /**
     * Each schedule task should has an id
     * @param scheduleId schedule id
     */
    public ExchangisSchedulerJob(String scheduleId){
        if (StringUtils.isBlank(scheduleId)){
            throw new ExchangisSchedulerException.Runtime("The schedule_id cannot be empty in scheduler task", null);
        }
        this.setId(scheduleId);
    }
    @Override
    public void init() throws Exception {

    }

    @Override
    public ExecuteRequest jobToExecuteRequest() throws Exception {
        return new DirectExecuteRequest();
    }

    @Override
    public int getMaxRetryNum() {
        return maxRetryNum;
    }

    private void setMaxRetryNum(int maxRetryNum){
        this.maxRetryNum = maxRetryNum;
    }

    @Override
    public void close() throws IOException {
    }

    /**
     *  schedule main method
     * @throws ExchangisSchedulerException error exception
     * @throws ExchangisSchedulerRetryException retry exception
     */
    protected abstract void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException;

    public class DirectExecuteRequest implements ExecuteRequest {

        @Override
        public String code() {
            return null;
        }

        public void directExecute() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
             // Direct execute
            try {
                schedule();
            } catch (ExchangisSchedulerRetryException e){
                if (e.getRetryNum() > 0){
                    setMaxRetryNum(e.getRetryNum());
                }
                // Need to throw again
                throw e;
            }
        }
    }

    public String getTenancy() {
        return tenancy;
    }

    public void setTenancy(String tenancy) {
        this.tenancy = tenancy;
    }
}
