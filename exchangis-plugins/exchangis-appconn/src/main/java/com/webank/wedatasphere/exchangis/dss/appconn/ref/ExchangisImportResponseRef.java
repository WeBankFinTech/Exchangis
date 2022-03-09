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
        this.newJobContent = jobContent;
    }

    @Override
    public Map<String, Object> toMap() {return newJobContent;}

}
