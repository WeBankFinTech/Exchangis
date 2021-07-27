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

import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.SnowFlake;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.exec.remote.BalanceRunService;
import com.webank.wedatasphere.exchangis.exec.remote.ExecuteService;
import com.webank.wedatasphere.exchangis.alarm.AlarmTemplate;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteReq;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteResp;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.job.JobConfiguration;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import com.webank.wedatasphere.exchangis.job.config.HistoryInterval;
import com.webank.wedatasphere.exchangis.job.dao.*;
import com.webank.wedatasphere.exchangis.job.domain.*;
import com.webank.wedatasphere.exchangis.job.service.JobTaskAliveCallback;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;
import com.webank.wedatasphere.exchangis.queue.manager.QueueSchedulerManager;
import com.webank.wedatasphere.exchangis.route.exception.NoAvailableServerException;
import com.webank.wedatasphere.exchangis.route.exception.RpcReqTimeoutException;
import com.webank.wedatasphere.exchangis.route.feign.FeignConstants;
import com.netflix.client.ClientException;
import org.apache.calcite.util.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by devendeng on 2018/8/24.
 * @author devendeng
 */
@Service
public class JobTaskServiceImpl extends AbstractGenericService<JobTask> implements JobTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(JobTaskServiceImpl.class);

    @Resource
    private JobTaskDao jobTaskDao;

    @Resource
    private JobInfoParamsDao jobInfoParamsDao;
    @Resource
    private JobProcessorDao jobProcessorDao;

    @Resource
    private JobConfiguration.TaskRun taskRunConf;

    @Resource
    private JobConfiguration.TaskQueue taskQueueConf;

    @Resource
    private JobInfoDao jobInfoDao;

    @Resource
    private JobExecNodeDao jobExecNodeDao;

    @Resource
    private ExecuteService executeService;

    @Resource
    private BalanceRunService balanceRunService;

    @Resource
    private JobConfiguration.TaskAlive taskConf;

    @Resource
    private EurekaDiscoveryClient discoveryClient;

    @Resource
    private QueueSchedulerManager queueManager;

    @Resource
    private JobInfoServiceImpl jobInfoService;

    @Resource
    private JobTaskParamsDao jobTaskParamsDao;

    @Resource
    private HistoryInterval historyInterval;

    @Value("${snowflake.startTime}")
    private long startTime;

    @Value("${snowflake.workerId}")
    private long workerId;

    @Value("${snowflake.dataCenterId}")
    private long dataCenterId;

    private ThreadPoolExecutor taskAlivePool;

    private SnowFlake idGenerator;
    @PostConstruct
    public void init(){
        idGenerator = new SnowFlake(dataCenterId, workerId, startTime);
        taskAlivePool = new ThreadPoolExecutor(taskConf.getAlivePoolCore(),
                taskConf.getAlivePoolNum(),60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(taskConf.getAlivePoolQueueSize()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
                );
    }

    @Override
    protected IBaseDao<JobTask> getDao() {
        return jobTaskDao;
    }

    /**
     * Task status list
     * @param idList
     * @return
     */
    @Override
    public List<String> statusList(List<Long> idList){
        List<JobTask> jobTasks = jobTaskDao.selectByIdList(idList);
        List<String> statusList = new ArrayList<>();
        for(JobTask jobTask : jobTasks){
            statusList.add(jobTask.getStatus());
        }
        return statusList;
    }

    @Override
    public void checkAliveInBatch(int batchSize, JobTaskAliveCallback callback) {
        LOG.info("Start to check task alive in batch");
        long idBegin = 0;
        while(true) {
            List<JobTask> jobTaskList = jobTaskDao.findRunInBatch(idBegin, batchSize);
            int size = jobTaskList.size();
            if (size <= 0){
                break;
            }
            LOG.info("Fetched running task, result size: {}, lastId: {}, batchSize: {}", size,
                    jobTaskList.get(size - 1).getId(), batchSize);
            jobTaskList.forEach(jobTask -> {
                taskAlivePool.execute(() -> {
                    Exception exception = null;
                    String message = null;
                    Response<Boolean> response = null;
                    List<ServiceInstance> instances = discoveryClient.getInstances(JobConstants.EXECUTOR_NAME);
                    for(ServiceInstance instance : instances){
                        String hostPot = instance.getHost() + ":" + instance.getPort();
                        if(hostPot.equals(jobTask.getExecuterAddress())){
                            try {
                                response = executeService.alive(jobTask.getExecuterAddress(), jobTask.getJobId(), jobTask.getId());
                            } catch (Exception e) {
                                exception = e;
                                message = e.getMessage();
                            } finally {
                                String errorMsgPattern = "Checking 'task alive' occurred exception, executor address: {}, jobId:{}, taskId: {}, message:{}";
                                if (null != exception) {
                                    LOG.error(errorMsgPattern,
                                            jobTask.getExecuterAddress(), jobTask.getJobId(), jobTask.getId(), message);
                                } else if (null != response && null == response.getData()) {
                                    LOG.error(errorMsgPattern, jobTask.getExecuterAddress(), jobTask.getJobId(), jobTask.getId(), response.getMessage());
                                } else if (null != response) {
                                    try {
                                        if (response.getData()) {
                                            callback.alive(jobTask);
                                        } else {
                                            callback.notAlive(jobTask);
                                        }
                                    } catch (Exception e) {
                                        LOG.trace("Callback 'task alive' occurred exception, jobId: {}, taskId: {}, message{}",
                                                jobTask.getJobId(), jobTask.getId(), e.getMessage());
                                    }
                                }
                            }
                            break;
                        }
                    }
                });
            });
            if(size < batchSize){
                break;
            }
            idBegin = jobTaskList.get(size - 1).getId();
        }
    }


    @Override
    public boolean checkAlive(int taskId) {
        return true;
    }

    @Override
    public boolean runTask(QueueElement taskQueueElement) {     // 这个方法是通过 QueueSchedulerManager 来调度的
        long taskId = taskQueueElement.getId();
        JobTask jobTask = jobTaskDao.selectOne(taskId);
        if(null != jobTask && StringUtils.isBlank(jobTask.getExecuterAddress())){
            JobInfo jobInfo = jobInfoDao.selectOne(jobTask.getJobId());
            if(null == jobInfo){
                //Remove task directly
                jobTaskDao.delete(Collections.singletonList(String.valueOf(jobTask)));
                return true;
            }
            Integer exists = jobExecNodeDao.existsRelationNodes(jobTask.getJobId());
            //Fetch job params
            Map<String, String> jobParams = jobInfoParamsDao.getParamsMapByJobId(jobTask.getJobId());
            //Fetch task params
            Map<String, Object> params = jobTaskParamsDao.getMapByTask(taskId);
            jobParams.forEach(params::putIfAbsent);
            Map<String, Object> engineParams = new HashMap<>(1);
            boolean withLabel = exists!= null && exists > 0;
            ExecuteReq req = buildExecuteReq(jobTask, jobInfo);
            req.setTaskParams(params);
            req.setEngineParams(engineParams);
            //Fetch processor source code
            String srcCode = jobProcessorDao.fetchSrcCode(jobTask.getJobId());
            if(StringUtils.isNotBlank(srcCode)) {
                engineParams.put(DefaultParams.Engine.PARAM_PROC_SRC_CODE, srcCode);
            }
            String errorMsgPattern = "run task {} occurred exception, message: {}";
            boolean result = false;
            try {
                Response<ExecuteResp> response;
                if(withLabel){
                    response = balanceRunService.run(FeignConstants.LB_LABEL_PREFIX_JOB
                                    + jobTask.getJobId(), req);
                }else if(StringUtils.isNotBlank(jobInfo.getCreateUser())){
                    response = balanceRunService.run(FeignConstants.LB_LABEL_PREFIX_USER
                    + jobInfo.getCreateUser(), req);
                }else{
                    response = balanceRunService.run(req);
                }
                if( null == response.getData() || !response.getData()
                        .getStatus().equals(ExecuteStatus.RUNNING)){
                    if(null != response.getData()){
                        jobTask.setExecuteMsg(response.getData().getMessage());
                    }
                    if(response.getCode() == CodeConstant.TASK_ALLOCATE_FAILD){
                        //Cannot allocate resource, delay to choose executor again
                        LOG.trace(errorMsgPattern, taskId, Json.toJson(response, null));
                        delayQueueElement(taskQueueElement);
                    }else{
                        LOG.info(errorMsgPattern, taskId, Json.toJson(response, null));
                    }
                }else {
                    LOG.info("Run task [{}] successfully, executor response [{}]", taskId, Json.toJson(response, null));
                    jobTask.setStatus(ExecuteStatus.RUNNING.name());
                    jobTask.setExecuterAddress(response.getData().getExecutorAddress());
                    jobTask.setExecuteMsg(response.getData().getMessage());
                    jobTask.setExecUser(response.getData().getExecUser());
                    result =true;
                }
            }catch(Throwable e){
                result = false;
                Throwable cause = e;
                boolean delay = false;
                while(null != cause){
                    if(cause instanceof RpcReqTimeoutException){
                        jobInfoService.sendInfoToAlarm(taskId, jobInfo, AlarmTemplate.TITLE_TASK_RPC_TIMEOUT,
                                PatternInjectUtils.inject(AlarmTemplate.TASK_RPC_TIMEOUT,
                                        new Object[]{((RpcReqTimeoutException)cause).getRemoteAddress(),
                                                jobInfo.getJobName(), jobInfo.getId(), taskId}), true);
                        break;
                    }
                    if(cause instanceof NoAvailableServerException || cause instanceof ClientException){
                        delayQueueElement(taskQueueElement);
                        delay = true;
                        break;
                    }
                    cause = cause.getCause();
                }
                if(!delay){
                    LOG.error(errorMsgPattern, taskId, e.getMessage());
                }
            }
            if(!result && taskQueueElement.getEnqCount() >= taskQueueConf.getQueueMaxEnq()){
                jobTask.setStatus(ExecuteStatus.FAILD.name());
                jobInfoService.sendInfoToAlarm(taskId, jobInfo, AlarmTemplate.TITLE_TASK_SCHEDULER_ERROR,
                        PatternInjectUtils.inject(AlarmTemplate.TASK_SCHEDULER_ERROR,
                                new Object[]{jobInfo.getJobName(), jobInfo.getId(), taskId}, false, false, true));
                removeFromQueue(taskId);
                result = true;
            }
            if(result && jobInfo.getDisposable() != null && jobInfo.getDisposable()){
                jobInfoService.delete(Collections.singletonList(jobTask.getJobId()));
            }
            //Update jobTask's info
            jobTaskDao.update(jobTask);
            return result;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertTaskAndAddToQueue(JobTask task) {
        //Add task date parameter
        JobTaskParams params = new JobTaskParams();
        params.setParamName(DefaultParams.Task.PARAM_DATE);
        params.setParamVal(String.valueOf(System.currentTimeMillis()));
        insertTaskAndAddToQueue(task, Collections.singletonList(params));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertTaskAndAddToQueue(JobTask task, List<JobTaskParams> params) {
        task.setId(idGenerator.nextId());
        jobTaskDao.insert(task);
        params.forEach(param -> param.setTaskId(task.getId()));
        addTaskParams(params);
        addTaskToQueue(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskAndAddToQueue(JobTask task) {
        jobTaskDao.update(task);
        addTaskToQueue(task);
    }

    @Override
    public void removeFromQueue(long taskId) {
        Queue<QueueElement> queue = queueManager.chooseQueue();
        if(null != queue) {
            QueueElement element = new QueueElement();
            element.setId(taskId);
            queue.remove(element);
            LOG.info("remove element related task id: {} from queue", taskId);
        }
    }

    @Override
    public void addTaskParams(List<JobTaskParams> params) {
        jobTaskParamsDao.insertBatch(params);
    }


    @Override
    public void runHistoryTaskByTime(JobInfo jobInfo, JobHistoryReq req, String userName) {
       Long startTime = req.getStartTime();
       Long endTime = req.getEndTime();
       int step = req.getStep();
       Long intervalTime = null;
       if(req.getUnit().equals(HistoryInterval.DAY)){
           intervalTime = NumberUtil.round(NumberUtil.multiply(1000d,NumberUtil.multiply(60d,NumberUtil.multiply(60d,NumberUtil.multiply(24d, (double) step)))));
       }
       if(req.getUnit().equals(HistoryInterval.HOUR)){
           intervalTime = NumberUtil.round(NumberUtil.multiply(1000d,NumberUtil.multiply(60d,NumberUtil.multiply(60d, (double) step))));
       }
       if(req.getUnit().equals(HistoryInterval.MINUTE)){
           intervalTime = NumberUtil.round(NumberUtil.multiply(1000d,NumberUtil.multiply(60d, (double) step)));
       }
       JobTask jobTask = new JobTask(jobInfo,"api",  userName);
       List<JobTaskParams> paramsList = new ArrayList<>();
       paramsList.add(new JobTaskParams(DefaultParams.Task.PARAM_DATE,String.valueOf(startTime)));
       while(startTime < endTime){
           startTime += intervalTime;
           if(startTime <= endTime){
               paramsList.add(new JobTaskParams(DefaultParams.Task.PARAM_DATE,String.valueOf(startTime)));
           }
       }
       for(JobTaskParams param : paramsList){
           JobTaskService jobTaskServiceProxy = (JobTaskService)AopContext.currentProxy();
           jobTaskServiceProxy.insertTaskAndAddToQueue(jobTask, Collections.singletonList(param));
       }

    }

    @Override
    public Map<String, Object> getTaskParameters(long taskId) {
        return jobTaskParamsDao.getMapByTask(taskId);
    }

    @Override
    public void updateTaskState(String address, List<TaskState> stateList) {
        List<JobTask> jobTaskList = new ArrayList<>();
        stateList.forEach(state->{
            if(null != state.getTaskId() && null != state.getCurrentByteSpeed()){
                JobTask jobTask = new JobTask();
                jobTask.setId(state.getTaskId());
                jobTask.setStatus(ExecuteStatus.RUNNING.toString());
                jobTask.setExecuterAddress(address);
                jobTask.setStateSpeed(state.getCurrentByteSpeed());
                jobTask.setVersion(state.getVersionTime());
                jobTaskList.add(jobTask);
            }
        });
        if(!jobTaskList.isEmpty()) {
            this.jobTaskDao.updateStates(jobTaskList);
        }
    }

    @Override
    public void limitSpeed(JobTask jobTask,  int speedLimit) {
        this.jobTaskDao.limitSpeed(jobTask.getId(), speedLimit);
        long byteSpeedLimit = speedLimit * 1024L  * 1024L;
        //Limit the speed of running task
        this.executeService.runtimeLimitSpeed(jobTask.getExecuterAddress(), jobTask.getJobId(),
                jobTask.getId(), byteSpeedLimit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectAndUpdate(JobTask jobTask) {
        Long taskId = jobTask.getId();
        if(null != taskId){
            JobTask selectOne = this.jobTaskDao.selectOneAndLock(jobTask.getId());
            jobTask.setVersion(selectOne.getVersion());
            return jobTaskDao.update(jobTask) > 0;
        }
        return false;
    }

    private ExecuteReq buildExecuteReq(JobTask jobTask, JobInfo jobInfo){
        ExecuteReq req = new ExecuteReq();
        req.setJobId(jobInfo.getId());
        req.setTaskId(jobTask.getId());
        req.setJobConfig(jobInfo.getJobConfig());
        req.setStartTime(jobTask.getTriggerTime().getTime());
        //Set execute user
        req.setExecUser(jobInfo.getExecUser());
        Long timeout = jobInfo.getTimeout();
        if(null == timeout || timeout <= 0){
            timeout = JobConstants.DEFAULT_JOB_TIMEOUT_IN_SECONDS;
        }
        req.setTimeout(timeout);
        req.setEngine(jobInfo.getEngineType());
        return req;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<Object> ids) {
        //Delete tasks' parameters
        jobTaskParamsDao.deleteByTaskIds(ids);
        return super.delete(ids);
    }

    private void delayQueueElement(QueueElement element){
        int delayCount = element.getDelayCount();
        int[] timesInSec = taskQueueConf.getQueuePollDelayTime();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, timesInSec[ delayCount % timesInSec.length]);
        element.setStatus(0);
        element.setDelayTime(calendar.getTime());
        element.setDelayCount(delayCount + 1);
        LOG.trace("No available executor, delay element {}", Json.toJson(element, null));
    }
    // 把要运行的任务加入到队列中
    private void addTaskToQueue(JobTask task){
        Queue<QueueElement> queue = queueManager.chooseQueue();
        if(null == queue){
            throw new EndPointException("udes.jobtaskservice.execute.queue.failed", null);
        }

        QueueElement element = new QueueElement();
        element.setId(task.getId());
        element.setStatus(0);
        queue.add(element);
        LOG.info("Add task {} to queue id: {} successfully", Json.toJson(task, null), element.getQid());
    }

}
