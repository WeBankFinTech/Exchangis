package com.webank.wedatasphere.exchangis.job.domain;

import java.util.List;
import java.util.Map;

public class LaunchTask {

    private List content;

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
