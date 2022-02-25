package com.webank.wedatasphere.exchangis.job.launcher;


import org.apache.linkis.common.conf.CommonVars;

// TODO Unified management of the linkis configuration
public class ExchangisLauncherConfiguration {

    public static final String TASK_NOT_EXIST = "Not exists EngineConn";

    public static final CommonVars<String> LAUNCHER_LINKIS_CREATOR = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.creator", "exchangis");

    public static final CommonVars<String> LAUNCHER_LINKIS_ENGINE_CONN_MODE = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.engineConn.mode", "once");

    public static final CommonVars<Long> LAUNCHER_LINKIS_MAX_SUBMIT = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.max.submit", 30000L);

    public static final CommonVars<Integer> LAUNCHER_LINKIS_MAX_ERROR = CommonVars.apply("wds.exchangis.job.task.launcher.linkis.max.error", 3);

    public static final CommonVars<String> LINKIS_SERVER_URL = CommonVars.apply("wds.exchangis.client.linkis.server-url", "http://127.0.0.1:9001");

    public static final CommonVars<String> LINKIS_TOKEN_VALUE = CommonVars.apply("wds.exchangis.client.linkis.token.value", "EXCHANGIS-TOKEN");
}
