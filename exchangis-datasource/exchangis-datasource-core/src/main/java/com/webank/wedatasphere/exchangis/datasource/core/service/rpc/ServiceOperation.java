package com.webank.wedatasphere.exchangis.datasource.core.service.rpc;


/**
 * Operation
 */
public class ServiceOperation {
    /**
     * Uri
     */
    protected String uri;
    /**
     * Timestamp
     */
    protected long timestamp;


    public ServiceOperation(){

    }

    public ServiceOperation(String uri){
        this.uri = uri;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
