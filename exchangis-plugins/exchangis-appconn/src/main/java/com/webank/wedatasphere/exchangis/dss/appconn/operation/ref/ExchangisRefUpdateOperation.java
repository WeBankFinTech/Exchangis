package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCommonResponseDef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPutAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisPutAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.AbstractExchangisResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.entity.RefJobReqEntity;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Ref update operation
 */
public class ExchangisRefUpdateOperation extends AbstractExchangisRefOperation implements RefUpdateOperation<UpdateRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefUpdateOperation.class);

    DevelopmentService developmentService;

    public ExchangisRefUpdateOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        setSSORequestService(developmentService);
    }

    @Override
    public ResponseRef updateRef(UpdateRequestRef updateRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef nodeRequestRef = (NodeRequestRef) updateRequestRef;
        ResponseRef responseRef = null;
        if(Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase(nodeRequestRef.getNodeType())){
            responseRef = updateOffLineRequest(nodeRequestRef, Constraints.ENGINE_TYPE_SQOOP_NAME);
        }else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(nodeRequestRef.getNodeType())){
            responseRef = updateOffLineRequest(nodeRequestRef, Constraints.NODE_TYPE_DATAX);
        }
        return responseRef;
    }

    private ResponseRef updateOffLineRequest(NodeRequestRef nodeRequestRef, String engineType) throws ExternalOperationFailedException{
        Integer id = AppConnUtils.resolveParam(nodeRequestRef.getJobContent(), Constraints.REF_JOB_ID, Integer.class);
        LOG.info("update {} job request => id: {}, jobContent:{}", id, engineType, nodeRequestRef.getJobContent());
        String url = requestURL("/job/" + id);
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, nodeRequestRef.getWorkspace(), nodeRequestRef,
                (requestRef) -> {
                    // Build ref update action
                    RefJobReqEntity jobReqEntity = getRefJobReqEntity(requestRef, engineType);
                    jobReqEntity.setId(Long.valueOf(id));
                    return new ExchangisEntityPutAction<>(jobReqEntity, requestRef.getUserName());
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("update {} job response => id: {}, status: {}, response: {}", engineType, id, httpResult.getStatusCode(),
                httpResult.getResponseBody());
        return new ExchangisCommonResponseDef(httpResult);
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
