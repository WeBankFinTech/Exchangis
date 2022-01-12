package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;

/**
 * Contains taskManager
 */
public interface ReceiveTaskObserver<T extends ExchangisTask> extends TaskObserver<T>{

}
