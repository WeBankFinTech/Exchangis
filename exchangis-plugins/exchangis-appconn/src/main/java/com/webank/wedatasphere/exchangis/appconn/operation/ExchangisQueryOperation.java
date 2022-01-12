package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisOpenRequestRef;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisOpenResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
            String jobId = ((Map<String,Object>)((Map<String,Object>)exchangisOpenRequestRef.getJobContent().get("data")).get("result")).get("id").toString();
            String baseUrl = exchangisOpenRequestRef.getParameter("redirectUrl").toString() + "/";
            String jumpUrl = baseUrl;
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

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
