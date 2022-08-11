package com.webank.wedatasphere.exchangis.engine.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

/**
 * Engine store into database
 */
public class EngineStoreResource extends EngineResource{

    private String storeUri;

    public EngineStoreResource(EngineResource engineResource){
        this.engineType = engineResource.getEngineType();
        this.name = engineResource.getName();
        this.type = engineResource.getType();
        this.path = engineResource.getPath();
        this.createTime = Optional.ofNullable(engineResource.getCreateTime())
                .orElse(Calendar.getInstance().getTime());
        this.modifyTime = Optional.ofNullable(engineResource.getModifyTime())
                .orElse(Calendar.getInstance().getTime());
        this.creator = engineResource.getCreator();
        try {
            URI uri = engineResource.getURI();
            if (Objects.nonNull(uri)){
                this.storeUri = uri.toString();
            }
        } catch (Exception e){
            // Ignore
        }

    }

    public EngineStoreResource(){

    }
    @Override
    public InputStream getInputStream() throws IOException {
        throw new IllegalArgumentException("Unsupported method 'getInputStream()'");
    }

    @Override
    public URI getURI() throws URISyntaxException {
        throw new IllegalArgumentException("Unsupported method 'getURI()'");
    }

    public String getStoreUri() {
        return storeUri;
    }

    public void setStoreUri(String storeUri) {
        this.storeUri = storeUri;
    }
}
