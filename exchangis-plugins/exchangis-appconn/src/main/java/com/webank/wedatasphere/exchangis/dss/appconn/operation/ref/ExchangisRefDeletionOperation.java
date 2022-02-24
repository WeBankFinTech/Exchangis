package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisDeleteAction;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Ref delete operation
 */
public class ExchangisRefDeletionOperation extends AbstractExchangisRefOperation implements RefDeletionOperation {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefDeletionOperation.class);

    DevelopmentService developmentService;

    public ExchangisRefDeletionOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        setSSORequestService(developmentService);
    }

    @Override
    public void deleteRef(RequestRef deleteRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef nodeRequestRef = (NodeRequestRef) deleteRequestRef;
        deleteJob(nodeRequestRef);
    }

    private void deleteJob(NodeRequestRef nodeRequestRef) throws ExternalOperationFailedException{
        Long id = AppConnUtils.resolveParam(nodeRequestRef.getJobContent(), Constraints.REF_JOB_ID, Long.class);
        LOG.info("delete job request => id: {}, jobContext:{}",
                id, nodeRequestRef.getJobContent().toString());
        String url = requestURL("/job/" + id);
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, nodeRequestRef.getWorkspace(), nodeRequestRef,
                (requestRef) ->{
                    // Build ref delete action
                    return new ExchangisDeleteAction(requestRef.getUserName());
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        LOG.info("delete job response => status: {}, url:{}", entity.getResult().getStatusCode(), url);
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
