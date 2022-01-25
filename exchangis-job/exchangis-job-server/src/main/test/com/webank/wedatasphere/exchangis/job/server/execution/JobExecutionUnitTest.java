package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.manager.LinkisExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.server.builder.JobBuilderMainProgress;
import com.webank.wedatasphere.exchangis.job.server.builder.SpringExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.DefaultTaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.DefaultTaskGeneratorContext;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.FlexibleTenancyLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.*;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.GenerationSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.MaxUsageTaskChooseRuler;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.NewInTaskObserver;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;
import com.webank.wedatasphere.exchangis.job.server.log.DefaultRpcJobLogger;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.executer.ExecutorManager;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Unit test of execution module
 */
public class JobExecutionUnitTest {
//    private static final Logger LOG = LoggerFactory.getLogger(JobExecutionUnitTest.class);

    public static void main(String[] args) throws ExchangisTaskExecuteException {
//        System.setProperty("log4j.configurationFile", "C:\\Users\\davidhua\\IdeaProjects\\Exchangis\\assembly-package\\config\\log4j2.xml");
        System.setProperty("log4j.configurationFile", "C:\\Users\\hadoop\\IdeaProjects\\Exchangis\\assembly-package\\config\\log4j2.xml");
        System.setProperty("wds.exchangis.job.scheduler.consumer.tenancies", "hadoop");
        final Logger LOG = LoggerFactory.getLogger(JobExecutionUnitTest.class);
        LOG.info("Job Execution Unit Test begin to launch");
        // Logger
        DefaultRpcJobLogger jobLogger = new DefaultRpcJobLogger();
        jobLogger.onEvent(new JobLogEvent(UUID.randomUUID().toString(), "That is just a test"));
        // Start an endless thread to hold the running of program
        new Thread(new EndlessThread()).start();
        try {
//            JobLogListener logListener = getLogListener();
            // Task Generator
            SpringExchangisJobBuilderManager jobBuilderManager = new SpringExchangisJobBuilderManager();
            jobBuilderManager.init();
            TaskGenerator<LaunchableExchangisJob> taskGenerator = new DefaultTaskGenerator(new DefaultTaskGeneratorContext(jobLogger), jobBuilderManager);
            // Executor manager
            ExecutorManager executorManager = new ExchangisSchedulerExecutorManager();
            // Tenancy consumer manager
            TenancyParallelConsumerManager consumerManager = new TenancyParallelConsumerManager();
            consumerManager.setInitResidentThreads(4);
            // Task manager
            TaskManager<LaunchedExchangisTask> taskManager = new DefaultTaskManager(jobLogger);
            Scheduler scheduler = new ExchangisGenericScheduler(executorManager, consumerManager);
            scheduler.init();
            // Load balancer
            FlexibleTenancyLoadBalancer loadBalancer = new FlexibleTenancyLoadBalancer(scheduler, taskManager);
            // Task observers
            List<TaskObserver<?>> observers = new ArrayList<>();
            NewInTaskObserver newInObserver = new NewInTaskObserver();
            observers.add(newInObserver);
            // Launcher manager
            LinkisExchangisTaskLaunchManager launchManager = new LinkisExchangisTaskLaunchManager();
            launchManager.init();
            // Task execution
            TaskExecution<LaunchableExchangisTask> execution =
                    new DefaultTaskExecution(scheduler, launchManager, taskManager, observers, loadBalancer, new MaxUsageTaskChooseRuler());
            execution.start();
            // Test submit
            execution.submit(getTestUnitTask("hadoop"));
            // Submit LaunchableExchangisTask
            submitTest(execution, newInObserver);
            // Generate
            generateTest(execution, taskGenerator, "davidhua");
        } catch (Exception e){
            LOG.error("Job Execution Unit Test shutdown", e);
        }
    }

    private static void generateTest(TaskExecution<LaunchableExchangisTask> execution,
                                     TaskGenerator<LaunchableExchangisJob> taskGenerator,
                                     String tenancy) throws ExchangisSchedulerException, ExchangisTaskGenerateException {
        GenerationSchedulerTask task = new GenerationSchedulerTask(taskGenerator, JobBuilderMainProgress.getDemoSqoopJobInfo());
        task.setTenancy(tenancy);
        execution.submit(task);
    }
    private static void submitTest(TaskExecution<LaunchableExchangisTask> execution, NewInTaskObserver newInTaskObserver){
        LaunchableExchangisTask task = new LaunchableExchangisTask();
        task.setId(1694451505815490560L);
        task.setJobExecutionId(UUID.randomUUID().toString());
        newInTaskObserver.getCacheQueue().offer(task);
    }

    private static JobLogListener getLogListener(){
        return new JobLogListener() {
            @Override
            public void onEvent(JobLogEvent event) throws ExchangisOnEventException {

            }
        };
    }

    private static AbstractExchangisSchedulerTask getTestUnitTask(String user){
        return new AbstractExchangisSchedulerTask("execution-test") {
            final Logger LOG = LoggerFactory.getLogger(this.getClass());
            @Override
            public String getName() {
                return null;
            }

            @Override
            public JobInfo getJobInfo() {
                return null;
            }

            @Override
            public String getTenancy() {
                return user;
            }

            @Override
            protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
                LOG.info("id: [{}] schedule success", getId());
            }
        };
    }
    private static class EndlessThread implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    //Ignore
                }
            }
        }
    }
}
