package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.netflix.config.ChainedDynamicProperty;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.SubExchangisJobHandler;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.common.utils.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TransformJob builder
 */
public class GenericExchangisTransformJobBuilder extends AbstractExchangisJobBuilder<ExchangisJob, ExchangisTransformJob> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExchangisTransformJobBuilder.class);

    /**
     * Handlers
     */
    private static final Map<String, SubExchangisJobHandlerChain> handlerHolders = new ConcurrentHashMap<>();

    public synchronized void initHandlers() {
        //Should define wds.linkis.reflect.scan.package in properties
        Set<Class<? extends SubExchangisJobHandler>> jobHandlerSet = ClassUtils.reflections().getSubTypesOf(SubExchangisJobHandler.class);
        List<SubExchangisJobHandler> reflectedHandlers = jobHandlerSet.stream().map(handlerClass -> {
            if (!Modifier.isAbstract(handlerClass.getModifiers()) &&
                    !Modifier.isInterface(handlerClass.getModifiers())) {
                try {
                    return handlerClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.warn("Cannot create the instance of handler: [" + handlerClass.getCanonicalName() + "], message: [" + e.getMessage() + "]", e);
                }
            }
            return null;
        }).filter(handler -> Objects.nonNull(handler) && Objects.nonNull(handler.dataSourceType())).collect(Collectors.toList());
        reflectedHandlers.forEach(reflectedHandler -> handlerHolders.compute(reflectedHandler.dataSourceType(), (type, handlerChain) -> {
           if (Objects.isNull(handlerChain)){
               handlerChain = new SubExchangisJobHandlerChain(type);
           }
           handlerChain.addHandler(reflectedHandler);
           return handlerChain;
        }));
        LOG.trace("Sort the handler chain");
        handlerHolders.values().forEach(SubExchangisJobHandlerChain::sort);
        LOG.trace("Add the default handlerChain to the head");
        //Add the default handlerChain to the head
        Optional.ofNullable(handlerHolders.get(SubExchangisJobHandler.DEFAULT_DATA_SOURCE_TYPE)).ifPresent(defaultHandlerChain ->
                handlerHolders.forEach( (s, handlerChain) -> {if(!Objects.equals(handlerChain, defaultHandlerChain)){
                    handlerChain.addFirstHandler(defaultHandlerChain);
        }}));
    }
    @Override
    public ExchangisTransformJob buildJob(ExchangisJob inputJob, ExchangisTransformJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        LOG.trace("Start to build exchangis transform job, name: [" + inputJob.getJobName() + "], id: [" + inputJob.getId() + "], " +
                "engine: [" + inputJob.getEngineType() + "], content: [" + inputJob.getContent() + "]");
        //First to convert content to "ExchangisJobInfoContent"
        ExchangisTransformJob outputJob = new ExchangisTransformJob();
        try {
            if (StringUtils.isNotBlank(inputJob.getContent())) {
                //First to convert content to "ExchangisJobInfoContent"
                List<ExchangisJobInfoContent> contents = Json.fromJson(inputJob.getContent(), List.class, ExchangisJobInfoContent.class);
                if (Objects.nonNull(contents) ) {
                    LOG.info("To parse content ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() +
                            "], expect subJobs: [" + contents.size() + "]");
                    //Second to new SubExchangisJob instances
                    List<SubExchangisJob> subExchangisJobs = contents.stream().map(ExchangisTransformJob.SubExchangisJobAdapter::new)
                            .collect(Collectors.toList());
                    outputJob.setSubJobSet(subExchangisJobs);
                    LOG.info("Invoke job handlers to handle the subJobs, ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "]");
                    //Do handle of the sub jobs
                    for (SubExchangisJob subExchangisJob : subExchangisJobs){
                        if(StringUtils.isBlank(subExchangisJob.getEngine())){
                            subExchangisJob.setEngine(inputJob.getEngineType());
                        }
                        SubExchangisJobHandler sourceHandler = handlerHolders.get(subExchangisJob.getSourceType().toLowerCase());
                        if(Objects.isNull(sourceHandler)){
                            LOG.warn("Not find source handler for subJob named: [" + subExchangisJob.getJobName() + "], sourceType: [" + subExchangisJob.getSourceType() +
                                    "], ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "], use default instead");
                            sourceHandler = handlerHolders.get(SubExchangisJobHandler.DEFAULT_DATA_SOURCE_TYPE);
                        }
                        SubExchangisJobHandler sinkHandler = handlerHolders.get(subExchangisJob.getSinkType().toLowerCase());
                        if(Objects.isNull(sinkHandler)){
                            LOG.warn("Not find sink handler for subJob named: [" + subExchangisJob.getJobName() + "], sinkType: [" + subExchangisJob.getSourceType() +
                                    "], ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "], use default instead");
                            sinkHandler = handlerHolders.get(SubExchangisJobHandler.DEFAULT_DATA_SOURCE_TYPE);
                        }
                        LOG.trace("Invoke handles for subJob: [" + subExchangisJob.getJobName() + "], sourceHandler: [" +
                                sourceHandler + "], sinkHandler: [" + sinkHandler + "]");
                        //TODO Handle the subExchangisJob parallel
                        if (Objects.nonNull(sourceHandler)) {
                            sourceHandler.handleSource(subExchangisJob, ctx);
                        }
                        if (Objects.nonNull(sinkHandler)){
                            sinkHandler.handleSource(subExchangisJob, ctx);
                        }
                    }
                }else{
                    throw new ExchangisJobException(ExchangisJobExceptionCode.TRANSFORM_JOB_ERROR.getCode(),
                            "Illegal content string: [" + inputJob.getContent() + "] in job, please check", null);
                }
            }else{
                LOG.warn("It looks like an empty job ? id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "]");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ExchangisJobException(ExchangisJobExceptionCode.TRANSFORM_JOB_ERROR.getCode(),
                    "Fail to build transformJob from input job, message: [" + e.getMessage() + "]", e);
        }
        return outputJob;
    }

    /**
     * Chain
     */
    private static class SubExchangisJobHandlerChain implements SubExchangisJobHandler{

        private String dataSourceType;

        private LinkedList<SubExchangisJobHandler> handlers = new LinkedList<>();

        public SubExchangisJobHandlerChain(String dataSourceType){
            this.dataSourceType = dataSourceType;
        }
        public void addFirstHandler(SubExchangisJobHandler handler){
            handlers.addFirst(handler);
        }

        public void addHandler(SubExchangisJobHandler handler){
            handlers.add(handler);
        }
        public void sort(){
            handlers.sort(Comparator.comparingInt(SubExchangisJobHandler::order));
        }
        @Override
        public String dataSourceType() {
            return dataSourceType;
        }

        @Override
        public void handleSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
            for(SubExchangisJobHandler handler : handlers){
                if(handler.acceptEngine(subExchangisJob.getEngine())) {
                    handler.handleSource(subExchangisJob, ctx);
                }
            }
        }

        @Override
        public void handleSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
            for(SubExchangisJobHandler handler : handlers){
                if(handler.acceptEngine(subExchangisJob.getEngine())) {
                    handler.handleSink(subExchangisJob, ctx);
                }
            }
        }
    }
}
