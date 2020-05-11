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

package com.webank.wedatasphere.exchangis.executor.task;

import com.webank.wedatasphere.exchangis.executor.daemons.AbstractTaskDaemon;
import com.webank.wedatasphere.exchangis.executor.task.process.TaskProcessUtils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Each task thread has a daemonPool
 * @author davidhua
 * 2019/2/18
 */
public class TaskDaemonPoolManager {

    private static final long SHUTDOWN_WAIT_SECOND = 5;

    private TaskProcess taskProcess;

    private final ThreadPoolExecutor internalPool;

    public TaskDaemonPoolManager(TaskProcess taskProcess){
        this.taskProcess = taskProcess;
        this.internalPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
               60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new DaemonThreadFactory());
    }

    public void addDaemon(AbstractTaskDaemon daemon){
        this.internalPool.execute(daemon);
    }

    public boolean shutdown() throws InterruptedException {
        internalPool.shutdown();
        return internalPool.awaitTermination(SHUTDOWN_WAIT_SECOND, TimeUnit.SECONDS);
    }
    private class DaemonThreadFactory implements ThreadFactory{
        private static final String TASK_DAEMON = "taskDaemon-";
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        DaemonThreadFactory(){
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = TASK_DAEMON + TaskProcessUtils.getJobId(taskProcess) + "-"
                    + TaskProcessUtils.getTaskId(taskProcess) + "-";
        }
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(), 0);
            if(t.isDaemon()){
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY){
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
