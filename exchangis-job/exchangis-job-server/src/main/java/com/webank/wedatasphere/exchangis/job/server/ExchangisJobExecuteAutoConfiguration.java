package com.webank.wedatasphere.exchangis.job.server;

import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.server.builder.SpringExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.*;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerExecutorManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisTaskScheduler;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelConsumerManager;
import org.apache.linkis.scheduler.executer.ExecutorManager;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangisJobExecuteAutoConfiguration {

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ExchangisJobBuilderManager.class)
    public ExchangisJobBuilderManager jobBuilderManager(){
        return new SpringExchangisJobBuilderManager();
    }

    @Bean
    @ConditionalOnMissingBean(TaskGeneratorContext.class)
    public TaskGeneratorContext taskGeneratorContext(){
        return new DefaultTaskGeneratorContext();
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorManager.class)
    public ConsumerManager consumerManager(){
        return new TenancyParallelConsumerManager();
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorManager.class)
    public ExecutorManager executorManagerInScheduler(){
        return new ExchangisSchedulerExecutorManager();
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(TaskScheduler.class)
    public TaskScheduler taskScheduler(ExecutorManager executorManager, ConsumerManager consumerManager){
        return new ExchangisTaskScheduler(executorManager, consumerManager);
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(TaskGenerator.class)
    public AbstractTaskGenerator taskGenerator(TaskGeneratorContext taskGeneratorContext, ExchangisJobBuilderManager jobBuilderManager){
        return new DefaultAsyncTaskGenerator(taskGeneratorContext, jobBuilderManager);
    }


}
