package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.utils.TypeGenericUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public abstract class AbstractExchangisJobBuilder<T extends ExchangisJob, E extends ExchangisBase> implements ExchangisJobBuilder<T, E> {

    private static final ThreadLocal<ExchangisJobBuilderContext> contextThreadLocal = new ThreadLocal<>();

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> inputJob() {
        return (Class<T>) TypeGenericUtils.getActualTypeFormGenericClass(this.getClass(), AbstractExchangisJobBuilder.class, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<E> outputJob() {
        return (Class<E>) TypeGenericUtils.getActualTypeFormGenericClass(this.getClass(), AbstractExchangisJobBuilder.class, 1);
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBuild(T inputJob) {
        return true;
    }


    @Override
    public E build(T inputJob, E expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        ExchangisJobBuilder<?, ?> beforeJoBuilder = ctx.getCurrentBuilder();
        JobParamDefine.defaultParam.set(new JobParamSet());
        contextThreadLocal.set(ctx);
        ctx.setCurrentBuilder(this);
        try {
            return buildJob(inputJob, expectOut, ctx);
        } finally{
            ctx.setCurrentBuilder(beforeJoBuilder);
            contextThreadLocal.remove();
            JobParamDefine.defaultParam.remove();
        }
    }

    public abstract E buildJob(T inputJob, E expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException;

    /**
     * Get current job builder context
     * @return
     */
    public static ExchangisJobBuilderContext getCurrentBuilderContext(){
        return contextThreadLocal.get();
    }
}
