package com.webank.wedatasphere.exchangis.job.datax.domain;

import com.webank.wedatasphere.exchangis.job.domain.Content;

import java.util.List;
import java.util.Map;

public class DataxLaunchCode {

    private List<Content> content;

    private Map settings;

    public List getContent() {
        return content;
    }

    public void setContent(List content) {
        this.content = content;
    }

    public Map getSettings() {
        return settings;
    }

    public void setSettings(Map settings) {
        this.settings = settings;
    }
}
