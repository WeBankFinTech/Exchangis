package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.AbstractExchangisOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.entity.ProjectReqEntity;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.JsonExtension;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implement of operation related by project
 */
public abstract class AbstractExchangisProjectOperation extends AbstractExchangisOperation {

    public AbstractExchangisProjectOperation(String[] uriParts) {
        super(uriParts);
    }

    public AbstractExchangisProjectOperation(){

    }
    /**
     * Get Project entity
     * @return postEntity
     */
    protected ProjectReqEntity getProjectEntity(ProjectRequestRef projectRequestRef){
            // Build project request entity
        Map<String, Object> source = new HashMap<>();
        String editUsers= StringUtils.join( projectRequestRef.getEditUsers(),",");
        String viewUsers = StringUtils.join( projectRequestRef.getAccessUsers(),",");
        String execUsers = StringUtils.join( projectRequestRef.getReleaseUsers(),",");
                ProjectReqEntity projectReqEntity = new ProjectReqEntity(editUsers,viewUsers,execUsers,
                projectRequestRef.getName(), projectRequestRef.getDescription(), source);
        projectReqEntity.setLabels(AppConnUtils.serializeDssLabel(projectRequestRef.getDSSLabels()));
        // Try to set the project request ref into the source map
        try {
            Map<String, Object> requestRefMap = JsonExtension.convert(projectReqEntity, Map.class, String.class, Object.class);
            source.putAll(requestRefMap);
        } catch (ExternalOperationFailedException e){
            getLogger().warn("Unable to serialize the project requestRef into the source map", e);
        }
        return projectReqEntity;
    }
}
