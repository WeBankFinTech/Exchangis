package com.webank.wedatasphere.exchangis.project.server.domain;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;

/**
 * For querying page
 */
public class ProjectPageQuery extends PageQuery {
    /**
     * Project name
     */
    protected String name;

    protected String domain = ExchangisProject.Domain.STANDALONE.name();

    protected String createUser;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
