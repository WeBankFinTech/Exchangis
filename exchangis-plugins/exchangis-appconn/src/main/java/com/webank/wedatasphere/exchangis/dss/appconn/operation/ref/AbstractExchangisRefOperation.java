package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.AbstractExchangisOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.request.entity.RefJobReqEntity;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract implement of operation related by ref
 */
public abstract class AbstractExchangisRefOperation extends AbstractExchangisOperation {
    public AbstractExchangisRefOperation() {
    }

    public AbstractExchangisRefOperation(String[] uriParts) {
          super(uriParts);
    }

    /**
     * Job request entity
     * @param nodeRequestRef node request ref
     * @param engineType engine type
     * @return
     */
    public RefJobReqEntity getRefJobReqEntity(NodeRequestRef nodeRequestRef, String engineType){
        Map<String, Object> jobContent = Optional.ofNullable(nodeRequestRef.getJobContent()).orElse(Collections.emptyMap());
        String desc = String.valueOf(jobContent.getOrDefault(Constraints.REF_JOB_DESC, ""));
        RefJobReqEntity jobReqEntity = new RefJobReqEntity(nodeRequestRef.getName(), desc,
                Constraints.JOB_TYPE_OFFLINE, engineType, nodeRequestRef.getProjectId());
        jobReqEntity.setJobLabels(AppConnUtils.serializeDssLabel(nodeRequestRef.getDSSLabels()));
        //TODO store the job content
        jobReqEntity.setSource(nodeRequestRef.getJobContent());
        return jobReqEntity;
    }
}
