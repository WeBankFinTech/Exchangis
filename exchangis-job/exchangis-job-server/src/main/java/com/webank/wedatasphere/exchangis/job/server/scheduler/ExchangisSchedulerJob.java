package com.webank.wedatasphere.exchangis.job.server.scheduler;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import org.apache.linkis.scheduler.executer.ExecuteRequest;
import org.apache.linkis.scheduler.queue.Job;

import java.io.IOException;

/**
 * Inheritable scheduler job for exchangis
 */
public abstract class ExchangisSchedulerJob extends Job {

    public static final int MAX_RETRY_NUM = 3;

    private int maxRetryNum = MAX_RETRY_NUM;

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
}
