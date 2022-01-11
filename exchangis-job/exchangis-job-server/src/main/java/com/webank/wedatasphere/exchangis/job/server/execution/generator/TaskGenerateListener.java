package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateEvent;
import com.webank.wedatasphere.exchangis.job.server.listener.ExchangisListener;
import org.apache.linkis.common.exception.ErrorException;

/**
 * Listener of task generating
 */
public interface TaskGenerateListener extends ExchangisListener {

    /**
     * listen generating event
      * @param generateEvent event
     * @throws ErrorException
     */
    void onEvent(TaskGenerateEvent generateEvent) throws ErrorException;
}
