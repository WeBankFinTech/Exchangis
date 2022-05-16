package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;

/**
 * Choose rule
 */
public interface TaskChooseRuler<T extends ExchangisTask> {

    /**
     * Choose the tasks from candidates with scheduler
     * @param candidates candidate task
     * @param scheduler scheduler
     * @return
     */
    List<T> choose(List<T> candidates, Scheduler scheduler);
}
