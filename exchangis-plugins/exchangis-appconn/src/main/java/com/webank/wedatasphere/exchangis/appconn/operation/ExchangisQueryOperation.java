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

        logger.info("query job=> name {},nodeType {},jobContent {}",exchangisOpenRequestRef.getName(),exchangisOpenRequestRef.getType(),exchangisOpenRequestRef.getJobContent());

        try {
            ExchangisGetAction exchangisGetAction = new ExchangisGetAction();


            String nodeId = AppconnUtils.getJobContent((Map<String, Object>)exchangisOpenRequestRef.getJobContent());

            String jobUrl=getBaseUrl()+"/dss/"+nodeId;

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
            logger.info("exchangisCommonResponseRef jobId {}",jobId);

            String baseUrl = exchangisOpenRequestRef.getParameter("redirectUrl").toString();
            String jumpUrl = baseUrl;
            if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisOpenRequestRef.getType())){
                jumpUrl = ExchangisConfig.getUrl(ExchangisConfig.BASEURL,ExchangisConfig.SQOOP_JUMP_URL_FORMAT);
            }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisOpenRequestRef.getType())){
                jumpUrl = ExchangisConfig.getUrl(ExchangisConfig.BASEURL,ExchangisConfig.DATAX_JUMP_URL_FORMAT);
            }
            jumpUrl +="?id="+jobId;
            Map<String,String> retMap = new HashMap<>();
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
