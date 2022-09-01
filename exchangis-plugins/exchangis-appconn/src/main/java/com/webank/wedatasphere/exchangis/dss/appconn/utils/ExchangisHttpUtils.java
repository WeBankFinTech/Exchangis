package com.webank.wedatasphere.exchangis.dss.appconn.utils;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import org.apache.linkis.httpclient.request.GetAction;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.request.POSTAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.manager.label.entity.SerializableLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 * @date 2022-05-09
 * @since 0.5.0
 */
public class ExchangisHttpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisHttpUtils.class);

    public static InternalResponseRef getResponseRef(WorkspaceRequestRef requestRef, String url,
                                                     DSSHttpAction httpAction,
                                                     SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {

        SSOUrlBuilderOperation ssoUrlBuilderOperation = SSOHelper.createSSOUrlBuilderOperation(requestRef.getWorkspace());
        ssoUrlBuilderOperation.setAppName(Constraints.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        httpAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        //String labels = serializeDssLabel(requestRef.getDSSLabels());
        if(httpAction instanceof POSTAction) {
            HashMap<String, String> labels = new HashMap<>();
            labels.put("route", requestRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
            //exchangisEntityPostAction.addRequestPayload("labels", labels);
            ((POSTAction) httpAction).addRequestPayload("labels", labels);
        } else if(httpAction instanceof GetAction) {
            String labels = requestRef.getDSSLabels().get(0).getValue().get("DSSEnv");
            ((GetAction) httpAction).setParameter("labels", labels);
        }
        LOG.info("User {} try to request Exchangis with url {} and labels {}.", httpAction.getUser(), httpAction.getURL(), requestRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
        HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, httpAction);
        LOG.info("responseBody:{}", httpResult.getResponseBody());
        InternalResponseRef responseRef = ResponseRef.newInternalBuilder().setResponseBody(httpResult.getResponseBody()).build();
        if (responseRef.isFailed()){
            throw new ExternalOperationFailedException(95011, responseRef.getErrorMsg());
        }
        return responseRef;
    }

    /**
     * Invoke the "getStringValue" method in label entity and then concat each one
     * @param list label list
     * @return serialized string value
     */
    public static String serializeDssLabel(List<DSSLabel> list){
        String dssLabelStr = "";
        if(list != null && !list.isEmpty()){
            dssLabelStr = list.stream().map(SerializableLabel::getStringValue).collect(Collectors.joining(","));
        }
        return dssLabelStr;
    }

}
