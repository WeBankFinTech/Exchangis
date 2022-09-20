package com.webank.wedatasphere.exchangis.common.linkis.bml;

/**
 * Bml resource definition
 */
public class BmlResource {
    /**
     * Resource id
     */
    private String resourceId;

    /**
     * Version
     */
    private String version;

    public BmlResource(){

    }

    public BmlResource(String resourceId, String version){
        this.resourceId = resourceId;
        this.version = version;
    }
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
