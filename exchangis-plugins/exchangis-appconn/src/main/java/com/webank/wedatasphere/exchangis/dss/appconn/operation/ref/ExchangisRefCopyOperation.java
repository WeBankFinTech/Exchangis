package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCopyRequestRef;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCopyResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisOpenResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import org.apache.http.HttpRequest;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/4/24 10:55
 */
public class ExchangisRefCopyOperation extends AbstractExchangisRefOperation implements RefCopyOperation<ExchangisCopyRequestRef> {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisRefCopyOperation.class);

    private DevelopmentService developmentService;

    private SSORequestOperation<HttpAction, HttpRequest> ssoRequestOperation;

    public ExchangisRefCopyOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(getAppName());
        setSSORequestService(this.developmentService);
    }

    @Override
    public ResponseRef copyRef(ExchangisCopyRequestRef exchangisCopyRequestRef) throws ExternalOperationFailedException {
        String url = developmentService.getAppInstance().getBaseUrl() + "api/rest_j" + ServerConfiguration.BDP_SERVER_VERSION() + "/dss/exchangis/main/appJob" + "/copy";
        ExchangisEntityPostAction exchangisEntityPostAction = new ExchangisEntityPostAction();
        exchangisEntityPostAction.setUser(exchangisCopyRequestRef.getParameter("user").toString());
        exchangisEntityPostAction.addRequestPayload("projectId", exchangisCopyRequestRef.getParameter("projectId"));
        exchangisEntityPostAction.addRequestPayload("partial", true);
        exchangisEntityPostAction.addRequestPayload("projectVersion", "v1");
        exchangisEntityPostAction.addRequestPayload("flowVersion", exchangisCopyRequestRef.getParameter("orcVersion"));
        exchangisEntityPostAction.addRequestPayload("contextID", exchangisCopyRequestRef.getParameter("contextId").toString());
        String nodeType = exchangisCopyRequestRef.getParameter("nodeType").toString();
        String externalContent = null;
        Map<String, Object> jobContent = (Map<String, Object>) exchangisCopyRequestRef.getParameter("jobContent");

        try {
            LOG.info("jobConten: {}", exchangisCopyRequestRef.getParameter("jobContent"));
            externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(exchangisCopyRequestRef.getParameter("jobContent"));
            if (Constraints.NODE_TYPE_SQOOP.equals(nodeType)) {
                ExchangisOpenResponseRef exchangisOpenResponseRef = new ExchangisOpenResponseRef(externalContent, 0);
                exchangisEntityPostAction.addRequestPayload("sqoopIds",((Double) Double.parseDouble(exchangisOpenResponseRef.getSqoopId())).longValue());
                LOG.info("sqoopIds: {}", exchangisEntityPostAction.getRequestPayload());
            } else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(nodeType)) {
                ExchangisOpenResponseRef exchangisOpenResponseRef = new ExchangisOpenResponseRef(externalContent, 0);
                exchangisEntityPostAction.addRequestPayload("dataXIds", ((Double) Double.parseDouble(exchangisOpenResponseRef.getDataxId())).longValue());
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + exchangisCopyRequestRef.getType(), null);
            }
        } catch (JsonProcessingException e) {
            LOG.error("Failed to create copy request", e);
        }

        SSOUrlBuilderOperation ssoUrlBuilderOperation = exchangisCopyRequestRef.getWorkspace().getSSOUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(exchangisCopyRequestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef = null;
        try {
            LOG.info("getBuiltUrl: {}", ssoUrlBuilderOperation.getBuiltUrl());
            LOG.info("postAction: {}", exchangisEntityPostAction);
            exchangisEntityPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisEntityPostAction);
            Map<String, Object> responseMap = SSOIntegrationConf.gson().fromJson(httpResult.getResponseBody(), Map.class);
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
            Map<String, Object> sqoopData = (Map<String, Object>) dataMap.get("sqoop");
            jobContent.put("sqoopIds", Double.parseDouble(sqoopData.get(jobContent.get("id").toString()).toString()));
            responseRef = new ExchangisCopyResponseRef( jobContent, httpResult.getResponseBody());
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "Copy Exchangis Exception", e);
        }

        return responseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
