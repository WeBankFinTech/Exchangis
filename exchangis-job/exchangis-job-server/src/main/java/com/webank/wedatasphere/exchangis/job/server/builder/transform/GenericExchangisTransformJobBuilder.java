package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.SubExchangisJobHandler;
import com.webank.wedatasphere.linkis.common.utils.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private static final Map<String, SubExchangisJobHandler> handlerHolders = new ConcurrentHashMap<>();

    public synchronized void initHandlers() {
        //Should define wds.linkis.reflect.scan.package in properties
        Set<Class<? extends SubExchangisJobHandler>> jobHandlerSet = ClassUtils.reflections().getSubTypesOf(SubExchangisJobHandler.class);
        Map<String, SubExchangisJobHandler> reflectedHandlers = jobHandlerSet.stream().map(handlerClass -> {
            if (!Modifier.isAbstract(handlerClass.getModifiers()) &&
                    !Modifier.isInterface(handlerClass.getModifiers())) {
                try {
                    return handlerClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.warn("Cannot create the instance of handler: [" + handlerClass.getCanonicalName() + "], message: [" + e.getMessage() + "]", e);
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(SubExchangisJobHandler::dataSourceType, handler -> handler));
        handlerHolders.putAll(reflectedHandlers);
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
                List<ExchangisJobInfoContent> contents = Json.fromJson(inputJob.getContent(), ExchangisJobInfoContent.class);
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
                        SubExchangisJobHandler sourceHandler = handlerHolders.get(subExchangisJob.getSourceType());
                        if(Objects.isNull(sourceHandler)){
                            LOG.warn("Cannot find source handler for subJob named: [" + subExchangisJob.getJobName() + "], sourceType: [" + subExchangisJob.getSourceType() +
                                    "], ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "]");
                        }
                        SubExchangisJobHandler sinkHandler = handlerHolders.get(subExchangisJob.getSinkType());
                        if(Objects.isNull(sinkHandler)){
                            LOG.warn("Cannot find sink handler for subJob named: [" + subExchangisJob.getJobName() + "], sinkType: [" + subExchangisJob.getSourceType() +
                                    "], ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "]");
                        }
                        LOG.info("Invoke handles for subJob: [" + subExchangisJob.getJobName() + "], sourceHandler: [" +
                                sourceHandler + "], sinkHandler: [" + sinkHandler + "]");
                        //TODO Handle the subExchangisJob parallel
                        sourceHandler.handleSource(subExchangisJob, ctx);
                        sinkHandler.handleSource(subExchangisJob, ctx);
                    }
                }else{
                    throw new ExchangisJobException(ExchangisJobExceptionCode.TRANSFORM_JOB_ERROR.getCode(),
                            "Illegal content string: [" + inputJob.getContent() + "] in job, please check", null);
                }
            }else{
                LOG.warn("It looks like an empty job ? id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "]");
            }
        }catch(Exception e){
            throw new ExchangisJobException(ExchangisJobExceptionCode.TRANSFORM_JOB_ERROR.getCode(),
                    "Fail to build transformJob from input job, message: [" + e.getMessage() + "]", e);
        }
        return outputJob;
    }
}
