package com.webank.wedatasphere.exchangis.engine.domain;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.UUID;

/**
 * Local engine resource
 */
public class EngineLocalPathResource extends EngineResource{

    public static final String DEFAULT_SCHEME = "file";

    /**
     * Local file
     */
    private final File localFile;

    /**
     * Whether the resource is a packet
     */
    private boolean isPacket;

    public EngineLocalPathResource(String engineType, URI baseUri, String path){
        this(engineType, baseUri, path, false);
    }

    public EngineLocalPathResource(String engineType, URI baseUri, String path, boolean isPacket){
        this.type = DEFAULT_SCHEME;
        this.engineType = engineType;
        this.path = path;
        this.localFile = new File(baseUri.getPath(), path);
        this.name = localFile.getName();
        this.isPacket = isPacket;
        Calendar calendar = Calendar.getInstance();
        this.createTime = calendar.getTime();
        this.modifyTime = calendar.getTime();
        // Set the creator as jvm user
        this.creator = EnvironmentUtils.getJvmUser();
        // Random resource id
        this.id = UUID.randomUUID().toString();
    }
    @Override
    public InputStream getInputStream() throws IOException {
        if (localFile.exists() && localFile.isFile()){
            return new FileInputStream(this.localFile);
        }
        return null;
    }

    @Override
    public URI getURI() throws URISyntaxException {
        return this.localFile.toURI();
    }


    public String getPath() {
        return path;
    }

    public File getLocalFile(){
        return this.localFile;
    }

    public boolean isPacket() {
        return isPacket;
    }

    public void setPacket(boolean isPacket){
        this.isPacket = isPacket;
    }

    @Override
    public int hashCode() {
        return (getEngineType() + ":" + this.localFile.getPath()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EngineLocalPathResource){
            EngineLocalPathResource other = (EngineLocalPathResource)obj;
            return this.engineType.equals(other.getEngineType()) &&
                    this.localFile.getPath().equals(other.localFile.getPath());
        }
        return super.equals(obj);
    }
}
