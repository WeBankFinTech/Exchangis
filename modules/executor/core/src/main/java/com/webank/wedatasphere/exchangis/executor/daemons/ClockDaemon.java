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

package com.webank.wedatasphere.exchangis.executor.daemons;

import com.webank.wedatasphere.exchangis.executor.listener.TaskHandleListener;
import com.webank.wedatasphere.exchangis.executor.task.TaskProcess;
import com.webank.wedatasphere.exchangis.executor.task.process.TaskProcessUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author davidhua
 * 2019/2/18
 */
public class ClockDaemon extends AbstractTaskDaemon{
    private final ReentrantLock exec;
    private long timeoutDuration;
    private TimeUnit timeUnit;
    private TaskHandleListener listener;


    public ClockDaemon(String daemonName, TaskProcess taskProcess, ReentrantLock exec ){
        super(daemonName, taskProcess);
        this.exec = exec;
    }

    public ClockDaemon(String daemonName, TaskProcess taskProcess,
                       ReentrantLock exec, long timeoutDuration, TaskHandleListener listener){
        super(daemonName, taskProcess);
        this.exec = exec;
        this.timeoutDuration = timeoutDuration;
        this.timeUnit = TimeUnit.SECONDS;
        this.listener = listener;
    }

    public ClockDaemon(String daemonName,TaskProcess taskProcess,
                       ReentrantLock exec, long timeoutDuration, TimeUnit timeUnit, TaskHandleListener listener) {
        super(daemonName, taskProcess);
        this.exec = exec;
        this.timeoutDuration = timeoutDuration;
        this.timeUnit = timeUnit;
        this.listener = listener;
    }

    @Override
    public void run() {
        TimeUnit unit = timeUnit;
        long timeStamp = System.currentTimeMillis();
        try {
            if(null != listener && timeoutDuration > 0){
                if(null == unit){
                    unit = TimeUnit.SECONDS;
                }
                long clock = System.currentTimeMillis();
                int count = 0;
                while(!exec.tryLock(timeoutDuration, unit)){
                    //The task is timeout, invoke the listener
                    try {
                        listener.handle(taskProcess, "RUN " +
                                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - clock)
                                + " " +unit.name());
                        if(++ count >= 3){
                            break;
                        }
                    }catch(Exception e){
                        logger.error("Notify server task {} timeout error", TaskProcessUtils.getTaskId(taskProcess), e);
                    }
                    exec.lock();
                }
            }else{
                exec.lock();
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally{
           if(exec.isHeldByCurrentThread()){
               exec.unlock();
               if(logger.isInfoEnabled()) {
                   logger.info("Task finished, spent time in seconds: {}",
                           TimeUnit.SECONDS.convert(System.currentTimeMillis() - timeStamp, TimeUnit.MILLISECONDS));
               }
           }
        }
    }
}
