package com.webank.wedatasphere.exchangis.dss.appconn.operation.publish;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisExportResponseRef;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/13 16:45
 */
public class ExchangisImportOperation implements ImportRequestRef {

    Map<String, Object> parameters = Maps.newHashMap();
    Workspace workspace;

    @Override
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String getName() {
        return parameters.get("name").toString();
    }

    @Override
    public String getType() {
        return parameters.get("type").toString();
    }

    @Override
    public boolean equals(Object ref) {
        return false;
    }

    @Override
    public String toString() {
        return parameters.toString();
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

}
