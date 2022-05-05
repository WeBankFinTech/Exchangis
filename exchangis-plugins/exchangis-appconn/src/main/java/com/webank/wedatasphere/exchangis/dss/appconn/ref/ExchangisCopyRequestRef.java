package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractRequestRef;

/**
 * @author tikazhang
 * @Date 2022/4/24 10:59
 */
public class ExchangisCopyRequestRef extends AbstractRequestRef implements CopyRequestRef {

    private Workspace workspace;

    @Override
    public void setWorkspace(Workspace workspace) { this.workspace =  workspace; }

    @Override
    public Workspace getWorkspace() { return this.workspace; }
}
