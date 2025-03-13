package com.webank.wedatasphere.exchangis.job.vo;

import java.util.List;
import java.util.Map;

/**
 * @author jefftlin
 * @date 2024/10/18
 */
public class ExchangisBulkJobVo {

    private List<Long> ids;

    private Map<String, String> configMap;

    private Map<String, Object> labels;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }
}
