package com.webank.wedatasphere.exchangis.project.server.request;

import java.io.Serializable;

public class ProjectQueryRequest implements Serializable {

    private static final long serialVersionUID=1L;

    private String username;

    private String name;

    private String domain;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }
}
