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

package com.webank.wedatasphere.exchangis.executor.task.process.datax;

import ch.qos.logback.core.status.NopStatusListener;
import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.util.MemUtils;
import com.webank.wedatasphere.exchangis.common.util.machine.MachineInfo;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.resource.Resource;
import com.webank.wedatasphere.exchangis.executor.task.process.AbstractJavaInternalTaskProcess;
import com.webank.wedatasphere.exchangis.executor.util.RunShell;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by devendeng on 2018/9/6.
 *
 * @author devendeng
 */
public class DataxTaskProcess extends AbstractJavaInternalTaskProcess {

    private static Logger LOG = LoggerFactory.getLogger(DataxTaskProcess.class);
    private static final String PID_F_NAME = "pid";
    private static final String PROC_FILE_NAME_PREFIX = "PROC_";
    private static final String JOB_ADVANCE_MAX_MEMORY = "job.setting.advance.mMemory";
    private static final String DEFAULT_JVM_HEAP_MEMORY = "1g";
    private static final String LOG_LEVEL_CMD = "loglevel";
    private static final String LOGBACK_LISTENER_CMD = "logback.statusListenerClass";
    private static final String LOGBACK_CONF_CMD = "logback.configurationFile";
    private static final String HOME_CMD = "datax.home";
    private static final String JOB_ID_CMD = "jobid";
    private static final String MODE_CMD = "mode";
    private static final String JOB_CMD = "job";
    private static final String LOG_FILE_NAME_CMD = "log.file.name";
    private static final String SERVER_ADDRESS_CMD = "server.address";
    private static final String EXEC_USER_CMD = "exec.user";

    private enum Method{
        JAVA, PYTHON;
    }

    /**
     * If allocate enough resource, default: false
     */
    private volatile boolean alloc = false;

    private String procSrcCode;

    private String loggerDir;
    private String dataXHome;
    private String procSrcPath;

    public DataxTaskProcess(long jobId, long taskId, String execUser,
                            TaskConfiguration taskConfig, ExecutorConfiguration configuration,
                            Map<String, Object> engineParams) {
        super(jobId, taskId, execUser, taskConfig, configuration, engineParams);
        this.loggerDir = configuration.getJobLogDir();
        this.procSrcCode = String.valueOf(engineParams.getOrDefault(DefaultParams.Engine.PARAM_PROC_SRC_CODE, ""));
        this.procSrcPath = configuration.getEngineDataXProcSrc();
        this.dataXHome = configuration.getEngineDataXHome();
    }

    @Override
    protected void initInternal() {
        try {
            //Write configuration to file
            FileUtils.writeStringToFile(new File(workDir, getExecConfig().getEngineDataXConfFile()), getTaskConfig().toJson());
            LOG.debug("Write {}/{} success.", workDir.getPath(), getExecConfig().getEngineDataXConfFile());
            //Write processor code to file
            if(StringUtils.isNotBlank(procSrcCode)) {
                FileUtils.writeStringToFile(new File(workDir.getAbsolutePath() + IOUtils.DIR_SEPARATOR_UNIX + procSrcPath,
                        PROC_FILE_NAME_PREFIX + System.currentTimeMillis()), procSrcCode);
            }
        }catch (Exception e){
            LOG.error("Write job config error",e);
        }
    }

    @Override
    public boolean isAlloc() {
        return alloc;
    }


    @Override
    protected boolean checkIfAlloc() {
        File file = new File(workDir + "/" + PID_F_NAME);
        alloc = file.exists();
        return alloc;
    }

    @Override
    public Process executeInternal() {
        String execUser = getExecUser();
        Method method = getMethod();
        String command = "";
        // --jvm=-Xms -Xmx
        String maxMemory = getTaskConfig().getString(JOB_ADVANCE_MAX_MEMORY, DEFAULT_JVM_HEAP_MEMORY).toLowerCase();
        long mMemory = MemUtils.convertToMB(Long.parseLong(maxMemory.substring(0, maxMemory.length() - 1)),
                maxMemory.substring(maxMemory.length() - 1));
        if(method == Method.PYTHON){
            command = StringUtils.join(new String[]{
                    getExecConfig().getEngineDataXPythonShell(), getExecConfig().getEngineDataXPythonScript(),
                    getExecConfig().getEngineDataXConfFile(),
                    "--" + JOB_ID_CMD + "=" + getTaskId(),
                    "--jvm='-Xms" + mMemory + "m -Xmx" + mMemory + "m'",
                    "-p'" +
                    " -D" + EXEC_USER_CMD + "=" + execUser +
                    " -D" + LOG_FILE_NAME_CMD + "=" + logFileName() +
                    " -D" + SERVER_ADDRESS_CMD + "=" + StringUtils.substringAfter(AppUtil.getIpAndPort(), "http://") +
                    "'"}, " ");
        }else{
            JavaCommandBuilder builder = new JavaCommandBuilder(MachineInfo.getProcPath(),
                    getExecConfig().getEngineDataXJavaMainClass())
                    .Xmx(mMemory, MemUtils.StoreUnit.MB).Xms(mMemory, MemUtils.StoreUnit.MB)
                    .classPath(getExecConfig().getEngineDataXJavaClassPath())
                    .prop("-D" + LOG_LEVEL_CMD, "info")
                    .prop("-D" + LOGBACK_LISTENER_CMD, NopStatusListener.class.getName())
                    .prop("-D" + HOME_CMD, dataXHome)
                    .prop("-D" + LOG_FILE_NAME_CMD,  logFileName())
                    .prop("-D" + LOGBACK_CONF_CMD, getExecConfig().getEngineDataXJavaLogConf())
                    .prop("-D" + SERVER_ADDRESS_CMD, StringUtils.substringAfter(AppUtil.getIpAndPort(), "http://"))
                    .cmdProp("-" + JOB_ID_CMD, String.valueOf(getTaskId()))
                    .cmdProp("-" + JOB_CMD, this.workDir.getAbsolutePath() + File.separator + getExecConfig().getEngineDataXConfFile())
                    .cmdProp("-" + MODE_CMD, "standalone");
            command = builder.build();
        }
        if(needToSwitchUser(execUser)){
            command = "sudo su " + execUser + " -c \"" + command + "\"";
        }
        LOG.info("Run Command:{}", command);
        Map<String,String> env = new HashMap<>(4);
        LOG.trace("Add token path {} to env", System.getProperty(AuthConstraints.ENV_SERV_TOKEN_PATH));
        try {
            ProcessBuilder builder = RunShell.createProcBuilder(command, env, this.workDir);
            return builder.start();
        } catch (Exception e) {
            LOG.error("Execute DataX occurred error: "+ e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource getResource() {
        String maxMemory = getTaskConfig().getString(JOB_ADVANCE_MAX_MEMORY, DEFAULT_JVM_HEAP_MEMORY).toLowerCase();
        return new Resource(getTaskId(), 0,
                MemUtils.convertToByte(Long.parseLong(maxMemory.substring(0, maxMemory.length() - 1)),
                        maxMemory.substring(maxMemory.length() - 1)));
    }

    @Override
    protected void clean(File workDir) {
        super.clean(workDir);
        //Finally change the allocate status
        alloc = false;
    }

    @Override
    public String toString() {
        return "DataxTaskProcess{" +
                "jobId=" + getJobId() +
                ", taskId=" + getTaskId() +
                ", loggerDir='" + loggerDir + '\'' +
                ", taskConfig='" + getTaskConfig()  + '\'' +
                '}';
    }

    /**
     * Get execute method
     * @return
     */
    private Method getMethod(){
        String method = getExecConfig().getEngineDataXMethod();
        Method methodType = null;
        try{
            methodType = Method.valueOf(StringUtils.isNotBlank(method)? method : "java");
        }catch(Exception e){
            //Ignore
        }
        if(null == methodType){
            methodType = Method.JAVA;
        }
        return methodType;
    }
}
