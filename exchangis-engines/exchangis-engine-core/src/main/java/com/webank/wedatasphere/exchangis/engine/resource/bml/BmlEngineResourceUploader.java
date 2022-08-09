package com.webank.wedatasphere.exchangis.engine.resource.bml;

import com.webank.wedatasphere.exchangis.engine.domain.EngineBmlResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResUploadException;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceUploader;
import org.apache.linkis.bml.protocol.BmlUpdateResponse;
import org.apache.linkis.bml.protocol.BmlUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Bml engine resource uploader
 */
public class BmlEngineResourceUploader implements EngineResourceUploader<EngineLocalPathResource, EngineBmlResource> {

    private static final Logger LOG = LoggerFactory.getLogger(BmlEngineResourceUploader.class);

    @Override
    public EngineBmlResource upload(EngineLocalPathResource res) throws ExchangisEngineResUploadException {
        try {
            BmlUploadResponse uploadResponse = BmlClients.getInstance()
                    .uploadResource(res.getCreator(), res.getName(), res.getInputStream());
            return new EngineBmlResource(res.getEngineType(), res.getPath(),
                    res.getName(), uploadResponse.resourceId(), uploadResponse.version(), res.getCreator());
        } catch (Exception e){
            throw new ExchangisEngineResUploadException(
                    "Fail to upload resource: [name: " + res.getName() + ", path: " + res.getPath()
                            + ", type: "+ res.getType() + ", creator: "+ res.getCreator() + "]", e);
        }
    }

    @Override
    public EngineBmlResource upload(EngineLocalPathResource res, EngineBmlResource relatedResource) throws ExchangisEngineResUploadException {
        if (Objects.isNull(relatedResource)){
            return upload(res);
        }
        try {
            BmlUpdateResponse response = BmlClients.getInstance()
                    .updateResource(res.getCreator(), relatedResource.getResourceId(),
                            res.getName(), res.getInputStream());
            return new EngineBmlResource(relatedResource.getEngineType(), res.getPath(),
                    res.getName(), response.resourceId(), response.version(), res.getCreator());
        } catch (Exception e){
            throw new ExchangisEngineResUploadException(
                    "Fail to upload resource: [name: " + res.getName() + ", path: " + res.getPath()
                            + ", type: "+ res.getType() + ", resourceId: " + relatedResource.getResourceId() +
                            ",creator: "+ res.getCreator() + "]", e);
        }
    }
}
