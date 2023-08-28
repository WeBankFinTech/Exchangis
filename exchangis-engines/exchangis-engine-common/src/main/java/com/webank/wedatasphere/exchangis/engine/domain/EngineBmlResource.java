package com.webank.wedatasphere.exchangis.engine.domain;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

/**
 * BML resources
 */
public class EngineBmlResource extends EngineResource {

    private static final Logger LOG = LoggerFactory.getLogger(EngineBmlResource.class);

    public static final String DEFAULT_SCHEME = "bml";

    /**
     * Resource id
     */
    private String resourceId;

    /**
     * Version
     */
    private String version;

    public EngineBmlResource(String engineType,
                             String path,  String name,
                             String resourceId, String version, String creator){
        this.type = DEFAULT_SCHEME;
        this.name = name;
        this.engineType = engineType;
        this.resourceId = resourceId;
        // Use the bml resource id as id
        this.id = resourceId;
        this.version = version;
        this.path = path;
        this.creator = creator;
        Calendar calendar = Calendar.getInstance();
        this.createTime = calendar.getTime();
        this.modifyTime = calendar.getTime();
    }

    /**
     * Get bml resource from stored resource
     * @param storeResource store resource
     */
    public EngineBmlResource(EngineStoreResource storeResource){
        this(storeResource.engineType, storeResource.path, storeResource.name, null, null,
                storeResource.creator);
        this.createTime = storeResource.createTime;
        this.modifyTime = storeResource.modifyTime;
        String storeUri = storeResource.getStoreUri();
        if (StringUtils.isNotBlank(storeUri)){
            try {
                String storePath = new URI(storeUri).getPath();
                if (storePath.startsWith(IOUtils.DIR_SEPARATOR_UNIX + "")){
                    storePath = storePath.substring(1);
                }
                String[] storeParts = storePath.split(IOUtils.DIR_SEPARATOR_UNIX + "");
                if (storeParts.length >= 2){
                    this.resourceId = storeParts[0];
                    this.version = storeParts[1];
                }
            } catch (URISyntaxException e) {
                LOG.warn("Unrecognized bml stored uri: [{}]", storeUri, e);
            }
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // TODO get input stream from BML
        return null;
    }

    @Override
    public URI getURI() throws URISyntaxException {
        return new URI(DEFAULT_SCHEME, "", IOUtils.DIR_SEPARATOR_UNIX +
                resourceId + IOUtils.DIR_SEPARATOR_UNIX + version, null, null);
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
