package com.webank.wedatasphere.exchangis.appconn.config;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;

public class ExchangisConfig {

    public final static String BASEURL = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis";

    public final static String EXCHANGIS_APPCONN_NAME = CommonVars.apply("wds.dss.appconn.exchangis.name", "Exchangis").getValue();
    public final static String NODE_TYPE_SQOOP = CommonVars.apply("wds.dss.appconn.exchangis.sqoop", "wds.dss.appconn.exchangis.sqoop").getValue();
    public final static String NODE_TYPE_DATAX = CommonVars.apply("wds.dss.appconn.exchangis.datax", "wds.dss.appconn.exchangis.datax").getValue();
    public final static String ENGINE_TYPE_DATAX_NAME = CommonVars.apply("wds.dss.appconn.exchangis.datax.name", "DATAX").getValue();
    public final static String ENGINE_TYPE_SQOOP_NAME = CommonVars.apply("wds.dss.appconn.exchangis.datax.name", "SQOOP").getValue();
    public final static String JOB_TYPE_OFFLINE = CommonVars.apply("wds.dss.appconn.exchangis.job.type.offline", "OFFLINE").getValue();
    public final static String JOB_TYPE_STREAM = CommonVars.apply("wds.dss.appconn.exchangis.job.type.stream", "STREAM").getValue();

    public final static String ID = "id";
    public final static String WORKSPACE_NAME = "workspaceName";
    public final static String PROJECT_NAME = "projectName";
    public final static String DESCRIPTION = "description";
    public final static String TAGS = "tags";
    public final static String EDIT_USERS = "editUsers";
    public final static String VIEW_USERS = "viewUsers";
    public final static String EXEC_USERS = "execUsers";

    public final static String PROJECT_ID = "projectId";
    public final static String ENGINE_TYPE = "engineType";
    public final static String JOB_DESC = "jobDesc";
    public final static String JOB_LABELS = "jobLabels";
    public final static String JOB_NAME = "jobName";
    public final static String JOB_TYPE="jobType";

}

