package com.webank.wedatasphere.exchangis.project.server.request;

import java.io.Serializable;

public class ProjectQueryRequest implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
