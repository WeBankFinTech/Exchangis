package com.webank.wedatasphere.exchangis.dss.appconn.operation.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.enums.ExchangisStatusEnum;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.OperationStrategy;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.AbstractExchangisRefOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCommonResponseDef;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCompletedExecutionResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisOpenResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.HttpExtAction;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.NumberUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ExchangisOptStrategy extends AbstractExchangisRefOperation implements OperationStrategy {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisOptStrategy.class);


    private String getNodeNameByKey(String key, String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        List<DSSNode> dwsNodes = DSSCommonUtils.COMMON_GSON.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        return dwsNodes.stream().filter(n -> key.equals(n.getId())).map(DSSNode::getName).findFirst().orElse("");
    }

    @Override
    public ResponseRef createRef(NodeRequestRef requestRef, String baseUrl, DevelopmentService developmentService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void deleteRef(String baseUrl, NodeRequestRef visualisDeleteRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {

    }

    @Override
    public ResponseRef executeRef(AsyncExecutionRequestRef nodeRequestRef, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {

        //AsyncExecutionRequestRef nodeRequestRef = (AsyncExecutionRequestRef) executionRequestRef;

        String executionRequestRefJson = null;
        String parameter = null;
        String workspace = null;
        try {
            parameter = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef.getParameters());
            workspace = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef.getWorkspace());
            logger.info("parameter =>  parameter: {}", parameter);
            logger.info("workspace =>  workspace: {}", workspace);
            executionRequestRefJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef);
        } catch (JsonProcessingException e) {
            logger.error("Parser request happen error8989");
        }

        logger.info("executionRequestRef =>  executionRequestRef: {}", nodeRequestRef);
        logger.info("executionRequestRefJson =>  executionRequestRefJson323432: {}", executionRequestRefJson);
        try {
            nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString();
            logger.info("getExecutionRequestRefContext User: {}", nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
        } catch (Exception e) {
            logger.error("parser request error", e);
        }
        Long id = AppConnUtils.resolveParam(nodeRequestRef.getJobContent(), Constraints.REF_JOB_ID, Double.class).longValue();
        logger.info("execute job request =>  id: {}, name: {}, user: {}, jobContent: {}",
                id, nodeRequestRef.getName(), nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString(),
                nodeRequestRef.getJobContent().toString());
        String originLabels = nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("labels").toString();
        String realLabels = "";
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(originLabels, Map.class);
            realLabels = responseMap.get("route").toString();
        } catch (JsonProcessingException e) {
            logger.error("parser request error", e);
        }
        String url = requestURL("appJob/execute/" + id);
        String finalRealLabels = realLabels;
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, nodeRequestRef.getWorkspace(), nodeRequestRef,
                (requestRef) -> {
                    // Build ref execution action

                    ExchangisEntityPostAction exchangisEntityPostAction = new ExchangisEntityPostAction();
                    exchangisEntityPostAction.setUser(nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
                    HashMap<String, String> labels = new HashMap<>();
                    labels.put("route", finalRealLabels);
                    exchangisEntityPostAction.addRequestPayload("labels", labels);
                    return exchangisEntityPostAction;

                    /*return new ExchangisEntityPostAction<>(null,
                            nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());*/
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        logger.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        return new ExchangisCommonResponseDef(httpResult);
    }

    @Override
    public String getId(AsyncExecutionRequestRef requestRef) {
        try {
            String executionContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getJobContent());
            ExchangisOpenResponseRef exchangisOpenResponseRef = new ExchangisOpenResponseRef(executionContent, 0);
            return NumberUtils.parseDoubleString(exchangisOpenResponseRef.getSqoopId());
        } catch (Exception e) {
            logger.error("failed to parse jobContent when execute widget node", e);
        }
        return null;
    }

    @Override
    public String submit(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, DevelopmentService developmentService) throws ExternalOperationFailedException {
        String url = baseUrl + "api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/dss/exchangis/main/appJob" + "/execute/" + getId(ref);
        ref.getExecutionRequestRefContext().appendLog("dss execute sqoop node,ready to submit from " + url);
        ExchangisEntityPostAction exchangisEntityPostAction = new ExchangisEntityPostAction();
        exchangisEntityPostAction.setUser(ref.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
        String originLabels = ref.getExecutionRequestRefContext().getRuntimeMap().get("labels").toString();
        setSSORequestService(developmentService);
        /*String realLabels = "";
        try {
            logger.info("originLabels: {}", originLabels);
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(originLabels, Map.class);
            realLabels = responseMap.get("route").toString();
        } catch (JsonProcessingException e) {
            logger.error("parser request error", e);
        }*/
        HashMap<String, String> labels = new HashMap<>();
        labels.put("route", originLabels);
        exchangisEntityPostAction.addRequestPayload("labels", labels);

        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, ref.getWorkspace(), ref,
                (requestRef) -> {
                    // Build ref execution action
                    return exchangisEntityPostAction;
                    /*return new ExchangisEntityPostAction<>(null,
                            nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());*/
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        logger.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        Map<String, Object> responseMap = BDPJettyServerHelper.gson().fromJson(httpResult.getResponseBody(), Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        return Optional.of(dataMap.get("jobExecutionId").toString()).orElseThrow(() -> new ExternalOperationFailedException(90176, "Get execute Id failed"));
    }

    @Override
    public RefExecutionState state(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(execId)) {
            ref.getExecutionRequestRefContext().appendLog("dss execute sqoop error for execId is null when get state!");
            throw new ExternalOperationFailedException(90176, "dss execute sqoop error when get state");
        }
        String url = baseUrl + "api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/dss/exchangis/main/job" + "/execution/" + getId(ref) +"/state";
        ref.getExecutionRequestRefContext().appendLog("dss execute sqoop node,ready to submit from " + url);

        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        exchangisPostAction.setUrl(ref.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());

        try {
            ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, ref.getWorkspace(), ref,
                    (requestRef) -> {
                        // Build ref execution action
                        return (HttpExtAction) exchangisPostAction;
                    /*return new ExchangisEntityPostAction<>(null,
                            nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());*/
                    }, Map.class);

            if (Objects.isNull(entity)){
                throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
            }
            ExchangisEntityRespResult httpResult = entity.getResult();
            logger.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
            Map<String, Object> responseMap = BDPJettyServerHelper.gson().fromJson(httpResult.getResponseBody(), Map.class);
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");

            String status = Optional.of(dataMap.get("status").toString()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute sqoop node failed,payload is empty", null));
            switch (ExchangisStatusEnum.getEnum(status)) {
                case Failed:
                    return RefExecutionState.Failed;
                case Succeed:
                    return RefExecutionState.Success;
                case Cannelled:
                    return RefExecutionState.Killed;
                default:
                    return RefExecutionState.Running;
            }
        } catch (Exception e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for get state failed，url：" + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "dss execute view error for get state failed", e);
        }
    }

    @Override
    public ResponseRef getAsyncResult(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        String url = baseUrl + "api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/dss/exchangis/main/appJob" + "/execute/" + getId(ref);
        ref.getExecutionRequestRefContext().appendLog("dss execute sqoop node,ready to submit from " + url);
        ExchangisEntityPostAction exchangisEntityPostAction = new ExchangisEntityPostAction();
        exchangisEntityPostAction.setUser(ref.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
        String originLabels = ref.getExecutionRequestRefContext().getRuntimeMap().get("labels").toString();
        /*String realLabels = "";
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(originLabels, Map.class);
            realLabels = responseMap.get("route").toString();
        } catch (JsonProcessingException e) {
            logger.error("parser request error", e);
        }*/
        HashMap<String, String> labels = new HashMap<>();
        labels.put("route", originLabels);
        exchangisEntityPostAction.addRequestPayload("labels", labels);

        try {
            ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, ref.getWorkspace(), ref,
                    (requestRef) -> {
                        // Build ref execution action
                        return exchangisEntityPostAction;
                    /*return new ExchangisEntityPostAction<>(null,
                            nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());*/
                    }, Map.class);
            if (Objects.isNull(entity)) {
                throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
            }
            ExchangisEntityRespResult httpResult = entity.getResult();
            logger.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
            Map<String, Object> responseMap = BDPJettyServerHelper.gson().fromJson(httpResult.getResponseBody(), Map.class);
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
            String newExecId = dataMap.get("jobExecutionId").toString();
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createTextResultSetWriter();
            resultSetWriter.addRecordString(newExecId);
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
        } catch (IOException e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute sqoop node failed，url：" + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "dss execute sqoop node failed", e);
        }
        return new ExchangisCompletedExecutionResponseRef(200);
    }


    public boolean kill (AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(execId)) {
            ref.getExecutionRequestRefContext().appendLog("dss execute sqoop error for execId is null when kill job!");
            throw new ExternalOperationFailedException(90176, "dss execute sqoop error when kill job");
        }
        String url = baseUrl + "api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/dss/exchangis/main/job" + "/execution/" + getId(ref) + "/kill";
        ref.getExecutionRequestRefContext().appendLog("dss execute sqoop node,ready to submit from " + url);

        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        exchangisPostAction.setUrl(ref.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());

        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(url, ref.getWorkspace(), ref,
                (requestRef) -> {
                    // Build ref execution action
                    return (HttpExtAction) exchangisPostAction;
                }, Map.class);

        if (Objects.isNull(entity)) {
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        logger.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        if (httpResult.getStatusCode() == 200) {
            return true;
        }
        return false;
    }

    @Override
    protected Logger getLogger() {
        return null;
    }
}
