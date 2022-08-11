package com.webank.wedatasphere.exchangis.engine.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Engine resource
 */
public abstract class EngineResource {

    /**
     * Engine type
     */
    protected String engineType;

    /**
     * Resource id
     */
    protected String id;

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
     * Create time
     */
    protected Date createTime;

    /**
     * Modify time
     */
    protected Date modifyTime;
    /**
     * Create user
     */
    protected String creator;
    /**
     * Get input stream from resource
     * @return input stream
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * URI value
     * @return uri
     */
    public abstract URI getURI() throws URISyntaxException;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
