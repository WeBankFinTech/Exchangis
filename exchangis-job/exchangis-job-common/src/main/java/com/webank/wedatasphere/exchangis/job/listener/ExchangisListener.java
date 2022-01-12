package com.webank.wedatasphere.exchangis.job.listener;


import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;

/**
 * Listener
 */
public interface ExchangisListener {

    /**
     * Subscribe the event
     * @param event
     */
    default void onEvent(ExchangisEvent event) throws ExchangisOnEventException {

    }
}
