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

package com.webank.wedatasphere.exchangis.executor.task.process.sqoop;

import com.webank.wedatasphere.exchangis.common.util.MemUtils;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.JobContainer;
import com.webank.wedatasphere.exchangis.executor.daemons.LoggerDaemon;
import com.webank.wedatasphere.exchangis.executor.resource.Resource;
import com.webank.wedatasphere.exchangis.executor.task.process.AbstractJavaInternalTaskProcess;
import com.webank.wedatasphere.exchangis.executor.util.RunShell;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author davidhua
 * 2019/12/11
 */
public class SqoopTaskProcess extends AbstractJavaInternalTaskProcess implements LoggerDaemon.Parser {

    private static final Logger LOG = LoggerFactory.getLogger(SqoopTaskProcess.class);
    private static final String SETTINGS_MAX_MEMORY = "settings.mMemory";
    private static final String JOB_SUBMIT_PREFIX = "Submitting tokens for job:";
    private static final Pattern JOB_ID_PATTERN = Pattern.compile(JOB_SUBMIT_PREFIX + "\\s+?([\\S]+)");

    private String clusterJobId;

    public SqoopTaskProcess(long jobId, long taskId, String execUser,
                     TaskConfiguration taskConfig, ExecutorConfiguration execConfig,
                     Map<String, Object> engineParams) {
        super(jobId, taskId, execUser, taskConfig, execConfig, engineParams);
    }

    @Override
    protected void initInternal() {
        //do nothing
    }

    @Override
    protected Process executeInternal() {
        String execUser = getExecUser();
        String maxMemory = getTaskConfig().getString(SETTINGS_MAX_MEMORY, "1g");
        //unit conversion
        long headSizeM = MemUtils.convertToMB(Integer.parseInt(maxMemory.substring(0, maxMemory.length()-1)),
                maxMemory.substring(maxMemory.length() - 1));
        TaskConfiguration taskConfig = getTaskConfig();
        String sqoopCmd = getExecConfig().getEngineSqoopShell() + " " + taskConfig.getString(SqoopTaskConfigBuilder.CONFIG_COMMAND);
        String command = StringUtils.join(new String[]{
                "echo $$ > " + pidFileName(), "&&",
                "echo " + mask(sqoopCmd) + "", "&&",
                sqoopCmd , "2>&1", "&&",
                "rm " + pidFileName()

        }, " ");
        if(needToSwitchUser(execUser)){
            command = "sudo su " + execUser + " -c \"" + command +"\"";
        }
        LOG.info("Run command:{}", command);
        Map<String, String> env = new HashMap<>(1);
        //hadoop config
        env.put("HADOOP_CLIENT_OPTS", "-Xmx" + headSizeM + "m");
        env.put("HADOOP_HEAPSIZE", String.valueOf(headSizeM));
        try{
            ProcessBuilder builder = RunShell.createProcBuilder(command ,env, this.workDir);
            return builder.start();
        }catch(Exception e){
            LOG.error("Execute Sqoop occurred error: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource getResource() {
        String maxMemory = getTaskConfig().getString(SETTINGS_MAX_MEMORY, "1g");
        return new Resource(getTaskId(), 0,
                MemUtils.convertToByte(Long.parseLong(maxMemory.substring(0, maxMemory.length() - 1)),
                        maxMemory.substring(maxMemory.length() - 1)));
    }

    @Override
    protected String logFileName() {
        return JobContainer.LOG_STDOUT_NAME;
    }

    @Override
    public boolean acceptLogger(String loggerName){
        return loggerName.equals(JobContainer.LOG_STDOUT_NAME);
    }

    @Override
    public void parseLoggerOutput(String output) {
        if(StringUtils.isBlank(clusterJobId) && output.contains(JOB_SUBMIT_PREFIX)){
            Matcher matcher = JOB_ID_PATTERN.matcher(output);
            if(matcher.find()){
                this.clusterJobId = matcher.group(1);
            }
        }
    }

    @Override
    protected void killProcess(int pid) throws IOException, InterruptedException {
        super.killProcess(pid);
        if(StringUtils.isNotBlank(this.clusterJobId)){
            RunShell shell = new RunShell("hadoop job -kill " +
                    this.clusterJobId);
            shell.run();
        }
    }

    @Override
    public String toString() {
        return "DataxTaskProcess{" +
                "jobId=" + getJobId() +
                ", taskId=" + getTaskId() +
                ", taskConfig='" + getTaskConfig()  + '\'' +
                '}';
    }

    private static String mask(String command){
        String[] commandsSpliced = command.split("\\s+");
        for(int i = 0; i < commandsSpliced.length; i++){
            if(commandsSpliced[i].contains("password") && i + 1 < commandsSpliced.length){
                commandsSpliced[i + 1] = "";
            }
        }
        return StringUtils.join(commandsSpliced, " ");
    }

}
