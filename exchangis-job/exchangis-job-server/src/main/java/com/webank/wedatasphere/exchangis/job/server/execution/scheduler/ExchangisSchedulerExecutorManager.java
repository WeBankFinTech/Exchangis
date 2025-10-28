package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerRetryException;
import org.apache.linkis.protocol.engine.EngineState;
import org.apache.linkis.scheduler.exception.LinkisJobRetryException;
import org.apache.linkis.scheduler.executer.*;
import org.apache.linkis.scheduler.listener.ExecutorListener;
import org.apache.linkis.scheduler.queue.SchedulerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.Some;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


/**
 * Executor manager for scheduler
 */
public class ExchangisSchedulerExecutorManager extends ExecutorManager {

    private ExchangisSchedulerExecutorFactory schedulerExecutorFactory;

    public ExchangisSchedulerExecutorManager(ExchangisSchedulerExecutorFactory schedulerExecutorFactory){
        this.schedulerExecutorFactory = schedulerExecutorFactory;
    }

    public ExchangisSchedulerExecutorManager(){
        this.schedulerExecutorFactory = new DefaultExchangisSchedulerExecutorFactory();
    }
    @Override
    public void setExecutorListener(ExecutorListener engineListener) {
        // Emm, It is never be used
    }

    @Override
    public Executor createExecutor(SchedulerEvent event) {
        return schedulerExecutorFactory.getOrCreateExecutor(event);
    }

    @Override
    public Option<Executor> askExecutor(SchedulerEvent event) {
        return Some.apply(schedulerExecutorFactory.getOrCreateExecutor(event));
    }

    @Override
    public Option<Executor> askExecutor(SchedulerEvent event, Duration wait) {
        return askExecutor(event);
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
        // Do nothing
    }

    public ExchangisSchedulerExecutorFactory getSchedulerExecutorFactory() {
        return schedulerExecutorFactory;
    }

    public static class DefaultExchangisSchedulerExecutorFactory implements ExchangisSchedulerExecutorFactory{

        private static final Class<? extends Executor> DEFAULT_DIRECT_EXECUTOR = DefaultDirectExecutor.class;

        /**
         * Register executor class
         */
        private Map<String, Class<? extends Executor>> registeredExecutorClass = new ConcurrentHashMap<>();
        /**
         * Singleton instance holder
         */
        private Map<String, Executor> singletonExecutorHolder = new ConcurrentHashMap<>();
        /**
         * Default true
         */
        boolean isSingleton = true;
        @Override
        public void setIsSingleTon(boolean singleton) {
            this.isSingleton = singleton;
        }

        @Override
        public Executor getOrCreateExecutor(SchedulerEvent event) {
            String name = event.getClass().getName();
            if (isSingleton){
                return singletonExecutorHolder.computeIfAbsent(name, this::createExecutorInternal);
            }
            return createExecutorInternal(name);
        }

        private Executor createExecutorInternal(String eventName){
            Class<? extends Executor> executorClass = registeredExecutorClass
                    .getOrDefault(eventName, DEFAULT_DIRECT_EXECUTOR);
            try {
                Constructor<?> constructor = executorClass.getDeclaredConstructor();
                Executor executor = (Executor)constructor.newInstance();
                if (executor instanceof FactoryCreateExecutor){
                    ((FactoryCreateExecutor)executor).setSchedulerExecutorFactory(this);
                }
                return executor;
            } catch (NoSuchMethodException e) {
               throw new ExchangisSchedulerException.Runtime("Fail to construct the executor for" +
                       " scheduler task: [" + eventName + "], reason: has no suitable constructor", e);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
               throw new ExchangisSchedulerException.Runtime("Fail to construct the executor for" +
                       " scheduler task: [" + eventName + "], reason: authority or other error", e);
            }
        }
        /**
         * Register method
         * @param schedulerTask scheduler task
         * @param schedulerExecutor scheduler executor
         */
        public void registerTaskExecutor(Class<? extends AbstractExchangisSchedulerTask> schedulerTask,
                                         Class<? extends Executor> schedulerExecutor){
            String schedulerTaskClass = schedulerTask.getCanonicalName();
            registeredExecutorClass.putIfAbsent(schedulerTaskClass, schedulerExecutor);
        }
    }

    public static abstract class FactoryCreateExecutor implements Executor, ConcurrentTaskOperateSupport{

        /**
         * Executor factory
         */
        private ExchangisSchedulerExecutorFactory schedulerExecutorFactory;

        public ExchangisSchedulerExecutorFactory getSchedulerExecutorFactory() {
            return schedulerExecutorFactory;
        }

        public void setSchedulerExecutorFactory(ExchangisSchedulerExecutorFactory schedulerExecutorFactory) {
            this.schedulerExecutorFactory = schedulerExecutorFactory;
        }
    }

    public static class DefaultDirectExecutor extends FactoryCreateExecutor{

        private static final Logger LOG = LoggerFactory.getLogger(DefaultDirectExecutor.class);

        public DefaultDirectExecutor(){
        }

        @Override
        public long getId() {
            return 0;
        }

        private Map<String, RequestContext> reqContext = new ConcurrentHashMap<>();
        @Override
        public ExecuteResponse execute(ExecuteRequest executeRequest) {
            if (executeRequest instanceof AbstractExchangisSchedulerTask.DirectExecuteRequest){
                AbstractExchangisSchedulerTask.DirectExecuteRequest directExecuteRequest =
                        (AbstractExchangisSchedulerTask.DirectExecuteRequest) executeRequest;
                String id = directExecuteRequest.id();
                reqContext.putIfAbsent(id, new RequestContext(directExecuteRequest));
                try {
                    directExecuteRequest.directExecute();
                    return new SuccessExecuteResponse();
                } catch (ExchangisSchedulerException | ExchangisSchedulerRetryException e) {
                    return new ErrorExecuteResponse("Exception occurred in scheduling, task will fail or retry on the next time, message: ["
                            + e.getMessage() + "]", e);
                } catch (Exception e){
                    return new ErrorExecuteResponse("Unknown Exception occurred in scheduling, message: [" + e.getMessage() + "]", e);
                } finally {
                    Optional.ofNullable(reqContext.remove(id)).ifPresent(context -> context.downLatch.countDown());
                }
            }
            return new ErrorExecuteResponse("Unsupported execute request: code: [" + executeRequest.code() + "]", null);
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


        @Override
        public boolean kill(String jobId) {
//            Optional.ofNullable(reqContext.get(jobId)).ifPresent(context -> {
//                try {
//                    LOG.info("Try to kill scheduler job id: [{}] ", jobId);
//                    context.downLatch.await();
//                } catch (InterruptedException e) {
//                    //
//                }
//            });
            return true;
        }

        @Override
        public boolean killAll() {
            return true;
        }

        @Override
        public boolean pause(String jobId) {
            return false;
        }

        @Override
        public boolean pauseAll() {
            return true;
        }

        @Override
        public boolean resume(String jobId) {
            return true;
        }

        @Override
        public boolean resumeAll() {
            return true;
        }

        private class RequestContext{

            /**
             * Request
             */
            AbstractExchangisSchedulerTask.DirectExecuteRequest executeRequest;
            /**
             * Count down
             */
            CountDownLatch downLatch;

            RequestContext(AbstractExchangisSchedulerTask.DirectExecuteRequest executeRequest){
                this.executeRequest = executeRequest;
                this.downLatch = new CountDownLatch(1);
            }
        }
    }
}
