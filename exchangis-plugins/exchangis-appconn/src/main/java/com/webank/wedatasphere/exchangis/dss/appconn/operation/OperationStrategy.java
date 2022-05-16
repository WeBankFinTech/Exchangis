package com.webank.wedatasphere.exchangis.dss.appconn.operation;


import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;


public interface OperationStrategy {

    ResponseRef createRef(NodeRequestRef requestRef,
                          String baseUrl,
                          DevelopmentService developmentService,
                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    void deleteRef(String baseUrl,
                   NodeRequestRef visualisDeleteRequestRef,
                   SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    ResponseRef executeRef(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    String getId(AsyncExecutionRequestRef requestRef);

    String submit(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, DevelopmentService developmentService) throws ExternalOperationFailedException;

    RefExecutionState state(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException;

    ResponseRef getAsyncResult(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException;

}
