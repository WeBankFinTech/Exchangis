package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateErrorEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateEvent;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisListener;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateInitEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateSuccessEvent;

/**
 * Listener of task generating
 */
public interface TaskGenerateListener extends ExchangisListener<TaskGenerateEvent> {

    @Override
    default void onEvent(TaskGenerateEvent event) throws ExchangisOnEventException {
        getLogger().trace("Event: [id: {}, type: {}] in listener [{}]", event.eventId(), event.getClass().getSimpleName(),
                this.getClass().getSimpleName());
        if (event instanceof TaskGenerateErrorEvent){
            onError((TaskGenerateErrorEvent) event);
        } else if (event instanceof TaskGenerateInitEvent){
            onInit((TaskGenerateInitEvent)event);
        } else if (event instanceof TaskGenerateSuccessEvent){
            onSuccess((TaskGenerateSuccessEvent)event);
        }
    }

    /**
     * Listen error
     * @param errorEvent error event
     */
    void onError(TaskGenerateErrorEvent errorEvent);

    /**
     * Listen init
     * @param initEvent init event
     */
    void onInit(TaskGenerateInitEvent initEvent);

    /**
     * Listen success
     * @param successEvent success event
     */
    void onSuccess(TaskGenerateSuccessEvent successEvent);
}
