package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.webank.wedatasphere.exchangis.common.linkis.bml.BmlResource;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.AbstractLoggingExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.SubExchangisJobHandler;
import com.webank.wedatasphere.exchangis.job.server.mapper.JobTransformProcessorDao;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformTypes;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.TransformProcessor;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import com.webank.wedatasphere.exchangis.utils.SpringContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TransformJob builder
 */
public class GenericExchangisTransformJobBuilder extends AbstractLoggingExchangisJobBuilder<ExchangisJobInfo, TransformExchangisJob> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExchangisTransformJobBuilder.class);

    /**
     * Handlers
     */
    private static final Map<String, SubExchangisJobHandlerChain> handlerHolders = new ConcurrentHashMap<>();

    /**
     * Transform dao
     */
    private JobTransformProcessorDao transformProcessorDao;

    public synchronized void initHandlers() {
        //Should define wds.linkis.reflect.scan.package in properties
        Set<Class<? extends SubExchangisJobHandler>> jobHandlerSet = ClassUtils.reflections().getSubTypesOf(SubExchangisJobHandler.class);
        List<SubExchangisJobHandler> reflectedHandlers = jobHandlerSet.stream().map(handlerClass -> {
            if (!Modifier.isAbstract(handlerClass.getModifiers()) &&
                    !Modifier.isInterface(handlerClass.getModifiers()) && !handlerClass.equals(SubExchangisJobHandler.class)) {
                try {
                    return handlerClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.warn("Cannot create the instance of handler: [{}], message: [{}]", handlerClass.getCanonicalName(), e.getMessage(), e);
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
    public TransformExchangisJob buildJob(ExchangisJobInfo inputJob, TransformExchangisJob expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        LOG.trace("Start to build exchangis transform job, name: [{}], id: [{}], engine: [{}], content: [{}]",
                inputJob.getName(), inputJob.getId(), inputJob.getEngineType(), inputJob.getJobContent());
        //First to convert content to "ExchangisJobInfoContent"
        TransformExchangisJob outputJob = new TransformExchangisJob();
        outputJob.setCreateUser(StringUtils.isNotBlank(inputJob.getExecuteUser()) ?
                inputJob.getExecuteUser() : String.valueOf(ctx.getEnv("USER_NAME")));
        try {
            if (StringUtils.isNotBlank(inputJob.getJobContent())) {
                //First to convert content to "ExchangisJobInfoContent"
                List<ExchangisJobInfoContent> contents = Json.fromJson(inputJob.getJobContent(), List.class, ExchangisJobInfoContent.class);
                if (Objects.nonNull(contents) ) {
                    LOG.info("To parse content ExchangisJob: id: [{}], name: [{}], expect subJobs: [{}]",
                            inputJob.getId(), inputJob.getName(), contents.size());
                    //Second to new SubExchangisJob instances
                    List<SubExchangisJob> subExchangisJobs = contents.stream().map(job -> {
                                // Put the params of job info
                                TransformExchangisJob.TransformSubExchangisJob transformSubJob =
                                        new TransformExchangisJob.TransformSubExchangisJob(job, inputJob.getJobParamsMap());
                                transformSubJob.setId(inputJob.getId());
                                transformSubJob.setCreateUser(outputJob.getCreateUser());
                                setTransformCodeResource(transformSubJob);
                                return transformSubJob;
                            })
                            .collect(Collectors.toList());
                    outputJob.setId(inputJob.getId());
                    outputJob.setName(inputJob.getName());
                    LOG.info("Invoke job handlers to handle the subJobs, ExchangisJob: id: [{}], name: [{}]", inputJob.getId(), inputJob.getName());
                    //Do handle of the sub jobs
                    List<SubExchangisJob> handledJobs = new ArrayList<>();
                    for (SubExchangisJob subExchangisJob : subExchangisJobs){
                        if(StringUtils.isBlank(subExchangisJob.getEngineType())){
                            subExchangisJob.setEngineType(inputJob.getEngineType());
                        }
                        SubExchangisJobHandler sourceHandler = handlerHolders.get( StringUtils
                                .isNotBlank(subExchangisJob.getSourceType())? subExchangisJob.getSourceType().toLowerCase():"");
                        if(Objects.isNull(sourceHandler)){
                            LOG.warn("Not find source handler for subJob named: [{}], sourceType: [{}], " +
                                            "ExchangisJob: id: [{}], name: [{}], use default instead",
                                    subExchangisJob.getName(), subExchangisJob.getSourceType(), inputJob.getId(), inputJob.getName());
                            sourceHandler = handlerHolders.get(SubExchangisJobHandler.DEFAULT_DATA_SOURCE_TYPE);
                        }
                        SubExchangisJobHandler sinkHandler = handlerHolders.get( StringUtils
                                .isNotBlank(subExchangisJob.getSinkType())? subExchangisJob.getSinkType().toLowerCase():"");
                        if(Objects.isNull(sinkHandler)){
                            LOG.warn("Not find sink handler for subJob named: [{}], sinkType: [{}], ExchangisJob: id: [{}], name: [{}], use default instead",
                                    subExchangisJob.getName(), subExchangisJob.getSourceType(), inputJob.getId(), inputJob.getName());
                            sinkHandler = handlerHolders.get(SubExchangisJobHandler.DEFAULT_DATA_SOURCE_TYPE);
                        }
                        LOG.trace("Invoke handles for subJob: [{}], sourceHandler: [{}], sinkHandler: [{}]", subExchangisJob.getName(), sourceHandler, sinkHandler);
                        //TODO Handle the subExchangisJob parallel
                        handledJobs.addAll(doHandle(sourceHandler, sinkHandler, subExchangisJob, ctx));
                    }
                    // Render the sub job
                    renderJobs(handledJobs);
                    // Final reset the sub exchangis jobs
                    outputJob.setSubJobSet(handledJobs);
                }else{
                    throw new ExchangisJobException(ExchangisJobExceptionCode.BUILDER_TRANSFORM_ERROR.getCode(),
                            "Illegal content string: [" + inputJob.getJobContent() + "] in job, please check", null);
                }
            }else{
                LOG.warn("It looks like an empty job ? id: [{}], name: [{}]", inputJob.getId(), inputJob.getName());
            }
        }catch(Exception e){
            throw new ExchangisJobException(ExchangisJobExceptionCode.BUILDER_TRANSFORM_ERROR.getCode(),
                    "Fail to build transformJob from input job, message: [" + e.getMessage() + "]", e);
        }
        return outputJob;
    }

    /**
     * Handle main
     * @param sourceHandler source handler
     * @param sinkHandler sink handler
     * @param subExchangisJob sub job
     * @param ctx context
     * @throws ErrorException
     */
    private List<SubExchangisJob> doHandle(SubExchangisJobHandler sourceHandler,
                          SubExchangisJobHandler sinkHandler, SubExchangisJob subExchangisJob,
                          ExchangisJobBuilderContext ctx) throws ErrorException {
        List<SubExchangisJob> handledJobs = new ArrayList<>();
        if (Objects.nonNull(sourceHandler)) {
            sourceHandler.handleSource(subExchangisJob, ctx);
        }
        if (Objects.nonNull(sinkHandler)){
            sinkHandler.handleSink(subExchangisJob, ctx);
        }
        List<Map<String, Object>> sourceSplits = subExchangisJob.getSourceSplits();
        if (!sourceSplits.isEmpty()){
            doSplitHandle(subExchangisJob, SubExchangisJob.REALM_JOB_CONTENT_SOURCE, sourceSplits, handledJobs);
        }
        List<Map<String, Object>> sinkSplits = subExchangisJob.getSinkSplits();
        if (!sinkSplits.isEmpty()){
            if (!sourceSplits.isEmpty()) {
                throw new ExchangisJobException.Runtime(ExchangisJobExceptionCode.JOB_BUILDER_ERROR.getCode(),
                        "Forbidden to split the sub exchangis job in source and sink direction at the same time" +
                                "(禁止同时在sink和source方向拆分子交换作业)", null);
            }
            doSplitHandle(subExchangisJob, SubExchangisJob.REALM_JOB_CONTENT_SINK, sinkSplits, handledJobs);
        }
        return !handledJobs.isEmpty() ? handledJobs : Collections.singletonList(subExchangisJob);
    }

    /**
     * Split handle
     * @param subExchangisJob job
     * @param splitRealm split realm
     * @param splits split
     * @param handledJobs handled jobs
     */
    private void doSplitHandle(SubExchangisJob subExchangisJob,
                               String splitRealm,
                               List<Map<String, Object>> splits, List<SubExchangisJob> handledJobs){
        JobParamSet splitParamSet = subExchangisJob.getRealmParams(splitRealm);
        Map<String, String> mappingParams;
        if (Objects.nonNull(splitParamSet)){
            List<JobParam<?>> params = splitParamSet.toList(false);
            // Convert the params to [mapping_key => param], filter the computed param
            mappingParams = params.stream().filter(param ->
                    StringUtils.isNotBlank(param.getMappingKey()) && !param.isComputed())
                    .collect(Collectors.toMap(JobParam::getMappingKey, JobParam::getStrKey, (left, right) -> left));
            for (Map<String, Object> splitPart : splits){
                SubExchangisJob copy = subExchangisJob.copy();
                JobParamSet copyParamSet = copy.getRealmParams(splitRealm);
                Map<String, Object> jobParams = copy.getJobParams();
                List<String> nameSuffix = new ArrayList<>();
                for (Map.Entry<String, Object> entry : splitPart.entrySet()){
                    // If it is mapping key, overwrite the param value
                    String itemKey = entry.getKey();
                    Object itemValue = entry.getValue();
                    if (mappingParams.containsKey(itemKey)){
                        Optional.ofNullable(copyParamSet.get(mappingParams.get(itemKey)))
                                .ifPresent(param -> param.setValue(itemValue));
                    }
                    nameSuffix.add(String.valueOf(itemValue));
                    jobParams.put(itemKey, itemValue);
                }
                // Overwrite the sub exchangis job name
                if (nameSuffix.size() > 0){
                    copy.setName(copy.getName() + Json.toJson(nameSuffix, null));
                }
                // Add to the handled job list
                handledJobs.add(copy);
            }
        }
    }

    /**
     * Render ${xxx} in jobs (include time placeholder)
     * @param jobs jobs
     */
    @SuppressWarnings("unchecked")
    private void renderJobs(List<SubExchangisJob> jobs){
        Calendar calendar = Calendar.getInstance();
        for (SubExchangisJob subJob : jobs){
            Map<String, Object> jobParams = subJob.getJobParams();
            Object dateTime = jobParams.remove(JobParamConstraints.EXTRA_SUBMIT_DATE);
            long time = Objects.nonNull(dateTime)? Long.parseLong(String.valueOf(dateTime)) : calendar.getTimeInMillis();
            subJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE)
                    .forEach((key, param) -> {
                        Object value = param.getValue();
                        if (value instanceof String){
                            ((JobParam<String>)param)
                                    .setValue(JobUtils.replaceVariable((String)value, jobParams, time));
                        }
                    });
            subJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK)
                    .forEach((key, param) -> {
                        Object value = param.getValue();
                        if (value instanceof String){
                            ((JobParam<String>)param)
                                    .setValue(JobUtils.replaceVariable((String)value, jobParams, time));
                        }
                    });
        }
    }
    /**
     * Set the code resource to transform job
     * @param subExchangisJob sub transform job
     */
    private void setTransformCodeResource(TransformExchangisJob.TransformSubExchangisJob subExchangisJob){
        if (subExchangisJob.getTransformType() == TransformTypes.PROCESSOR){
            TransformProcessor processor = getTransformProcessorDao().getProcInfo(
                    Long.valueOf(subExchangisJob.getJobInfoContent().getTransforms().getCodeId()));
            if (Objects.nonNull(processor)){
                // TODO maybe the content of processor doesn't store in bml
                subExchangisJob.addCodeResource(new
                        BmlResource(processor.getCodeBmlResourceId(), processor.getCodeBmlVersion()));
            }
        }
    }

    /**
     * Processor dao
     * @return dao
     */
    private JobTransformProcessorDao getTransformProcessorDao(){
        if (null == transformProcessorDao) {
            this.transformProcessorDao = SpringContextHolder.getBean(JobTransformProcessorDao.class);
        }
        return this.transformProcessorDao;
    }
    /**
     * Chain
     */
    private static class SubExchangisJobHandlerChain implements SubExchangisJobHandler{

        private String dataSourceType;

        private LinkedList<SubExchangisJobHandler> handlers = new LinkedList<>();

        public SubExchangisJobHandlerChain(){}
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
                if(handler.acceptEngine(subExchangisJob.getEngineType())) {
                    handler.handleSource(subExchangisJob, ctx);
                }
            }
        }

        @Override
        public void handleSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
            for(SubExchangisJobHandler handler : handlers){
                if(handler.acceptEngine(subExchangisJob.getEngineType())) {
                    handler.handleSink(subExchangisJob, ctx);
                }
            }
        }
    }

}
