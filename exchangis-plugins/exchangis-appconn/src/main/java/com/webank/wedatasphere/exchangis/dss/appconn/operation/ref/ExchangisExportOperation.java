package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisExportResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisOpenResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ref export operation
 */
public class ExchangisExportOperation extends AbstractExchangisRefOperation implements RefExportOperation<ExportRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisExportOperation.class);

    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public ExchangisExportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(getAppName());
        setSSORequestService(this.developmentService);
    }


    @Override
    public ResponseRef exportRef(ExportRequestRef exportRequestRef) throws ExternalOperationFailedException {
        String url = developmentService.getAppInstance().getBaseUrl() + "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis/project" + "/export";
        ExchangisEntityPostAction exchangisEntityPostAction = new ExchangisEntityPostAction();
        exchangisEntityPostAction.setUser(exportRequestRef.getParameter("user").toString());
        exchangisEntityPostAction.addRequestPayload("projectId", exportRequestRef.getParameter("projectId"));
        exchangisEntityPostAction.addRequestPayload("partial", true);
        String nodeType = exportRequestRef.getParameter("nodeType").toString();
        String externalContent = null;
        try {
            externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(exportRequestRef.getParameter("jobContent"));
            if(Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase(nodeType)) {
                ExchangisOpenResponseRef exchangisOpenResponseRef = new ExchangisOpenResponseRef(externalContent, 0);
                exchangisEntityPostAction.addRequestPayload("sqoopIds", ((Double) Double.parseDouble(exchangisOpenResponseRef.getSqoopId())).longValue());
            } else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(nodeType)) {
                ExchangisOpenResponseRef exchangisOpenResponseRef = new ExchangisOpenResponseRef(externalContent, 0);
                exchangisEntityPostAction.addRequestPayload("dataXIds", ((Double) Double.parseDouble(exchangisOpenResponseRef.getSqoopId())).longValue());
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + exportRequestRef.getType(), null);
            }
        } catch (Exception e) {
            LOG.error("Failed to create export request", e);
        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = exportRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(exportRequestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef;
        try{
            exchangisEntityPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisEntityPostAction);
            responseRef = new ExchangisExportResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90176, "Export Exchangis Exception", e);
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
