package com.webank.wedatasphere.exchangis.project.server.vo;

import javax.validation.constraints.NotNull;

/**
 * @author tikazhang
 * @Date 2022/3/26 15:36
 */
public class ExchangisProjectUserVo {
    /**
     * ID
     */
    @NotNull(message = "Project id cannot be null (项目ID不能为空)")
    private Long projectId;

    /**
     * Project name
     */
    @NotNull(message = "PrivUser cannot be null (用户名不能为空)")
    private String privUser;

    public ExchangisProjectUserVo() {
    }

    public ExchangisProjectUserVo(Long projectId, String privUser) {
        this.projectId = projectId;
        this.privUser = privUser;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPrivUser() {
        return privUser;
    }

    public void setPrivUser(String privUser) {
        this.privUser = privUser;
    }
}
