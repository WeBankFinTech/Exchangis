package com.webank.wedatasphere.exchangis.project.entity.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.linkis.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExchangisProject {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProject.class);

    public enum Domain {
        DSS, STANDALONE
    }

    /**
     * Id (Long value)
     */
    private Long id;
    /**
     * Project name
     */
    private String name;
    /**
     * Description
     */
    private String description;

    /**
     * Create user
     */
    private String createUser;

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Last update user
     */
    private String lastUpdateUser;

    /**
     * Last update time
     */
    private Date lastUpdateTime;

    /**
     * Labels
     */
    private String labels;

    /**
     * User has editing permission
     */
    private String editUsers;

    /**
     * User has viewing permission
     */
    private String viewUsers;

    /**
     * User has executing permission
     */
    private String execUsers;

    /**
     * Domain
     */
    private String domain;

    /**
     * Source map
     */
    private Map<String, Object> sourceMap = new HashMap<>();

    private String source;

    private String privUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(String editUsers) {
        this.editUsers = editUsers;
    }

    public String getViewUsers() {
        return viewUsers;
    }

    public void setViewUsers(String viewUsers) {
        this.viewUsers = viewUsers;
    }

    public String getExecUsers() {
        return execUsers;
    }

    public void setExecUsers(String execUsers) {
        this.execUsers = execUsers;
    }

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getPrivUser() {
        return privUser;
    }

    public void setPrivUser(String privUser) {
        this.privUser = privUser;
    }

    // TODO use the common Json util
    public Map<String, Object> getSourceMap() {
        if (Objects.isNull(this.sourceMap) && Objects.nonNull(this.source)){
            try {
                ObjectMapper mapper = JsonUtils.jackson();
                this.sourceMap = mapper.readValue(this.source, mapper.getTypeFactory()
                        .constructParametricType(Map.class, String.class, Object.class));
            } catch (JsonProcessingException e) {
                //Ignore
                LOG.warn("Cannot deserialize the source string: {}", this.source, e);
            }
        }
        return sourceMap;
    }

    public void setSourceMap(Map<String, Object> sourceMap) {
        this.sourceMap = sourceMap;
    }

    // TODO use the common Json util
    public String getSource() {
        if (Objects.isNull(this.source) && Objects.nonNull(this.sourceMap)){
            try {
                this.source = JsonUtils.jackson().writeValueAsString(this.sourceMap);
            } catch (JsonProcessingException e) {
                // Ignore
                LOG.warn("Cannot serialize the source map", e);
            }
        }
        return source;
    }
}
