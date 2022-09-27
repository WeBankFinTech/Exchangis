package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;


import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.builder.SpringExchangisJobBuilderContext;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Objects;
import java.util.Optional;

/**
 * Job handler refer job builder
 */
public abstract class AbstractLoggingSubExchangisJobHandler implements SubExchangisJobHandler{

    private static final ThreadLocal<SpringExchangisJobBuilderContext> springContext = new ThreadLocal<>();

    @Override
    public final void handleSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        wrapFuncWithContext(ctx, () -> {
            try {
                handleJobSource(subExchangisJob, ctx);
            }catch (ErrorException e){
                throw new ExchangisJobException.Runtime(-1, "Exception in handling job source parameters", e);
            }
        });
    }

    @Override
    public final void handleSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        wrapFuncWithContext(ctx, () -> {
            try {
                handleJobSink(subExchangisJob, ctx);
            } catch (ErrorException e) {
                throw new ExchangisJobException.Runtime(-1, "Exception in handling job sink parameters", e);
            }
        });
    }

    /**
     * Wrap the function(runnable) with context
     * @param context context
     * @param runnable function
     */
    private void wrapFuncWithContext(ExchangisJobBuilderContext context, Runnable runnable){
        if (context instanceof SpringExchangisJobBuilderContext){
            springContext.set((SpringExchangisJobBuilderContext)context);
            // Rest the default param set
            JobParamSet storedParamSet = JobParamDefine.defaultParam.get();
            JobParamDefine.defaultParam.set(new JobParamSet());
            try{
                runnable.run();
            } finally {
                springContext.remove();
                // Restore the default param set
                if (Objects.nonNull(storedParamSet)){
                    JobParamDefine.defaultParam.set(storedParamSet);
                } else {
                    JobParamDefine.defaultParam.remove();
                }
            }
        } else {
            runnable.run();
        }
    }

    /**
     * handle job source params
     * @param subExchangisJob sub exchangis job
     * @param ctx ctx
     */
    public abstract void handleJobSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException;

    /**
     * handle job sink params
     * @param subExchangisJob sub exchangis job
     * @param ctx ctx
     */
    public abstract void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException;

    /**
     * Warn message
     * @param message message
     */
    public static void warn(String message, Object... args){
        Optional.ofNullable(springContext.get()).ifPresent(ctx -> ctx.getLogging().warn(null, message, args));
    }

    public static void warn(String message, Throwable t){
        Optional.ofNullable(springContext.get()).ifPresent(ctx -> ctx.getLogging().warn(null, message, t));
    }

    /**
     * Info message
     * @param message message
     */
    public static void info(String message, Object... args){
        Optional.ofNullable(springContext.get()).ifPresent(ctx -> ctx.getLogging().info(null, message, args));
    }

    public static void info(String message, Throwable t){
        Optional.ofNullable(springContext.get()).ifPresent(ctx -> ctx.getLogging().info(null, message, t));
    }

    public static <T>T getBean(Class<T> clazz){
        return Objects.nonNull(springContext.get())? springContext.get().getBean(clazz) : null;
    }

    protected static SpringExchangisJobBuilderContext getJobBuilderContext(){
        return springContext.get();
    }
}
