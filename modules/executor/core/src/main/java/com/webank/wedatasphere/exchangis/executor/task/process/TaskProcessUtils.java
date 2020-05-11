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

package com.webank.wedatasphere.exchangis.executor.task.process;

import com.webank.wedatasphere.exchangis.executor.task.TaskProcess;
import com.webank.wedatasphere.exchangis.executor.task.process.datax.DataxTaskProcess;
import com.webank.wedatasphere.exchangis.executor.task.process.sqoop.SqoopTaskProcess;
import com.webank.wedatasphere.exchangis.job.domain.JobEngine;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * @author davidhua
 * 2019/2/20
 */
public class TaskProcessUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TaskProcessUtils.class);
    private static Map<JobEngine, Class<? extends TaskProcess>> engineProcessMap = new HashMap<>();
    static{
        engineProcessMap.put(JobEngine.DATAX, DataxTaskProcess.class);
        engineProcessMap.put(JobEngine.SQOOP, SqoopTaskProcess.class);
    }

    public static TaskProcess buildTaskProcess(JobEngine engine, Object... args){
        if(null == engine){
            engine = JobEngine.DATAX;
        }
        Class<? extends TaskProcess> clazz = engineProcessMap.get(engine);
        if(null != clazz){
            try {
                Constructor constructor = clazz.getConstructors()[0];
                return (TaskProcess) constructor.newInstance(args);
            } catch (Exception e) {
                LOG.error("New task process fail, message:[" + e.getMessage() + "]", e);
                //ignore
            }
        }
        return null;
    }

    public static Process getProcess(TaskProcess taskProcess){
        if(taskProcess instanceof AbstractJavaInternalTaskProcess){
            return ((AbstractJavaInternalTaskProcess)taskProcess).getProcess();
        }
        return null;
    }

    public static long getJobId(TaskProcess taskProcess){
        if(taskProcess instanceof AbstractTaskProcess){
            return ((AbstractTaskProcess)taskProcess).getJobId();
        }
        return -1;
    }

    public static long getTaskId(TaskProcess taskProcess){
        if(taskProcess instanceof AbstractTaskProcess){
            return ((AbstractTaskProcess)taskProcess).getTaskId();
        }
        return -1;
    }

    public static boolean isAllocate(TaskProcess taskProcess){
        if(taskProcess instanceof AbstractJavaInternalTaskProcess){
            return ((AbstractJavaInternalTaskProcess)taskProcess).isAlloc();
        }
        return taskProcess.isAlive();
    }

    public static File getWorkDir(TaskProcess taskProcess){
        if(taskProcess instanceof AbstractTaskProcess){
            return ((AbstractTaskProcess)taskProcess).getWorkDir();
        }
        return null;
    }

    public static TaskState getTaskState(TaskProcess taskProcess){
        if(taskProcess instanceof AbstractTaskProcess){
            return ((AbstractTaskProcess)taskProcess).getTaskState();
        }
        return null;
    }

}
