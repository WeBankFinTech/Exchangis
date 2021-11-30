package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisOpenRequestRef;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExchangisQueryOperation implements RefQueryOperation<OpenRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisQueryOperation.class);

    DevelopmentService developmentService;

    @Override
    public ResponseRef query(OpenRequestRef openRequestRef) throws ExternalOperationFailedException {
        ExchangisOpenRequestRef exchangisOpenRequestRef = (ExchangisOpenRequestRef) openRequestRef;
        try {
            String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(exchangisOpenRequestRef.getJobContent());
            Long projectId = (Long) exchangisOpenRequestRef.getParameter("projectId");
            String baseUrl = exchangisOpenRequestRef.getParameter("redirectUrl").toString();
            String jumpUrl = baseUrl;

            if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisOpenRequestRef.getType())){
                jumpUrl="";
            }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisOpenRequestRef.getType())){
                jumpUrl="";
            }

            String retJumpUrl = getEnvUrl(jumpUrl, exchangisOpenRequestRef);
            Map<String,String> retMap = new HashMap<>();
            retMap.put("jumpUrl",retJumpUrl);
            return null;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(31022, "Failed to parse jobContent ", e);
        }
    }

    public String getEnvUrl(String url, ExchangisOpenRequestRef exchangisOpenRequestRef ){
        String env = ((Map<String, Object>) exchangisOpenRequestRef.getParameter("params")).get(DSSCommonUtils.DSS_LABELS_KEY).toString();
        return url + "?env=" + env.toLowerCase();
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
