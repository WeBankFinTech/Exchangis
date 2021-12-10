package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisGetAction;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCommonResponseRef;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisOpenRequestRef;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisOpenResponseRef;
import com.webank.wedatasphere.exchangis.appconn.utils.AppconnUtils;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExchangisQueryOperation implements RefQueryOperation<OpenRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisQueryOperation.class);


    private DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;

    public ExchangisQueryOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
    }
    @Override
    public ResponseRef query(OpenRequestRef openRequestRef) throws ExternalOperationFailedException {
        ExchangisOpenRequestRef exchangisOpenRequestRef = (ExchangisOpenRequestRef) openRequestRef;

        logger.info("query job=>jobContent:{} ||,projectId:{}  ||,projectName:{}  ||,parameters:{} ||,type:{}",exchangisOpenRequestRef.getJobContent(),exchangisOpenRequestRef.getProjectId(),exchangisOpenRequestRef.getProjectName(),exchangisOpenRequestRef.getParameters().toString(),exchangisOpenRequestRef.getType());
        try {
            ExchangisGetAction exchangisGetAction = new ExchangisGetAction();

            String name = exchangisOpenRequestRef.getName();
            String type = openRequestRef.getType();
            String jobType = ExchangisConfig.JOB_TYPE_OFFLINE.toString();
            Map<String, Object> contextVal = null;
            try {
//                String contextID = exchangisOpenRequestRef.getJobContent().get("contextID").toString();
//                Map contextIDMap =  BDPJettyServerHelper.jacksonJson().readValue(contextID, Map.class);
//                contextVal = (Map<String, Object>) contextIDMap.get("value");
//                logger.info("contextVal {}",contextVal.toString());
            }catch (Exception e){
                throw new ExternalOperationFailedException(31023, "Get node Id failed!", e);
            }

           // logger.info("exchangisCommonResponseRef nodeId {}",nodeId);
            String jobUrl=getBaseUrl()+"/dss/";

            SSOUrlBuilderOperation ssoUrlBuilderOperation = exchangisOpenRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
            ssoUrlBuilderOperation.setReqUrl(jobUrl);
            ssoUrlBuilderOperation.setWorkspace(exchangisOpenRequestRef.getWorkspace().getWorkspaceName());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisGetAction);
            ExchangisCommonResponseRef exchangisCommonResponseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
            logger.info("exchangisCommonResponseRef map {}",exchangisCommonResponseRef.getResponseBody());
            if(exchangisCommonResponseRef.isFailed()){
                logger.error(exchangisCommonResponseRef.getResponseBody());
                throw new ExternalOperationFailedException(31022,exchangisCommonResponseRef.getErrorMsg());
            }


            String jobId = exchangisCommonResponseRef.toMap().get("id").toString();


            String baseUrl = exchangisOpenRequestRef.getParameter("redirectUrl").toString();
            String jumpUrl = baseUrl;
            logger.info("exchangisCommonResponseRef jobId {},baseUrl {}",jobId,baseUrl);
            if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisOpenRequestRef.getType())){
                jumpUrl +=ExchangisConfig.SQOOP_JUMP_URL_FORMAT;
            }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisOpenRequestRef.getType())){
                jumpUrl += ExchangisConfig.DATAX_JUMP_URL_FORMAT;
            }
            jumpUrl +="?id="+jobId;
            Map<String,String> retMap = new HashMap<>();
            logger.info("exchangisCommonResponseRef jumpUrl {}",jumpUrl);
            retMap.put("jumpUrl",jumpUrl);
            return new ExchangisOpenResponseRef(DSSCommonUtils.COMMON_GSON.toJson(retMap),0);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(31022, "Failed to parse jobContent ", e);
        }
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl() + ExchangisConfig.BASEURL;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
