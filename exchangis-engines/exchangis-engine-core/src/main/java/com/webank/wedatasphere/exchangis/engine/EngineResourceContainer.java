package com.webank.wedatasphere.exchangis.engine;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceScanner;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceUploader;

/**
 * Resource container
 */
public interface EngineResourceContainer {

    /**
     * Engine type related
     * @return string
     */
    String getEngineType();

    /**
     * Get engine resource from username and identify
     * @param userName userName user name
     * @param identify identify identify
     * @return
     */
    EngineResource getResource(String userName, String identify);


    EngineResource getResource(String identify);
    /**
     * Engine resource scanner
     * @return scanner
     */
    EngineResourceScanner getResourceScanner();

    /**
     * Engine resource uploader
     * @return
     */
    EngineResourceUploader getResourceUploader();
}
