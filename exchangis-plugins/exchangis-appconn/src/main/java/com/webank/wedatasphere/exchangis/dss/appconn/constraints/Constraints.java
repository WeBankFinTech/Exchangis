package com.webank.wedatasphere.exchangis.dss.appconn.constraints;


import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.server.conf.ServerConfiguration;

public class Constraints {

     public final static String DOMAIN_NAME = CommonVars.apply("wds.dss.appconn.exchangis.domain.name", "DSS").getValue();

     // Constraint in Project operation
     public final static String PROJECT_ID = "projectId";

     public final static String BASEURL = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis";
     public final static String SQOOP_JUMP_URL_FORMAT="dss/exchangis/#/childJobManagement";
     public final static String DATAX_JUMP_URL_FORMAT="dss/exchangis/#/childJobManagement";

     public final static String EXCHANGIS_APPCONN_NAME = CommonVars.apply("wds.dss.appconn.exchangis.name", "Exchangis").getValue();
     public final static String NODE_TYPE_SQOOP = CommonVars.apply("linkis.appconn.exchangis.sqoop", "linkis.appconn.exchangis.sqoop").getValue();
     public final static String NODE_TYPE_DATAX = CommonVars.apply("linkis.appconn.exchangis.datax", "linkis.appconn.exchangis.datax").getValue();
     public final static String ENGINE_TYPE_DATAX_NAME = CommonVars.apply("wds.dss.appconn.exchangis.datax.name", "DATAX").getValue();
     public final static String ENGINE_TYPE_SQOOP_NAME = CommonVars.apply("wds.dss.appconn.exchangis.datax.name", "SQOOP").getValue();
     public final static String JOB_TYPE_OFFLINE = CommonVars.apply("wds.dss.appconn.exchangis.job.type.offline", "OFFLINE").getValue();
     public final static String JOB_TYPE_STREAM = CommonVars.apply("wds.dss.appconn.exchangis.job.type.stream", "STREAM").getValue();

     public final static String ID = "id";
     public final static String WORKSPACE_NAME = "workspaceName";
     public final static String PROJECT_NAME = "projectName";
     public final static String DSS_PROJECT_ID="dssProjectId";
     public final static String DSS_PROJECT_NAME="dssProjectName";
     public final static String DESCRIPTION = "description";
     public final static String TAGS = "tags";
     public final static String EDIT_USERS = "editUsers";
     public final static String VIEW_USERS = "viewUsers";
     public final static String EXEC_USERS = "execUsers";

     public final static String NODE_ID="nodeId";
     public final static String NODE_NAME="nodeName";
     public final static String ENGINE_TYPE = "engineType";
     public final static String JOB_DESC = "jobDesc";
     public final static String JOB_LABELS = "jobLabels";
     public final static String JOB_NAME = "jobName";
     public final static String JOB_TYPE="jobType";


     public static String getUrl(String baseUrl, String format, String entityId){
          return baseUrl + String.format(format, entityId);
     }

     public static String getUrl(String baseUrl, String format, String... ids){
          return baseUrl + String.format(format, ids);
     }
}

