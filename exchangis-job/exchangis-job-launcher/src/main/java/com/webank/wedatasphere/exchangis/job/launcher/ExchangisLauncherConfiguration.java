package com.webank.wedatasphere.exchangis.job.launcher;


import org.apache.linkis.common.conf.CommonVars;

// TODO Unified management of the linkis configuration
public class ExchangisLauncherConfiguration {

    public static final String TASK_NOT_EXIST = "Not exists EngineConn";

    public static final String LAUNCHER_LINKIS_RUNTIME_PARAM_NAME = "runtimeParams";

    public static final String LAUNCHER_LINKIS_STARTUP_PARAM_NAME = "startUpParams";

    public static final String LAUNCHER_LINKIS_REQUEST_MEMORY = "wds.linkis.engineconn.java.driver.memory";

    public static final CommonVars<String> LAUNCHER_LINKIS_CREATOR = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.creator", "exchangis");

    public static final CommonVars<String> LAUNCHER_LINKIS_ENGINE_CONN_MODE = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.engineConn.mode", "once");

    public static final CommonVars<Long> LAUNCHER_LINKIS_MAX_SUBMIT = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.max.submit", 30000L);

    public static final CommonVars<Integer> LAUNCHER_LINKIS_MAX_ERROR = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.max.error", 3);

}
