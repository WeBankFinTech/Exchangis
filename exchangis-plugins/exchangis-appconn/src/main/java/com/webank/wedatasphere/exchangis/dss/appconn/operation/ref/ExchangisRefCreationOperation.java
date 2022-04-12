package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCommonResponseDef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;


/**
 * Ref creation operation
 */
public class ExchangisRefCreationOperation extends AbstractExchangisRefOperation implements RefCreationOperation<CreateRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefCreationOperation.class);

    DevelopmentService developmentService;

    public ExchangisRefCreationOperation(DevelopmentService developmentService){
        super(new String[]{"appJob/create"});
        this.developmentService = developmentService;
        setSSORequestService(developmentService);
    }

    @Override
    public ResponseRef createRef(CreateRequestRef createRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef exchangisCreateRequestRef = (NodeRequestRef) createRequestRef;
        exchangisCreateRequestRef.getProjectName();
        ResponseRef responseRef = null;
        if(Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisCreateRequestRef.getNodeType())){
             responseRef = sendOffLineRequest(exchangisCreateRequestRef, Constraints.ENGINE_TYPE_SQOOP_NAME);
            LOG.info("responseRef: {}", responseRef.toMap());
        }else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisCreateRequestRef.getNodeType())){
            responseRef = sendOffLineRequest(exchangisCreateRequestRef, Constraints.ENGINE_TYPE_DATAX_NAME);
        }
        return responseRef;
    }

    private ResponseRef sendOffLineRequest(NodeRequestRef nodeRequestRef, String engineType)  throws ExternalOperationFailedException {
        LOG.info("create {} job request => jobContent:{}, projectId:{}, projectName:{}, parameters:{}, type:{}",
                engineType, nodeRequestRef.getJobContent(), nodeRequestRef.getProjectId(), nodeRequestRef.getProjectName(),
                nodeRequestRef.getParameters().toString(), nodeRequestRef.getType());
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(nodeRequestRef.getWorkspace(), nodeRequestRef,
                (requestRef) -> {
                    // Build ref creation action
                    return new ExchangisEntityPostAction<>(getRefJobReqEntity(requestRef, engineType), requestRef.getUserName());
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("create {} job response => status: {}, response: {}", engineType, httpResult.getStatusCode(), httpResult.getResponseBody());
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
