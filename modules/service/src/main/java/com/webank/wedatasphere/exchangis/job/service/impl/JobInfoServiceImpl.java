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

import com.webank.wedatasphere.exchangis.alarm.AlarmService;
import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import com.webank.wedatasphere.exchangis.job.dao.JobExecNodeDao;
import com.webank.wedatasphere.exchangis.job.dao.JobInfoDao;
import com.webank.wedatasphere.exchangis.job.dao.JobInfoParamsDao;
import com.webank.wedatasphere.exchangis.job.dao.JobProcessorDao;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.job.domain.JobExecNode;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import com.webank.wedatasphere.exchangis.job.domain.JobInfoParams;
import com.webank.wedatasphere.exchangis.job.service.JobInfoService;
import com.webank.wedatasphere.exchangis.scheduler.cluster.JobDynamicScheduler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * Created by devendeng on 2018/8/24.
 * @author devendeng
 */
@Service
public class JobInfoServiceImpl extends AbstractGenericService<JobInfo> implements JobInfoService {
    private static Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);
    @Resource
    private JobInfoDao jobInfoDao;
    @Resource
    private JobExecNodeDao jobExecNodeDao;
    @Resource
    private JobProcessorDao jobProcessorDao;
    @Resource
    private JobInfoParamsDao jobInfoParamsDao;
    @Resource
    private JobDynamicScheduler jobDynamicScheduler;
    @Resource
    private AlarmService alarmService;
    @Override
    protected IBaseDao<JobInfo> getDao() {
        return jobInfoDao;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(JobInfo jobInfo) {
        if(null == jobInfo.getTimeout() || jobInfo.getTimeout() <= 0){
            jobInfo.setTimeout(JobConstants.DEFAULT_JOB_TIMEOUT_IN_SECONDS);
        }
        // Add to db
        boolean result = super.add(jobInfo);
        List<Integer> execNodes = jobInfo.getExecNodes();
        List<JobExecNode> jobExecNodes = new ArrayList<>();
        //Add executor node
        if(!execNodes.isEmpty()) {
            execNodes.forEach(nodeId -> {
                JobExecNode jobExecNode = new JobExecNode();
                jobExecNode.setExecId(nodeId);
                jobExecNode.setJobId(jobInfo.getId());
                jobExecNodes.add(jobExecNode);
            });
            jobExecNodeDao.insertBatch(jobExecNodes);
        }
        Map<String, String> params = jobInfo.getJobParams();
        //add job params
        List<JobInfoParams> jobParams = new ArrayList<>();
        if(!params.isEmpty()){
            params.forEach((key, value) -> jobParams.add(new JobInfoParams(jobInfo.getId(), key, value)));
            jobInfoParamsDao.insertBatch(jobParams);
        }
        if(StringUtils.isNotEmpty(jobInfo.getJobCorn())){
            if (result) {
                try {
                    jobDynamicScheduler.addJob(jobInfo.getId().toString(), jobInfo.getJobCorn());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    try {
                        delete(jobInfo.getId().toString());
                        jobDynamicScheduler.removeJob(jobInfo.getId().toString());
                    } catch (Exception e1) {
                        logger.error(e1.getMessage());
                    }
                    if(e.getCause() instanceof ParseException){
                        throw new JobDataParamsInValidException("任务调度表达式错误");
                    }
                    result = false;
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(JobInfo jobInfo) {
        if(null == jobInfo.getTimeout() || jobInfo.getTimeout() <= 0){
            jobInfo.setTimeout(JobConstants.DEFAULT_JOB_TIMEOUT_IN_SECONDS);
        }
        boolean result = super.update(jobInfo);
        updateJobExecNodes(jobInfo);
        updateJobParams(jobInfo);
        logger.info("Update:" + Json.toJson(jobInfo, null) +", result:" + result);
        if (result) {
            try {
                String jobName = jobInfo.getId().toString();
                if (StringUtils.isNotEmpty(jobInfo.getJobCorn())) {
                    if(!jobDynamicScheduler.rescheduleJob(jobName, jobInfo.getJobCorn())){
                        jobDynamicScheduler.addJob(jobName, jobInfo.getJobCorn());
                    }
                } else {
                    if(jobDynamicScheduler.checkExists(jobName)){
                        jobDynamicScheduler.removeJob(jobName);
                    }
                }
            } catch (Exception e) {
                if(e.getCause() instanceof ParseException){
                    throw new JobDataParamsInValidException("Corn Expression Wrong");
                }
                logger.error(e.getMessage(), e);
                result = false;
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String ids) {
        boolean result = true;
        try {
            String[] idsStr = ids.split(",");
            for (String id : idsStr) {
                jobDynamicScheduler.removeJob(id);
            }
            super.delete(ids);
            //Delete Relations
            List<Long> jobIds = new ArrayList<>();
            for(String id :idsStr){
                jobIds.add(Long.valueOf(id));
            }
            jobExecNodeDao.deleteBatchByJobIds(jobIds);
            //Delete job params
            jobInfoParamsDao.deleteBatchByJobIds(jobIds);
            //Delete job processor
            jobProcessorDao.deleteBatch(jobIds);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean delete(List<Object> ids) {
        boolean result = true;
        try {
            for (Object id : ids) {
                jobDynamicScheduler.removeJob(String.valueOf(id));
            }
            super.delete(ids);
            //Delete Relations
            List<Long> jobIds = new ArrayList<>();
            for(Object id : ids){
                jobIds.add(Long.valueOf(String.valueOf(id)));
            }
            jobExecNodeDao.deleteBatchByJobIds(jobIds);
            //Delete job params
            jobInfoParamsDao.deleteBatchByJobIds(jobIds);
            //Delete job processor
            jobProcessorDao.deleteBatch(jobIds);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public JobInfo get(Object id) {
        JobInfo jobInfo = super.get(id);
        if(null != jobInfo){
            if(StringUtils.isNotEmpty(jobInfo.getJobCorn())) {
                jobDynamicScheduler.fillJobInfo(jobInfo);
            }
            List<ExecutorNode> nodes = jobExecNodeDao.getNodesByJobId(Long.valueOf(String.valueOf(id)));
            List<Integer> execNodes = new ArrayList<>();
            nodes.forEach(node -> execNodes.add(node.getId()));
            jobInfo.setExecNodes(execNodes);
            jobInfo.setJobParams(jobInfoParamsDao.getParamsMapByJobId(Long
                    .parseLong(String.valueOf(id))));
        }
        return jobInfo;
    }


    @Override
    protected JobInfo queryFilter(JobInfo jobInfo) {
        if(null != jobInfo) {
            if (StringUtils.isNotEmpty(jobInfo.getJobCorn())) {
                jobDynamicScheduler.fillJobInfo(jobInfo);
            }
        }
        return jobInfo;
    }

    @Override
    public boolean resume(Long id) {
        boolean result = true;
        try {
            result = jobDynamicScheduler.resumeJob(id.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    @Override
    public boolean pause(Long id) {
        boolean result = true;
        try {
            result = jobDynamicScheduler.pauseJob(id.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    @Override
    public boolean triggerJob(Long id) {
        boolean result = true;
        try {
            result = jobDynamicScheduler.triggerJob(id.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }


    @Override
    public void sendInfoToAlarm(long taskId, JobInfo jobInfo, String alarmTitle, String alarmInfo) {
        sendInfoToAlarm(taskId, jobInfo, alarmTitle, alarmInfo, false);
    }

    @Override
    public void sendInfoToAlarm(long taskId, long jobId, String alarmTitle, String alarmInfo) {
        sendInfoToAlarm(taskId, jobInfoDao.selectOne(jobId), alarmTitle, alarmInfo, false);
    }


    @Override
    public boolean isRunWithDataSource(Long id) {
        List<JobInfo> jobInfos = jobInfoDao.getBySrcOrDstDataId(id);
        return !jobInfos.isEmpty();
    }

    @Override
    public void sendInfoToAlarm(long taskId, JobInfo jobInfo, String alarmTitle, String alarmInfo, boolean defaultAlarmUser) {
        Set<String> dedupAlarmUser = new HashSet<>();
        dedupAlarmUser.add(jobInfo.getCreateUser());
        String alarmJobUsers = jobInfo.getAlarmUser();
        if(StringUtils.isNotBlank(alarmJobUsers)){
            Collections.addAll(dedupAlarmUser, alarmJobUsers.split(","));
        }
        if(defaultAlarmUser && null != alarmService.getDefaultAlarmUsers() &&
                !alarmService.getDefaultAlarmUsers().isEmpty()){
            dedupAlarmUser.addAll(alarmService.getDefaultAlarmUsers());
        }
        String alarmReceivers = StringUtils.join(dedupAlarmUser, ",");
        String extraMsg = "Please alarm(请通知): " + alarmReceivers;
        alarmService.doSend(alarmTitle,  jobInfo.getAlarmLevel(), alarmInfo + extraMsg, new ArrayList<>(dedupAlarmUser));
    }

    private void updateJobExecNodes(JobInfo jobInfo){
        List<Integer> updateExecNodes = new ArrayList<>(jobInfo.getExecNodes());
        List<Integer> oldExecNodes = new ArrayList<>();
        for (ExecutorNode executorNode : jobExecNodeDao.getNodesByJobId(jobInfo.getId())) {
            oldExecNodes.add(executorNode.getId());
        }
        updateExecNodes.removeAll(oldExecNodes);
        //add
        List<JobExecNode> addRelation = new ArrayList<>();
        updateExecNodes.forEach(nodeId -> addRelation.add(new JobExecNode(jobInfo.getId(), nodeId)));
        if(!addRelation.isEmpty()){
            jobExecNodeDao.insertBatch(addRelation);
        }
        oldExecNodes.removeAll(jobInfo.getExecNodes());
        //Delete
        List<JobExecNode> deleteRelation = new ArrayList<>();
        oldExecNodes.forEach(nodeId -> deleteRelation.add(new JobExecNode(jobInfo.getId(), nodeId)));
        if(!deleteRelation.isEmpty()){
            jobExecNodeDao.deleteBatch(deleteRelation);
        }
    }

    private void updateJobParams(JobInfo jobInfo){
        long jobId = jobInfo.getId();
        Map<String, String> updateParams = jobInfo.getJobParams();
        Map<String, String> oldParams = jobInfoParamsDao.getParamsMapByJobId(jobInfo.getId());
        //Search the update parameters
        Set<String> updateKeys = new HashSet<>(updateParams.keySet());
        updateKeys.retainAll(oldParams.keySet());
        List<JobInfoParams> updateParamsList = new ArrayList<>();
        updateKeys.forEach(updateKey -> {
            String newValue = updateParams.getOrDefault(updateKey, "");
            if(!newValue.equals(oldParams.get(updateKey))){
                updateParamsList.add(new JobInfoParams(jobId, updateKey, newValue));
            }
        });
        if(!updateParamsList.isEmpty()){
            //Update
            this.jobInfoParamsDao.updateBatch(updateParamsList);
        }
        //Search the added parameters
        Set<String> addedKeys = new HashSet<>(updateParams.keySet());
        addedKeys.removeAll(updateKeys);
        List<JobInfoParams> addedParamsList = new ArrayList<>();
        addedKeys.forEach(addedKey -> addedParamsList.add(new JobInfoParams(jobId, addedKey, updateParams.get(addedKey))));
        if(!addedParamsList.isEmpty()){
            //Add
            this.jobInfoParamsDao.insertBatch(addedParamsList);
        }
        //Search the deleted parameters
        Set<String> deletedKeys = new HashSet<>(oldParams.keySet());
        deletedKeys.removeAll(updateKeys);
        if(!deletedKeys.isEmpty()) {
            this.jobInfoParamsDao.deleteBatch(jobId, new ArrayList<>(deletedKeys));
        }
    }
}
