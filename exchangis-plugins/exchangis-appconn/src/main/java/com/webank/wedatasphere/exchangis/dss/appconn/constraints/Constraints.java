package com.webank.wedatasphere.exchangis.dss.appconn.constraints;


import org.apache.linkis.common.conf.CommonVars;

/**
 * Constraints
 */
public class Constraints {

     // AppConn name
     public final static String EXCHANGIS_APPCONN_NAME = CommonVars.apply("wds.dss.appconn.exchangis.name", "Exchangis").getValue();

     public final static String API_REQUEST_PREFIX = CommonVars.apply("wds.dss.appconn.exchangis.api.request-prefix", "api/rest_j/v1/dss/exchangis/main").getValue();

     public final static String DOMAIN_NAME = CommonVars.apply("wds.dss.appconn.exchangis.domain.name", "DSS").getValue();

     // Constraint in Project operation
     public final static String PROJECT_ID = "projectId";

     // Node type
     public final static String NODE_TYPE_SQOOP = CommonVars.apply("wds.dss.appconn.exchangis.node-type.sqoop", "linkis.appconn.exchangis.sqoop").getValue();
     public final static String NODE_TYPE_DATAX = CommonVars.apply("wds.dss.appconn.exchangis.node-type.datax", "linkis.appconn.exchangis.datax").getValue();

     // Engine type
     public final static String ENGINE_TYPE_DATAX_NAME = CommonVars.apply("wds.dss.appconn.exchangis.engine.datax.name", "DATAX").getValue();
     public final static String ENGINE_TYPE_SQOOP_NAME = CommonVars.apply("wds.dss.appconn.exchangis.engine.sqoop.name", "SQOOP").getValue();

     // Job type
     public final static String JOB_TYPE_OFFLINE = CommonVars.apply("wds.dss.appconn.exchangis.job-type.offline", "OFFLINE").getValue();

     public final static String REF_JOB_ID = "id";
     public final static String REF_JUMP_URL_FORMAT = CommonVars.apply("wds.dss.appconn.exchangis.ref.jump","#/childJobManagement").getValue();


}

