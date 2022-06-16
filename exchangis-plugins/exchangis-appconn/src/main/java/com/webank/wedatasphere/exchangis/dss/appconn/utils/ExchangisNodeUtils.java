package com.webank.wedatasphere.exchangis.dss.appconn.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/9 16:14
 */
public class ExchangisNodeUtils {
    public static String getId(NodeRequestRef nodeRequestRef) throws Exception {
        String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef.getJobContent());
        if ("linkis.appconn.exchangis.sqoop".equalsIgnoreCase(nodeRequestRef.getNodeType())) {
            return NumberUtils.parseDoubleString(getSqoopId(externalContent));
        } else if ("linkis.appconn.exchangis.datax".equalsIgnoreCase(nodeRequestRef.getNodeType())) {
            return NumberUtils.parseDoubleString(getDataxId(externalContent));
        }
        return null;
    }


    public static String getSqoopId(String responseBody) throws ExternalOperationFailedException {
        String sqoopId = null;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            sqoopId = ((Map<String, Object>) responseMap.get("data")).get("id").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000054, "Get sqoop Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000054, "Get sqoop Id failed!", e);
        }

        return sqoopId;
    }

    public static String getDataxId(String responseBody) throws ExternalOperationFailedException {
        String dataxId = null;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            dataxId = ((Map<String, Object>) responseMap.get("data")).get("dataxId").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000055, "Get datax Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000055, "Get datax Id failed!", e);
        }
        return dataxId;
    }
}
