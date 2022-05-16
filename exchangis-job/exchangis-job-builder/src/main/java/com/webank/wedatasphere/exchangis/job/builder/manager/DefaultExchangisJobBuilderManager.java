package com.webank.wedatasphere.exchangis.job.builder.manager;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilderChain;
import com.webank.wedatasphere.exchangis.job.builder.api.GenericExchangisJobBuilderChain;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implement
 */
public class DefaultExchangisJobBuilderManager implements ExchangisJobBuilderManager{

    /**
     * Builder chains
     */
    @SuppressWarnings("rawtypes")
    private Map<BuilderDirection, ExchangisJobBuilderChain>  jobBuilderChains = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ExchangisJob, E extends ExchangisBase> E doBuild(T originJob, Class<E> expectJobClass,
                                                                       ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        return doBuild(originJob, (Class<T>)originJob.getClass(), expectJobClass, ctx);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ExchangisJob, E extends ExchangisBase> E doBuild(T originJob, Class<T> inputJobClass, Class<E> expectJobClass, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        BuilderDirection direction = new BuilderDirection(inputJobClass, expectJobClass);
        ExchangisJobBuilderChain<T, E> chain = (ExchangisJobBuilderChain<T, E>) jobBuilderChains.get(direction);
        if(Objects.nonNull(chain)) {
            return chain.build(originJob, ctx);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ExchangisJob, E extends ExchangisBase> void addJobBuilder(ExchangisJobBuilder<T, E> jobBuilder) {
        BuilderDirection direction = new BuilderDirection(jobBuilder.inputJob(), jobBuilder.outputJob());

        jobBuilderChains.compute(direction, (key, value) -> {
            if(Objects.isNull(value)){
                value = new GenericExchangisJobBuilderChain<>();
            }
            value.registerBuilder(jobBuilder);
            return value;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ExchangisJob, E extends ExchangisBase> ExchangisJobBuilderChain<T, E> getJobBuilderChain(Class<T> inputJob, Class<E> outputJob) {
        BuilderDirection direction = new BuilderDirection(inputJob, outputJob);
        return jobBuilderChains.get(direction);
    }

    @Override
    public <T extends ExchangisJob, E extends ExchangisBase> void resetJobBuilder(Class<T> inputJob, Class<E> outputJob) {
        BuilderDirection direction = new BuilderDirection(inputJob, outputJob);
        jobBuilderChains.compute(direction, (key, value) ->{
            if(Objects.nonNull(value)){
                value.clean();
            }
            return value;
        });
    }

    @Override
    public void initBuilderChains() {
        jobBuilderChains.values().forEach(ExchangisJobBuilderChain::initialize);
    }

    private static class BuilderDirection {

        private Class<?> inputJobClass;

        private Class<?> outputJobClass;

        public BuilderDirection(Class<?> inputJobClass, Class<?> outputJobClass){
            this.inputJobClass = inputJobClass;
            this.outputJobClass = outputJobClass;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuilderDirection that = (BuilderDirection) o;
            return Objects.equals(inputJobClass, that.inputJobClass) &&
                    Objects.equals(outputJobClass, that.outputJobClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inputJobClass, outputJobClass);
        }
    }

}
