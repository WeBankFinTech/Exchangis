package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import org.apache.linkis.protocol.engine.EngineState;
import org.apache.linkis.scheduler.executer.*;
import org.apache.linkis.scheduler.listener.ExecutorListener;
import org.apache.linkis.scheduler.queue.SchedulerEvent;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.io.IOException;


/**
 * Executor manager for scheduler
 */
public class ExchangisSchedulerExecutorManager extends ExecutorManager {


    @Override
    public void setExecutorListener(ExecutorListener engineListener) {
        // Emm, It is never be used
    }

    @Override
    public Executor createExecutor(SchedulerEvent event) {
        return null;
    }

    @Override
    public Option<Executor> askExecutor(SchedulerEvent event) {
        return null;
    }

    @Override
    public Option<Executor> askExecutor(SchedulerEvent event, Duration wait) {
        return null;
    }

    @Override
    public Option<Executor> getById(long id) {
        return null;
    }

    @Override
    public Executor[] getByGroup(String groupName) {
        return new Executor[0];
    }

    @Override
    public void delete(Executor executor) {

    }

    @Override
    public void shutdown() {

    }

    /**
     * Mark the executor is singleton
     */
    public interface SingletonExecutor extends Executor{

    }
    private static class DefaultDirectExecutor implements SingletonExecutor{

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public ExecuteResponse execute(ExecuteRequest executeRequest) {
            if (executeRequest instanceof ExchangisSchedulerTask.DirectExecuteRequest){
                try {
                    ((ExchangisSchedulerTask.DirectExecuteRequest)executeRequest).directExecute();
                } catch (ExchangisSchedulerException | ExchangisSchedulerRetryException e) {
                    // TODO convert to ExecuteResponse
                }
            }
            return null;
        }

        @Override
        public EngineState state() {
            return null;
        }

        @Override
        public ExecutorInfo getExecutorInfo() {
            return null;
        }

        @Override
        public void close() throws IOException {

        }
    }
}
