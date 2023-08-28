package org.apache.linkis.engineconnplugin.datax.plugin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * BML resources
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginBmlResource extends PluginResource{

    /**
     * Resource id
     */
    private String resourceId;

    /**
     * Version
     */
    private String version;

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
