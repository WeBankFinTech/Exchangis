package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Generic implement
 * @param <T> input job
 * @param <E> output job
 */
public class GenericExchangisJobBuilderChain<T extends ExchangisJob, E extends ExchangisBase> implements ExchangisJobBuilderChain<T, E>{

    /**
     * Chain list
     */
    private List<ExchangisJobBuilder<T, E>> builderChain = new CopyOnWriteArrayList<>();

    @Override
    public boolean registerBuilder(ExchangisJobBuilder<T, E> jobBuilder) {
        //Need to have inputClass and outputClass
        if (Objects.nonNull(jobBuilder.inputJob()) && Objects.nonNull(jobBuilder.outputJob())){
            builderChain.add(jobBuilder);
            return true;
        }
        return false;
    }

    @Override
    public E build(T inputJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        final AtomicReference<E> expectJob = new AtomicReference<>(null);
        if (Objects.nonNull(inputJob)){
            for( ExchangisJobBuilder<T, E> builder : builderChain){
                if(builder.canBuild(inputJob)){
                    expectJob.set(builder.build(inputJob, expectJob.get(), ctx));
                }
            }
        }
        return expectJob.get();
    }

    @Override
    public void initialize() {
        doInnerSort();
    }

    @Override
    public void clean() {
        builderChain.clear();
    }

    /**
     * Sort method
     */
    public synchronized void doInnerSort(){
        builderChain.sort(Comparator.comparingInt(ExchangisJobBuilder::priority));
    }
}
