package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisListener;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskExecutionEvent;

/**
 * Execution listener
 */
public interface TaskExecutionListener extends ExchangisListener<TaskExecutionEvent> {
    /**
     * Listen event during task execution
     * @param taskExecutionEvent event
     */
    void onEvent(TaskExecutionEvent taskExecutionEvent) throws ExchangisOnEventException;
}
