package com.webank.wedatasphere.exchangis.project.entity.vo;

import com.webank.wedatasphere.exchangis.project.entity.domain.ExchangisProjectUser;

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
    private String projectId;

    /**
     * Project name
     */
    @NotNull(message = "PrivUser cannot be null (用户名不能为空)")
    private String privUser;

    @NotNull(message = "Priv cannot be null (用户项目权限不能为空)")
    private int priv;

    public ExchangisProjectUserVo() {
    }

    public ExchangisProjectUserVo(Long projectId, String privUser) {
        this.projectId = String.valueOf(projectId);
        this.privUser = privUser;
    }

    public ExchangisProjectUserVo(ExchangisProjectUser exchangisProjectUser) {
        this.projectId = String.valueOf(exchangisProjectUser.getProjectId());
        this.privUser = exchangisProjectUser.getPrivUser();
        this.priv = exchangisProjectUser.getPriv();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = String.valueOf(projectId);
    }

    public String getPrivUser() {
        return privUser;
    }

    public void setPrivUser(String privUser) {
        this.privUser = privUser;
    }
}
