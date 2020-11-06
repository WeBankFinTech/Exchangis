/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.job.config.builder;

import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.AbstractJobTemplate;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.TransportType;
import com.webank.wedatasphere.exchangis.job.config.handlers.JobDataConfHandler;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.dto.Transform;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import com.webank.wedatasphere.exchangis.job.domain.JobConfForm;
import com.webank.wedatasphere.exchangis.job.domain.JobEngine;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.webank.wedatasphere.exchangis.job.JobConstants.DATAX_TEMPLATE_LOC;
import static com.webank.wedatasphere.exchangis.job.JobConstants.DEFAULT_TEMPLATE_LOC;

/**
 * Build 'jobConfig' String
 * according to configuration map and job template file
 * @author enjoyyin
 * 2018/10/26
 */
public class JobConfBuilder extends AbstractJobTemplate {
    private static final Logger logger = LoggerFactory.getLogger(JobConfBuilder.class);
    private static final String DEFAULT_MEMORY_UNIT = "M";

    private String configTemplateLocation;
    private TypeEnums readerType = TypeEnums.NONE;
    private TypeEnums writerType = TypeEnums.NONE;
    private Map<String, Object> readerDataFormParams = new HashMap<>();
    private Map<String, Object> writerDataFormParams = new HashMap<>();
    private Map<String, Object> jobFormParams = new HashMap<>();
    private ForkJoinTask<Map<String, Object>> readerPreHandler;
    private ForkJoinTask<Map<String, Object>> writerPreHandler;

    private JobConfBuilder(String configTemplateLocation){
        this.configTemplateLocation = configTemplateLocation;
    }
    public static JobConfBuilder custom(){
        return new JobConfBuilder(DATAX_TEMPLATE_LOC);
    }

    public static JobConfBuilder custom(JobEngine engineEnum){
        return new JobConfBuilder(null != engineEnum ? DEFAULT_TEMPLATE_LOC + "/" + engineEnum.toString().toLowerCase() : DATAX_TEMPLATE_LOC);
    }

    public static JobConfBuilder custom(String configTemplateLocation){
        return new JobConfBuilder(configTemplateLocation);
    }

    public JobConfBuilder reader(DataSource dataSource, Map<String, Object> dataFormParams){
        String t = dataSource.getSourceType().toLowerCase();
        JobDataConfHandler jobDataConfHandler;
        try{
            jobDataConfHandler = AppUtil.getBean(JobDataConfHandler.PREFIX + t, JobDataConfHandler.class);
            readerPreHandler = new AbstractConfPreHandlerTask(dataFormParams) {
                @Override
                protected void handle(Map<String, Object> params) {
                    jobDataConfHandler.prePersistHandle(DataConfType.READER, dataSource, params);
                }
            };
        }catch(BeansException e){
            //Cannot find the handler
            logger.info(e.getMessage());
        }
        this.readerType = TypeEnums.type(t);
        return this;
    }

    public JobConfBuilder reader(TypeEnums enums){
        this.readerType = enums;
        return this;
    }

    public JobConfBuilder reader(TypeEnums enums,  Map<String, Object> dataFormParams){
        this.readerType = enums;
        this.readerDataFormParams =  dataFormParams;
        return this;
    }


    public JobConfBuilder writer(DataSource dataSource, Map<String, Object> dataFormParams){
        String t = dataSource.getSourceType().toLowerCase();
        JobDataConfHandler jobDataConfHandler;
        try {
            jobDataConfHandler =
                    AppUtil.getBean(JobDataConfHandler.PREFIX + t, JobDataConfHandler.class);
            writerPreHandler = new AbstractConfPreHandlerTask(dataFormParams) {
                @Override
                protected void handle(Map<String, Object> params) {
                    jobDataConfHandler.prePersistHandle(DataConfType.WRITER, dataSource, params);
                }
            };
        }catch(BeansException e){
            //Cannot find the handler
            logger.info(e.getMessage());
        }
        this.writerType = TypeEnums.type(t);
        return this;
    }

    public JobConfBuilder writer(TypeEnums enums){
        this.writerType = enums;
        return this;
    }

    public JobConfBuilder writer(TypeEnums enums, Map<String, Object> dataFormParams){
        this.writerType = enums;
        this.writerDataFormParams = dataFormParams;
        return this;
    }
    public JobConfBuilder speed(JobConfForm.Speed speed){
        if(speed.getRecord() >0 || speed.getmBytes() > 0) {
            jobFormParams.put(JobConstants.CONFIG_SPEED_BYTE_NAME, (long) (speed.getmBytes() * Math.pow(2, 20)));
            jobFormParams.put(JobConstants.CONFIG_SPEED_RECORD_NAME, speed.getRecord());
        }
        return this;
    }

    public JobConfBuilder advance(JobConfForm.Advance advance){
        if(advance.getmMemory() > 0){
            jobFormParams.put(JobConstants.CONFIG_ADVANCE_MEMORY, advance.getmMemory() + advance.getMemoryUnit());
        }
        jobFormParams.put(JobConstants.CONFIG_ADVANCE_PARALLEL, advance.getmParallel());
        return this;
    }
    public JobConfBuilder errorLimit(JobConfForm.ErrorLimit errorLimit){
        jobFormParams.put(JobConstants.CONFIG_ERRORLIMIT_RECORD, errorLimit.getRecord());
        return this;
    }

    public JobConfBuilder transformer(List<Transform> transforms){
        jobFormParams.put(JobConstants.CONFIG_TRANSFORMER_NAME, transforms);
        return this;
    }

    public JobConfBuilder transportType(String transportType){
        TransportType type = TransportType.type(transportType);
        jobFormParams.put(JobConstants.CONFIG_TRANSPORT_TYPE, type.v());
        return this;
    }

    public JobConfBuilder syncMeta(Boolean flag){
        jobFormParams.put(JobConstants.CONFIG_SYNC_METADATA,flag);
        return this;
    }

    public JobConfBuilder usePostProcessor(boolean useProcess){
        jobFormParams.put(JobConstants.CONFIG_USE_POSTPROCESSOR, useProcess);
        return this;
    }
    public String build(){
        ForkJoinPool pool = new ForkJoinPool(3);
        Future<String> future = pool.submit(new RecursiveTask<String>() {
            @Override
            protected String compute() {
                if(writerPreHandler != null){
                    writerPreHandler.fork();
                }
                if(readerPreHandler != null){
                    readerPreHandler.fork();
                }
                writerDataFormParams = writerPreHandler != null ? writerPreHandler.join() : new HashMap<>(1);
                readerDataFormParams = readerPreHandler != null ? readerPreHandler.join() : new HashMap<>(1);
                return "success";
            }
        });
        try {
            future.get();
        } catch (ExecutionException e){
            logger.error("PreHandler dataFormParams failed, message: " + e.getMessage(), e);
            if(e.getCause() instanceof JobDataParamsInValidException){
                JobDataParamsInValidException jobDataParamsInValidException = (JobDataParamsInValidException)e.getCause();
                throw new EndPointException(jobDataParamsInValidException.getMessage(), null,
                        jobDataParamsInValidException.getArgs());
            }else if (e.getCause() instanceof EndPointException){
                throw (EndPointException)e.getCause();
            }
            throw new EndPointException("exchange.job_conf.error.in.configuration", e);
        } catch (Exception e) {
            logger.error("PreHandler dataFromParams failed, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.job_conf.error.in.configuration", e);
        }
        matchColumns(readerDataFormParams, writerDataFormParams);
        String readerTemplate = getTemplateContent(configTemplateLocation + READER_DIRECTORY +
                "/" + readerType.v() + DEFAULT_SUFFIX);
        String readerJson = PatternInjectUtils.inject(readerTemplate, readerDataFormParams);
        if(StringUtils.isNotBlank(readerJson)){
            jobFormParams.put(JobConstants.CONFIG_READER_NAME, readerJson);
        }
        String writerTemplate = getTemplateContent(configTemplateLocation + WRITER_DIRECTORY +
                "/" + writerType.v() + DEFAULT_SUFFIX);
        String writerJson = PatternInjectUtils.inject(writerTemplate, writerDataFormParams);
        if(StringUtils.isNotBlank(writerJson)){
            jobFormParams.put(JobConstants.CONFIG_WRITER_NAME, writerJson);
        }
        String jobTemplate = getTemplateContent(configTemplateLocation + "/"+ COMMON_TEMPLATE_FILE);
        return PatternInjectUtils.inject(jobTemplate, jobFormParams);
    }

    /**
     * Keep the writer and writer columnList's length match
     * @param readerDataFormParams
     * @param writerDataFormParams
     */
    @SuppressWarnings("unchecked")
    private void matchColumns(Map<String, Object> readerDataFormParams, Map<String, Object> writerDataFormParams){
        List<DataColumn> readerColumns = new ArrayList<>();
        List<DataColumn> writerColumns = new ArrayList<>();
        if(null != readerDataFormParams.get(JobConstants.CONFIG_COLUM_NAME)){
            readerColumns = (List<DataColumn>)readerDataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        }
        if(null != writerDataFormParams.get(JobConstants.CONFIG_COLUM_NAME)){
            writerColumns = (List<DataColumn>)writerDataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        }
        int minLen = Math.min(readerColumns.size(), writerColumns.size());
        if(minLen > 0) {
            readerColumns = readerColumns.subList(0, minLen);
            writerColumns = writerColumns.subList(0, minLen);
        }
        //write back to params Map
        readerDataFormParams.put(JobConstants.CONFIG_COLUM_NAME, readerColumns);
        writerDataFormParams.put(JobConstants.CONFIG_COLUM_NAME, writerColumns);
    }
    public static abstract class AbstractConfPreHandlerTask extends ForkJoinTask<Map<String, Object>> {
        private Map<String, Object> result;
        public AbstractConfPreHandlerTask(Map<String, Object> params){
            this.result = params;
        }

        /**
         * handle the params
         * @param params
         */
        protected abstract  void handle(Map<String, Object> params);
        @Override
        public Map<String, Object> getRawResult() {
            return result;
        }

        @Override
        protected void setRawResult(Map<String, Object> value) {
            result = value;
        }

        @Override
        protected boolean exec() {
            handle(result);
            return true;
        }
    }

}
