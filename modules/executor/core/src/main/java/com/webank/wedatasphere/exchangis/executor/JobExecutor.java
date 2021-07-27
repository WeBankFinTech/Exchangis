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

package com.webank.wedatasphere.exchangis.executor;

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.executor.exception.TaskResAllocException;
import com.webank.wedatasphere.exchangis.executor.listener.TaskHandleListener;
import com.webank.wedatasphere.exchangis.executor.resource.Resource;
import com.webank.wedatasphere.exchangis.executor.resource.ResourceManager;
import com.webank.wedatasphere.exchangis.executor.task.TaskDaemonPoolManager;
import com.webank.wedatasphere.exchangis.executor.task.TaskProcess;
import com.webank.wedatasphere.exchangis.executor.task.TaskProcessBuilder;
import com.webank.wedatasphere.exchangis.executor.task.process.TaskProcessUtils;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.executor.listener.TaskCompleteListener;
import com.webank.wedatasphere.exchangis.executor.daemons.ClockDaemon;
import com.webank.wedatasphere.exchangis.executor.daemons.LoggerDaemon;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by devendeng on 2018/9/5.
 *
 * @author devendeng
 */
public class JobExecutor extends Thread {
    private Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private TaskProcess taskProcess;
    private final int timeout;
    private TaskCompleteListener completeListener;
    private Map<String, TaskHandleListener> handleListenerMap = new HashMap<>();

    private TaskDaemonPoolManager daemonPoolManager;

    private ExecuteStatus status;

    private ReentrantLock exec = new ReentrantLock();
    private ReentrantLock alloc = new ReentrantLock();

    private ResourceManager resourceManager;
    private Condition notAlloc = alloc.newCondition();
    private boolean init = false;

    private long jobId;

    public JobExecutor(long jobId, TaskProcessBuilder processBuilder, int timeout,
                       TaskCompleteListener completeListener){
            this(jobId, processBuilder.build(), timeout, completeListener);
    }

    public JobExecutor(long jobId, TaskProcess taskProcess, int timeout,
                       TaskCompleteListener completeListener){
        this.jobId = jobId;
        this.taskProcess = taskProcess;
        this.timeout = timeout;
        this.completeListener = completeListener;
        this.daemonPoolManager = new TaskDaemonPoolManager(taskProcess);
    }
    /**
     * Kill job thread
     */
    public void toStop() {
        LOG.info("Stop task, info {}", taskProcess);
        try {
            //first to destroy the process
            taskProcess.destroy();
            //second to clean it
            status = ExecuteStatus.KILL;
        } catch (Exception e) {
            long taskId = TaskProcessUtils.getTaskId(taskProcess);
            LOG.error("TASK_KILL_ERROR, kill task error, jobId: {} , taskId: {}, message: {}",
                    TaskProcessUtils.getJobId(taskProcess), taskId, e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * Fetch log
     * @param startLine
     * @param windSize
     * @return
     */
    public LogResult log(long startLine, long windSize){
        return this.taskProcess.log(startLine, windSize);
    }
    public Resource getResource(){
        return taskProcess.getResource();
    }

    /**
     * Wait for allocating resource for executor
     * @return is allocated
     */
    public boolean waitForAlloc(int interval, TimeUnit timeUnit){
        alloc.lock();
        try{
            if(!TaskProcessUtils.isAllocate(taskProcess)){
                if(notAlloc.await(interval, timeUnit)){
                    //Do nothing
                }
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupt when waiting for allocating resource for 'jobExecutor'");
            Thread.currentThread().interrupt();
        } finally{
            alloc.unlock();
        }
        return TaskProcessUtils.isAllocate(taskProcess);
    }

    public void init() throws Exception {
        taskProcess.init();
        init = true;
    }

    public void clean(){
        if(null != taskProcess) {
            taskProcess.clean();
        }
    }
    @Override
    public void run() {
        status = ExecuteStatus.NONE;
        String message = "";
        long jobId = TaskProcessUtils.getJobId(taskProcess);
        long taskId = TaskProcessUtils.getTaskId(taskProcess);
        Exception ex = null;
        try {
            if(!init) {
                taskProcess.init();
            }
            try {
                Future<Integer> taskFuture = allocAndExec(taskProcess);
                LOG.info("Task {} 's status change to RUNNING", taskId);
                status = ExecuteStatus.RUNNING;
                int exitCode;
                if(!taskFuture.isDone()) {
                    //Get lock for executing
                    exec.lock();
                    LOG.info("Open daemon threads for taskId: [{}]", taskId);
                    openDaemons(taskProcess, jobId + "_" + taskId);
                    try {
                        exitCode = taskFuture.get();
                    } catch (InterruptedException e) {
                        LOG.error("Task [{}] process have been interrupted", taskId);
                        throw e;
                    } finally {
                        //Release lock for executing
                        exec.unlock();
                    }
                }else{
                    exitCode = taskFuture.get();
                }
                if(exitCode == JobContainer.TASK_CODE_SUCCESS){
                    status = ExecuteStatus.SUCCESS;
                }else if (exitCode == JobContainer.TASK_CODE_KILL){
                    status = ExecuteStatus.KILL;
                }else{
                    status = ExecuteStatus.FAILD;
                }
            }catch(TaskResAllocException e){
                try {
                    LOG.info("Allocate task [{}] failed, message: [{}] try to destroy the process...", taskId, e.getMessage());
                    taskProcess.destroy();
                }catch(Exception e0){
                    //ignore
                }
            }
        } catch (Exception e) {
            if(status == ExecuteStatus.RUNNING) {
                ex = e;
                //this must to be failed
                status = ExecuteStatus.FAILD;
                message = e.getMessage();
            }else{
                LOG.error("Task allocate failed, [{}], message: [{}]", taskId, e.getMessage());
            }
        } finally {
            clean(taskProcess);
            if(status != ExecuteStatus.NONE && status != ExecuteStatus.KILL){
                if(null == ex | !(ex instanceof InterruptedException)) {
                    try {
                        //任务完成情况
                        completeListener.handleComplete(taskProcess, status, message);
                    } catch (Exception e) {
                        LOG.error("TASK_ERROR, notify server task [{}] complete error.", String.valueOf(taskId), e);
                    }
                    try {
                        LOG.info("Start to close the daemon pool of task [{}]", taskId);
                        long record = System.currentTimeMillis();
                        daemonPoolManager.shutdown();
                        LOG.info("Finish closing the daemon pool of task [{}], spend time in millis: [{}]",
                                taskId, System.currentTimeMillis() - record);
                    }catch(Exception e){
                        //ignore error
                        LOG.error("Cannot shutdown the task [{}] 's daemons thread completely.", String.valueOf(taskId), e);
                    }
                }
            }
            TaskHandleListener listener = handleListenerMap.get(String.valueOf(Hook.TASK_END));
            if(null != listener){
                listener.handle(taskProcess, message);
            }
        }

    }

    /**
     * Add hook
     * @param hook
     */
    public void addHook(Hook hook, TaskHandleListener handleListener){
        handleListenerMap.put(String.valueOf(hook), handleListener);
    }

    /**
     * Get task state
     * @return task state
     */
    public TaskState getTaskState(){
        return TaskProcessUtils.getTaskState(taskProcess);
    }

    private void openDaemons(TaskProcess process, String namePrefix){
       daemonPoolManager.addDaemon(
               new LoggerDaemon(namePrefix , JobContainer.LOG_STDOUT_NAME,
                       this.taskProcess, process.getInputStream())
       );
       daemonPoolManager.addDaemon(
               new LoggerDaemon(namePrefix, JobContainer.LOG_STDERR_NAME,
                       this.taskProcess, process.getErrorStream())
       );
       daemonPoolManager.addDaemon(
               new ClockDaemon(namePrefix + "-clock", this.taskProcess, this.exec,
                       this.timeout, this.handleListenerMap.get(String.valueOf(Hook.TASK_TIMEOUT)))
       );
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * Allocate resource for process and execute
     * @param taskProcess
     * @return
     */
    private Future<Integer> allocAndExec(TaskProcess taskProcess) throws TaskResAllocException{
        //Get lock for allocating
        alloc.lock();
        boolean exec = false;
        boolean preAlloc = true;
        try{
            Resource resource = this.taskProcess.getResource();
            //Pre allocate 如果存在 resourceManager 对象，这尝试预分配资源，如果分配成功，则 preAlloc = true
            if(null != resourceManager){
                preAlloc = resourceManager.allocate(resource);
            }
            if(preAlloc) {
                LOG.info("Pre-allocate task [{}] resource success, " +
                        "resource:["+ Json.toJson(resource, null) + "]", TaskProcessUtils.getTaskId(taskProcess));
                //execute and allocate actually
                return taskProcess.executeAsync();
            }else{  // 分配失败，抛出异常
                LOG.info("Pre-allocate task:[" + TaskProcessUtils.getTaskId(taskProcess) + "]  resource failed," +
                        " resource:[" + Json.toJson(resource, null)+ "]");
                throw new TaskResAllocException();
            }
        }finally{
            notAlloc.signalAll();
            alloc.unlock();
        }
    }

    private void clean(TaskProcess taskProcess){
        long taskId = TaskProcessUtils.getTaskId(taskProcess);
        LOG.info("Task [{}] finish, start to release the resource", taskId);
        try {
            if(null != resourceManager){
                resourceManager.collect(taskProcess.getResource());
            }
            taskProcess.clean();
        }catch(Exception e){
            LOG.error("IO_ERROR: release the resource of  task [{}] failed", taskId, e);
        }
    }
    public long getJobId() {
        return jobId;
    }

    public enum Hook{
        /**
         * Condition: timeout
         */
        TASK_TIMEOUT,
        /**
         * Condition: end
         */
        TASK_END
    }
}
