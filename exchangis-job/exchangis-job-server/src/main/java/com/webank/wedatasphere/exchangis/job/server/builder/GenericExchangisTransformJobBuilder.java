package com.webank.wedatasphere.exchangis.job.server.builder;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.ExchangisTransformJob;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TransformJob builder
 */
public class GenericExchangisTransformJobBuilder extends AbstractExchangisJobBuilder<ExchangisJob, ExchangisTransformJob> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExchangisTransformJobBuilder.class);

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
                            "], expect sub jobs: [" + contents.size() + "]");
                    //Second to new SubExchangisJob instances
                    List<SubExchangisJob> subExchangisJobs = contents.stream().map(ExchangisTransformJob.SubExchangisJobAdapter::new)
                            .collect(Collectors.toList());
                    outputJob.setSubJobSet(subExchangisJobs);
                    LOG.info("Invoke job handlers to handle the parameters of sub jobs, ExchangisJob: id: [" + inputJob.getId() + "], name: [" + inputJob.getJobName() + "]");
                    //TODO do transform
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
