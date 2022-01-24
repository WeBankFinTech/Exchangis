package com.webank.wedatasphere.exchangis.job.server;

import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.manager.LinkisExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.server.builder.SpringExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.server.execution.*;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.*;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.AbstractTaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.FlexibleTenancyLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerExecutorManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisGenericScheduler;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelConsumerManager;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskChooseRuler;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;
import com.webank.wedatasphere.exchangis.job.server.log.DefaultRpcJobLogger;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.executer.ExecutorManager;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * Auto configure the beans in job execution
 */
@Configuration
public class ExchangisJobExecuteAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JobLogListener.class)
    public JobLogListener logListener(){
        return new DefaultRpcJobLogger();
    }

    /**
     * Job builder manager
     * @return builder manager
     */
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ExchangisJobBuilderManager.class)
    public ExchangisJobBuilderManager jobBuilderManager(){
        return new SpringExchangisJobBuilderManager();
    }

    @Bean
    @ConditionalOnMissingBean(TaskGeneratorContext.class)
    public TaskGeneratorContext taskGeneratorContext(JobLogListener jobLogListener){
        return new DefaultTaskGeneratorContext(jobLogListener);
    }

    /**
     * Task generator
     * @param taskGeneratorContext generator context
     * @param jobBuilderManager job builder manager
     * @return generator
     */
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(TaskGenerator.class)
    public AbstractTaskGenerator taskGenerator(TaskGeneratorContext taskGeneratorContext,
                                               ExchangisJobBuilderManager jobBuilderManager, List<TaskGenerateListener> generateListeners){
        AbstractTaskGenerator taskGenerator = new DefaultTaskGenerator(taskGeneratorContext, jobBuilderManager);
        Optional.ofNullable(generateListeners).ifPresent(listeners -> listeners.forEach(taskGenerator::addListener));
        return taskGenerator;
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorManager.class)
    public ExecutorManager executorManagerInScheduler(){
        return new ExchangisSchedulerExecutorManager();
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerManager.class)
    public ConsumerManager consumerManager(){
        TenancyParallelConsumerManager consumerManager = new TenancyParallelConsumerManager();
        consumerManager.setInitResidentThreads(4);
        return consumerManager;
    }

    /**
     * Task manager
     * @param jobLogListener log listener
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(TaskManager.class)
    public AbstractTaskManager taskManager(JobLogListener jobLogListener){
        return new DefaultTaskManager(jobLogListener);
    }

    /**
     * Task scheduler
     * @param executorManager executor manager
     * @param consumerManager consumer manage
     * @return scheduler
     */
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(Scheduler.class)
    public Scheduler scheduler(ExecutorManager executorManager, ConsumerManager consumerManager){
        return new ExchangisGenericScheduler(executorManager, consumerManager);
    }

    /**
     * Flexible tenancy load balancer
     * @param scheduler scheduler
     * @param taskManager task manager
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(TaskSchedulerLoadBalancer.class)
    public AbstractTaskSchedulerLoadBalancer taskSchedulerLoadBalancer(Scheduler scheduler,
                                                                       TaskManager<LaunchedExchangisTask> taskManager){
        return new FlexibleTenancyLoadBalancer(scheduler, taskManager);
    }

    /**
     * Task launch manager
     * @return
     */
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ExchangisTaskLaunchManager.class)
    public ExchangisTaskLaunchManager taskLaunchManager(){
        return new LinkisExchangisTaskLaunchManager();
    }

    /**
     * Task execution
     * @param scheduler scheduler
     * @param launchManager launch manager
     * @param taskManager task manager
     * @param observers observers
     * @param loadBalancer load balancer
     * @param taskChooseRuler ruler
     * @return task execution
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(TaskExecution.class)
    public AbstractTaskExecution taskExecution(Scheduler scheduler, ExchangisTaskLaunchManager launchManager,
                                               TaskManager<LaunchedExchangisTask> taskManager, List<TaskObserver<?>> observers,
                                               TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer,
                                               TaskChooseRuler<LaunchableExchangisTask> taskChooseRuler, List<TaskExecutionListener> executionListeners){
        AbstractTaskExecution taskExecution = new DefaultTaskExecution(scheduler, launchManager, taskManager, observers, loadBalancer, taskChooseRuler);
        Optional.ofNullable(executionListeners).ifPresent(listeners -> listeners.forEach(taskExecution::addListener));
        return taskExecution;
    }
}
