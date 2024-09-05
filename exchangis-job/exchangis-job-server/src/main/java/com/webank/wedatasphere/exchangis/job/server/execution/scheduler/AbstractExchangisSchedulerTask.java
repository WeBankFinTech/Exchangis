package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerRetryException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.executer.CompletedExecuteResponse;
import org.apache.linkis.scheduler.executer.ErrorExecuteResponse;
import org.apache.linkis.scheduler.executer.ExecuteRequest;
import org.apache.linkis.scheduler.queue.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Inheritable scheduler task for exchangis, different from ExchangisTask
 */
public abstract class AbstractExchangisSchedulerTask extends Job implements ExchangisSchedulerTask{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExchangisSchedulerTask.class);

    public static final int MAX_RETRY_NUM = 3;

    private static final int MAX_ERROR_DEPTH = 4;

    private int maxRetryNum = MAX_RETRY_NUM;

    /**
     * Tenancy name
     */
    private String tenancy;

    protected String scheduleId;
    /**
     * Each schedule task should has an id
     * @param scheduleId schedule id
     */
    public AbstractExchangisSchedulerTask(String scheduleId){
        this.scheduleId = scheduleId;
    }

    public AbstractExchangisSchedulerTask() {

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

    protected void setMaxRetryNum(int maxRetryNum){
        this.maxRetryNum = maxRetryNum;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String getId() {
        if (StringUtils.isNotBlank(this.scheduleId)){
            return scheduleId;
        }
        return super.getId();
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

        public String id(){
            return getId();
        }

        public void cancel(){
//            kill();
        }
    }

    @Override
    public void transitionCompleted(CompletedExecuteResponse executeCompleted) {
        super.transitionCompleted(executeCompleted);
        if (executeCompleted instanceof ErrorExecuteResponse){
            ErrorExecuteResponse response = ((ErrorExecuteResponse)executeCompleted);
//            LOG.error("Schedule Error: " + response.message(), response.t());
            LOG.error("Schedule Error: {} => Stack message: {}" + response.message(), toErrorMsg(response.t()));
        }
    }

    public String getTenancy() {
        return tenancy;
    }

    public void setTenancy(String tenancy) {
        this.tenancy = tenancy;
    }

    /**
     * Deal with error
     * @param t t
     * @return message
     */
    private String toErrorMsg(Throwable t){
        StringBuilder builder = new StringBuilder();
        int depth = 0;
        while (null != t && depth++ < MAX_ERROR_DEPTH){
            builder.append("[").append(t.getMessage()).append("]");
            t = t.getCause();
        }
        return builder.toString();
    }
}
