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

import com.webank.wedatasphere.exchangis.executor.util.LinuxPlatFormUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author davidhua
 * 2019/8/27
 */
@Component
public class ExecutorConfiguration {

    @Value("${executor.linux.sudo_user:hadoop}")
    private String linuxSudoUser = LinuxPlatFormUtil.DEFAULT_HADOOP_USER;

    @Value("${executor.heartbeat.interval-in-sec.active}")
    private Long activeHeartBeat;

    @Value("${executor.heartbeat.interval-in-sec.idle}")
    private Long idleHeartBeat;

    @Value("${executor.job.allocate.time-in-seconds:5}")
    private int waitAllocTimeInSec;

    @Value("${executor.job.logger.dir}")
    private String jobLogDir;

    @Value("${executor.job.logger.history}")
    private String jobLogHistory;

    @Value("${executor.job.logger.limit.number:10000}")
    private Long jobLogLimitNum;

    @Value("${executor.job.engine.dataX.home}")
    private String engineDataXHome;

    @Value("${executor.job.engine.dataX.python.shell}")
    private String engineDataXPythonShell;

    @Value("${executor.job.engine.dataX.python.script}")
    private String engineDataXPythonScript;

    @Value("${executor.job.engine.dataX.java.mainClass}")
    private String engineDataXJavaMainClass;

    @Value("${executor.job.engine.dataX.java.classPath}")
    private String engineDataXJavaClassPath;

    @Value("${executor.job.engine.dataX.java.logConfFile}")
    private String engineDataXJavaLogConf;

    @Value("${executor.job.engine.dataX.method}")
    private String engineDataXMethod;

    @Value("${executor.network.interface.name}")
    private String networkInterface;
    /**
     * DataX log file name
     */
    @Value("${executor.job.engine.dataX.logger-file-name}")
    private String engineDataXLogFile;

    /**
     * DataX config file name
     */
    @Value("${executor.job.engine.dataX.config-file-name}")
    private String engineDataXConfFile;

    /**
     * DataX processors source path
     */
    @Value("${executor.job.engine.dataX.proc-src-dir}")
    private String engineDataXProcSrc;

    @Value("${executor.job.engine.sqoop.shell}")
    private String engineSqoopShell;

    @Value("${executor.node.default:true}")
    private boolean nodeDefault;

    @Value("${executor.node.tab}")
    private String nodeTabs;

    public Long getActiveHeartBeat() {
        return activeHeartBeat;
    }

    public void setActiveHeartBeat(Long activeHeartBeat) {
        this.activeHeartBeat = activeHeartBeat;
    }

    public Long getIdleHeartBeat() {
        return idleHeartBeat;
    }

    public void setIdleHeartBeat(Long idleHeartBeat) {
        this.idleHeartBeat = idleHeartBeat;
    }

    public int getWaitAllocTimeInSec() {
        return waitAllocTimeInSec;
    }

    public String getJobLogDir() {
        return jobLogDir;
    }

    public String getJobLogHistory() {
        return jobLogHistory;
    }

    public String getEngineDataXHome() {
        return engineDataXHome;
    }

    public String getEngineDataXLogFile() {
        return engineDataXLogFile;
    }

    public String getEngineDataXConfFile() {
        return engineDataXConfFile;
    }

    public String getEngineDataXProcSrc() {
        return engineDataXProcSrc;
    }


    public String getNetworkInterface() {
        return networkInterface;
    }

    public String getEngineSqoopShell() {
        return engineSqoopShell;
    }

    public Long getJobLogLimitNum() {
        return jobLogLimitNum;
    }

    public String getLinuxSudoUser() {
        return linuxSudoUser;
    }

    public String getEngineDataXPythonShell() {
        return engineDataXPythonShell;
    }

    public String getEngineDataXPythonScript() {
        return engineDataXPythonScript;
    }

    public String getEngineDataXJavaMainClass() {
        return engineDataXJavaMainClass;
    }

    public String getEngineDataXJavaClassPath() {
        return engineDataXJavaClassPath;
    }

    public String getEngineDataXMethod() {
        return engineDataXMethod;
    }

    public String getEngineDataXJavaLogConf() {
        return engineDataXJavaLogConf;
    }

    public boolean isNodeDefault() {
        return nodeDefault;
    }

    public String getNodeTabs() {
        return nodeTabs;
    }
}
