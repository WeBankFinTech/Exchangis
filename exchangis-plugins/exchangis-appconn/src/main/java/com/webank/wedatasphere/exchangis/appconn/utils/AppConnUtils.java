package com.webank.wedatasphere.exchangis.appconn.utils;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.manager.label.entity.SerializableLabel;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppConnUtils {
    public static String changeDssLabelName(List<DSSLabel> list){
        String dssLabelStr="";
        if(list != null && !list.isEmpty()){
            dssLabelStr=list.stream().map(SerializableLabel::getStringValue).collect(Collectors.joining(","));
        }
        return dssLabelStr;
    }

    public static String getId(NodeRequestRef nodeRequestRef) throws Exception {
        String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef.getJobContent());
        return NumberUtils.parseDoubleString(getNodeId(externalContent));
    }

    public static String getJobContent(Map<String, Object> jobContent) throws Exception {
        String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(jobContent);
        return NumberUtils.parseDoubleString(getNodeId(externalContent));
    }

    private static String getNodeId(String responseBody) throws ExternalOperationFailedException {
        String nodeId="";
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            nodeId = ((Map<String, Object>) responseMap.get("payload")).get("id").toString();
        }catch (Exception e){
            throw new ExternalOperationFailedException(31022, "Get node Id failed!", e);
        }
        return nodeId;
    }
}
