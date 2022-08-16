package org.apache.linkis.engineconnplugin.datax.plugin;

/**
 * Define the plugin resource
 */
public class PluginResource {

    /**
     * Resource name
     */
    protected String name;

    /**
     * Resource type
     */
    protected String type;

    /**
     * Resource path
     */
    protected String path;

    /**
     * Resource creator
     */
    protected String creator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
