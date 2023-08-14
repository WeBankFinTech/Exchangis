package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Ref export operation
 */
public class ExchangisExportOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ExportResponseRef>
        implements RefExportOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    private String exportUrl;

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        exportUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appJob/export"));
    }

    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl exportRequestRef) throws ExternalOperationFailedException {
        logger.info("User {} try to export Exchangis job {} with jobContent: {}, refProjectId: {}, projectName: {}, nodeType: {}.",
                exportRequestRef.getUserName(), exportRequestRef.getName(), exportRequestRef.getRefJobContent(),
                exportRequestRef.getRefProjectId(), exportRequestRef.getProjectName(), exportRequestRef.getType());
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(exportRequestRef.getUserName());
        postAction.addRequestPayload("projectId", exportRequestRef.getRefProjectId());
        postAction.addRequestPayload("partial", true);
        String nodeType = exportRequestRef.getType();
        Long id = ((Double) exportRequestRef.getRefJobContent().get(Constraints.REF_JOB_ID)).longValue();
        if(Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase(nodeType)) {
            postAction.addRequestPayload("sqoopIds", id);
        } else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(nodeType)) {
            postAction.addRequestPayload("dataXIds", id);
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown Exchangis jobType " + exportRequestRef.getType());
        }
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(exportRequestRef, exportUrl, postAction, ssoRequestOperation);
        return ExportResponseRef.newBuilder().setResourceMap(responseRef.getData()).success();
    }

}
