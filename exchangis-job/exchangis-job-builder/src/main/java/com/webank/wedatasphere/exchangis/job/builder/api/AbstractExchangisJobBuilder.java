package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.utils.TypeGenericUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public abstract class AbstractExchangisJobBuilder<T extends ExchangisJob, E extends ExchangisJob> implements ExchangisJobBuilder<T, E> {
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

}
