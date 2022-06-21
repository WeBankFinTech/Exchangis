package com.webank.wedatasphere.exchangis.datasource.dto;

import java.util.Map;

public class MetadataGetTablePropsResultDTO extends ResultDTO {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private Map<String, String> props;

        public Map<String, String> getProps() {
            return props;
        }

        public void setProps(Map<String, String> props) {
            this.props = props;
        }
    }

}
