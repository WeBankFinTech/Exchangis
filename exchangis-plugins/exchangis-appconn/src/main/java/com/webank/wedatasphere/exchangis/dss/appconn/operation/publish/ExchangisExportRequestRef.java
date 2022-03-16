package com.webank.wedatasphere.exchangis.dss.appconn.operation.publish;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/13 16:33
 */
public class ExchangisExportRequestRef implements ExportRequestRef {

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
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
