package com.webank.wedatasphere.exchangis.privilege.domain;

import java.util.Map;

public class DeployModeInfo {

    private String deployMode;

    private Map<String, String> labelMap;

    public String getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }

    public Map<String, String> getLabelMap() {
        return labelMap;
    }

    public void setLabelMap(Map<String, String> labelMap) {
        this.labelMap = labelMap;
    }

    public enum DeployMode {

        BDP("BDP"),
        BDAP("BDAP");
        private String mode;
        DeployMode(String mode) {
            this.mode = mode;
        }

        public java.lang.String getMode() {
            return mode;
        }

        public void setMode(java.lang.String mode) {
            this.mode = mode;
        }
    }
}
