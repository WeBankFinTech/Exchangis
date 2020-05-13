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

package com.webank.wedatasphere.exchangis.job.service.impl;

import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.MemUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.AbstractJobTemplate;
import com.webank.wedatasphere.exchangis.job.config.builder.JobConfBuilder;
import com.webank.wedatasphere.exchangis.job.config.parser.JobFormParser;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.dto.Transform;
import com.webank.wedatasphere.exchangis.job.config.func.FuncUtils;
import com.webank.wedatasphere.exchangis.job.dao.JobProcessorDao;
import com.webank.wedatasphere.exchangis.job.domain.*;
import com.webank.wedatasphere.exchangis.job.service.JobFuncService;
import com.webank.wedatasphere.exchangis.job.service.JobInfoConfService;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 * 2018/10/30
 */
@Service
public class JobInfoConfServiceImpl implements JobInfoConfService {
    private static final Logger logger = LoggerFactory.getLogger(JobInfoConfService.class);

    @Resource
    private JobInfoServiceImpl jobInfoService;
    @Resource
    private JobTaskService jobTaskService;

    @Resource
    private DataSourceService dataSourceService;

    @Resource
    private JobFuncService jobFuncService;

    @Resource
    private JobProcessorDao processorDao;
    @Override
    public boolean add(JobInfo jobInfo) {
        buildConfig(jobInfo);
        boolean result = jobInfoService.add(jobInfo);
        if(result && jobInfo.getConfig().isUsePostProcess()){
            processorDao.insert(jobInfo.getId(), jobInfo.getConfig().getProcSrcCode());
        }
        //To bind the data source to project
        dataSourceService.bindDataSourceToProject(jobInfo.getProjectId(), jobInfo.getDataSrcId(), jobInfo.getDataDestId());
        return result;
    }

    @Override
    public JobInfo get(Integer id) {
        long used = System.currentTimeMillis();
        JobInfo info = jobInfoService.get(id);
        if(null != info && null != info.getDataDestId() &&
                null != info.getDataSrcId()) {
            DataSource srcDs =
                    dataSourceService.get(info.getDataSrcId());
            DataSource dstDs =
                    dataSourceService.get(info.getDataDestId());
            logger.trace("Get jobInfo: query db use time(in ms):" + (System.currentTimeMillis() - used));
            Map<String, String> funcRefName = jobFuncService.getFuncRefName(info.getEngineType().name(), JobFunction.FunctionType.VERIFY);
            JobConfForm form = JobFormParser.custom(info.getEngineType())
                    .jobFuncNameRef(JobFunction.FunctionType.VERIFY, funcRefName)
                    .parse(info.getJobConfig(),
                            srcDs != null ? TypeEnums.type(srcDs.getSourceType()) : TypeEnums.type(info.getDataSrcType()),
                            dstDs != null ? TypeEnums.type(dstDs.getSourceType()) : TypeEnums.type(info.getDataDestType()));
            if(srcDs != null){
                form.getDataSrcParams().put(JobConstants.FORM_DATATYPE_NAME, srcDs.getSourceType());
                form.getDataSrcParams().put(JobConstants.FORM_DATASOURCE_NAME_NAME, srcDs.getSourceName());
            }else{
                form.getDataSrcParams().put(JobConstants.FORM_DATATYPE_NAME, info.getDataSrcType());
            }
            if(dstDs != null) {
                form.getDataDstParams().put(JobConstants.FORM_DATATYPE_NAME, dstDs.getSourceType());
                form.getDataDstParams().put(JobConstants.FORM_DATASOURCE_NAME_NAME, dstDs.getSourceName());
            }else{
                form.getDataDstParams().put(JobConstants.FORM_DATATYPE_NAME, info.getDataDestType());
            }
            if(form.isUsePostProcess()){
                form.setProcSrcCode(processorDao
                        .fetchSrcCode(info.getId()));
            }
            info.setConfig(form);
        }
        return info;
    }

    @Override
    public boolean update(JobInfo jobInfo) {
        buildConfig(jobInfo);
        if(jobInfo.getConfig().isUsePostProcess()){
            processorDao.insert(jobInfo.getId(), jobInfo.getConfig().getProcSrcCode());
        }else{
            processorDao.delete(jobInfo.getId());
        }
        return jobInfoService.update(jobInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long runJob(JobInfo jobInfo) {
        buildConfig(jobInfo);
        jobInfoService.add(jobInfo);
        if(jobInfo.getConfig().isUsePostProcess()){
            processorDao.insert(jobInfo.getId(), jobInfo.getConfig().getProcSrcCode());
        }
        JobTask jobTask = new JobTask(jobInfo,  "api", jobInfo.getCreateUser());
        jobTask.setDisposable(jobInfo.getDisposable());
        jobTaskService.insertTaskAndAddToQueue(jobTask);
        return jobTask.getId();
    }

    @Override
    public JobInfo templateInfo(TypeEnums src, TypeEnums dest) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setDataSrcType(src.v());
        jobInfo.setDataDestType(dest.v());
        try {
            String config = JobConfBuilder.custom()
                    .reader(src).writer(dest)
                    .build();
            JobConfForm form = JobFormParser.custom().parse(config, src, dest);
            jobInfo.setConfig(form);
        }catch(Exception e){//buildException, parseException
            throw new EndPointException("udes.jobinfoconf.emplate.generation.error", e,e.getMessage());
        }
        return jobInfo;
    }


    private void buildConfig(JobInfo jobInfo){
        JobConfForm form = jobInfo.getConfig();
        List<JobConfForm.ColumnMap> columnMaps = form.getColumnMaps();
        //Fetch columns from columnMaps
        List<DataColumn> readerCols = new ArrayList<>();
        List<DataColumn> writerCols = new ArrayList<>();
        List<Transform> transforms = new ArrayList<>();
        Map<String, String> verifyFuncRefNameMap = null;
        List<String> transformFuncNameList = null;
        for(int i = 0; i < columnMaps.size(); i++){
            JobConfForm.ColumnMap columnMap = columnMaps.get(i);
            readerCols.add(new DataColumn(columnMap.getSrcName(), columnMap.getSrcType(), columnMap.getSrcIndex()));
            writerCols.add(new DataColumn(columnMap.getDstName(), columnMap.getDstType(), columnMap.getSrcIndex()));
            if(StringUtils.isNotBlank(columnMap.getVerifyFunc())){
                if(null == verifyFuncRefNameMap){
                    //Fetch map: verify function -> reference name
                    verifyFuncRefNameMap = jobFuncService.getFuncRefName(jobInfo.getEngineType().name(), JobFunction.FunctionType.VERIFY);
                }
                Transform transformVerify = FuncUtils.parseVerifyFuncToTransform(i, columnMap.getVerifyFunc(), verifyFuncRefNameMap);
                if(null != transformVerify) {
                    transforms.add(transformVerify);
                }
            }
            if(StringUtils.isNotBlank(columnMap.getTransforFunc())){
                if(null == transformFuncNameList){
                    //Fetch transform function list
                    List<JobFunction> transformFuncList = jobFuncService.getFunctions(jobInfo.getEngineType().name(), JobFunction.FunctionType.TRANSFORM);
                    transformFuncNameList = transformFuncList.stream().map(JobFunction::getFuncName).collect(Collectors.toList());;
                }
                Transform transform = FuncUtils.parseTransformFunc(i, columnMap.getTransforFunc(), transformFuncNameList);
                if(null != transform){
                    transforms.add(transform);
                }
            }
        }
        Map<String, Object> dataSrcParams = form.getDataSrcParams();
        Map<String, Object> dataDstParams = form.getDataDstParams();
        dataSrcParams.put(JobConstants.CONFIG_COLUM_NAME, readerCols);
        dataSrcParams.put(JobConstants.CONFIG_DATASOURCEID_NAME, jobInfo.getDataSrcId());
        dataSrcParams.put(JobConstants.CONFIG_TRANSPORT_TYPE, jobInfo.getConfig().getTransportType());
        dataSrcParams.put(JobConstants.CONFIG_SYNC_METADATA, jobInfo.getConfig().isSyncMeta());
        dataDstParams.put(JobConstants.CONFIG_COLUM_NAME, writerCols);
        dataDstParams.put(JobConstants.CONFIG_DATASOURCEID_NAME, jobInfo.getDataDestId());
        dataDstParams.put(JobConstants.CONFIG_TRANSPORT_TYPE, jobInfo.getConfig().getTransportType());
        dataDstParams.put(JobConstants.CONFIG_SYNC_METADATA, jobInfo.getConfig().isSyncMeta());
        DataSource srcDs = dataSourceService.getDetail(jobInfo.getDataSrcId());
        DataSource dstDs = dataSourceService.getDetail(jobInfo.getDataDestId());
        if(null != srcDs){
            jobInfo.setDataSrcOwner(srcDs.getOwner());
        }else if( null != jobInfo.getDataSrcId() && jobInfo.getDataSrcId() > 0){
            logger.error("Cannot find the source datasource, illegal jobInfo dataSrcId: " + jobInfo.getDataSrcId());
            throw new EndPointException("udes.jobinfoconf.not.find.datasource", null);
        }else{
            srcDs = new DataSource();
            srcDs.setParameterMap(dataSrcParams);
        }
        if(StringUtils.isNotBlank(jobInfo.getDataSrcType())){
            srcDs.setSourceType(jobInfo.getDataSrcType());
        }
        if(null != dstDs){
            jobInfo.setDataDestOwner(dstDs.getOwner());
        }else if( null != jobInfo.getDataDestId() && jobInfo.getDataDestId() > 0){
            logger.error("Cannot find the dest datasource, illegal jobInfo dataDstId: " + jobInfo.getDataDestId());
            throw new EndPointException("udes.jobinfoconf.not.find.datasource", null);
        }else{
            dstDs = new DataSource();
            dstDs.setParameterMap(dataDstParams);
        }
        if(StringUtils.isNotBlank(jobInfo.getDataDestType())){
            dstDs.setSourceType(jobInfo.getDataDestType());
        }
        if(jobInfo.getSync() == JobSyncEnum.INCR){
            dataDstParams.put(JobConstants.CONFIG_WRITE_MODE, "append");
        }
        JobConfBuilder jobConfBuilder = JobConfBuilder.custom(jobInfo.getEngineType());
        //compatible
        String jobConfig = jobConfBuilder
                .transportType(form.getTransportType())
                .usePostProcessor(form.isUsePostProcess())
                .speed(form.getSpeed())
                .advance(form.getAdvance())
                .errorLimit(form.getErrorLimit())
                .transformer(transforms)
                .syncMeta(form.isSyncMeta())
                .reader(srcDs, dataSrcParams)
                .writer(dstDs, dataDstParams).build();
        logger.info("Job_config: " + jobConfig);
        Map<String, Object> objectMap = Json.fromJson(jobConfig, Map.class);
        if(null != objectMap){
            removeEmptyInMap(objectMap);
            jobConfig = Json.toJson(objectMap, null);
        }
        jobInfo.setJobConfig(jobConfig);
    }

    private void removeEmptyInMap(Map<String, Object> map){
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            if(entry.getKey().startsWith(AbstractJobTemplate.IGNORE_EMPTY_KEY_SIGN)){
                continue;
            }
            if(null == entry.getValue() || StringUtils.isEmpty(String.valueOf(entry.getValue()))){
                iterator.remove();
            }else if(entry.getValue() instanceof Map){
                Map entryMap = (Map)entry.getValue();
                if(entryMap.isEmpty()){
                    iterator.remove();
                    continue;
                }
                removeEmptyInMap((Map<String, Object>)entryMap);
            }else if(entry.getValue() instanceof List){
                List entryList = (List)entry.getValue();
                if(entryList.isEmpty()){
                    iterator.remove();
                    continue;
                }
                removeEmptyInList((List<Object>)entryList);
            }
        }
    }

    private void removeEmptyInList(List<Object> list){
        Iterator<Object> iterator = list.iterator();
        while(iterator.hasNext()){
            Object v = iterator.next();
            if(v == null || StringUtils.isBlank(String.valueOf(v))){
                iterator.remove();
            }else if(v instanceof Map){
                Map entryMap = (Map)v;
                if(entryMap.isEmpty()){
                    iterator.remove();
                    continue;
                }
                removeEmptyInMap((Map<String, Object>) entryMap);
            }else if(v instanceof List){
                List entryList = (List)v;
                if(entryList.isEmpty()){
                    iterator.remove();
                    continue;
                }
                removeEmptyInList((List<Object>)entryList);
            }
        }
    }
}
