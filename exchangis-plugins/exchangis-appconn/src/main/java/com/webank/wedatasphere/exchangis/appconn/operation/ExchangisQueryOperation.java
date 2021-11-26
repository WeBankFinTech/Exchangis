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

import java.util.Map;

public class ExchangisQueryOperation implements RefQueryOperation<OpenRequestRef> {

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

            }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisOpenRequestRef.getType())){

            }
//            if("linkis.appconn.visualis.widget".equalsIgnoreCase(exchangisOpenRequestRef.getType())){
//                VisualisCommonResponseRef widgetCreateResponseRef = new VisualisCommonResponseRef(externalContent);
//
//            } else if("linkis.appconn.visualis.display".equalsIgnoreCase(visualisOpenRequestRef.getType())){
//                VisualisCommonResponseRef displayCreateResponseRef = new VisualisCommonResponseRef(externalContent);
//                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.DISPLAY_JUMP_URL_FORMAT, projectId.toString(), displayCreateResponseRef.getDisplayId());
//            }else if("linkis.appconn.visualis.dashboard".equalsIgnoreCase(visualisOpenRequestRef.getType())){
//                VisualisCommonResponseRef dashboardCreateResponseRef = new VisualisCommonResponseRef(externalContent);
//                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_JUMP_URL_FORMAT, projectId.toString(), dashboardCreateResponseRef.getDashboardId(), visualisOpenRequestRef.getName());
//            } else {
//                throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisOpenRequestRef.getType(), null);
//            }
//            String retJumpUrl = getEnvUrl(jumpUrl, visualisOpenRequestRef);
//            Map<String,String> retMap = new HashMap<>();
//            retMap.put("jumpUrl",retJumpUrl);
            return null;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }
//
//    public String getEnvUrl(String url, VisualisOpenRequestRef visualisOpenRequestRef ){
//        String env = ((Map<String, Object>) visualisOpenRequestRef.getParameter("params")).get(DSSCommonUtils.DSS_LABELS_KEY).toString();
//        return url + "?env=" + env.toLowerCase();
//    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
