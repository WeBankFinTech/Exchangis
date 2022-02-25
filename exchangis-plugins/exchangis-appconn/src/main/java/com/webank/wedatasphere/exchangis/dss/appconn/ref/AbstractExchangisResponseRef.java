package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Abstract response ref
 */
public abstract class AbstractExchangisResponseRef extends AbstractResponseRef {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractExchangisResponseRef.class);
    protected AbstractExchangisResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public AbstractExchangisResponseRef(ExchangisEntityRespResult result){
        super(result.getResponseBody(), result.getStatusCode());
    }
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        try {
            responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        } catch (JsonProcessingException e) {
            LOG.warn("Fail to convert the response body {} to map", responseBody);
            return Collections.emptyMap();
        }
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

}
