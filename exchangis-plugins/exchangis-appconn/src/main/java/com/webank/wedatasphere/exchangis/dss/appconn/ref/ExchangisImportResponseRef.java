package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.common.entity.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/9 23:01
 */
public class ExchangisImportResponseRef extends CommonResponseRef {

    Map<String, Object> newJobContent = Maps.newHashMap();

    public ExchangisImportResponseRef(Map<String, Object> jobContent, String responseBody, String nodeType) throws Exception {
        super(responseBody, 0);
        if (!Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase(nodeType) && !Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(nodeType)) {
            throw new ExternalOperationFailedException(90177, "Unknown task type" + nodeType, null);
        }
        Map<String, Object> data = (Map<String, Object>)responseMap.get("data");
        Map<String, Object> realNode = (Map<String, Object>)data.get("sqoop");
        double newId = 0;
        for (Map.Entry<String, Object> entry : realNode.entrySet()) {
            newId = Double.parseDouble(entry.getValue().toString());
            if (newId != 0) {
                break;
            }
        }
        Map<String, Object> jobContentData = (Map<String, Object>)jobContent.get("data");
        jobContentData.put("id", newId);
        jobContent.put("data", jobContentData);
        this.newJobContent = jobContent;
    }

    @Override
    public Map<String, Object> toMap() {return newJobContent;}

}
