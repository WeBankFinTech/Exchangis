package com.webank.wedatasphere.exchangis.job.datax.domain;

import java.util.Map;

public class Mapping {

    private String source_field_name;
    private String source_field_type;
    private String sink_field_name;
    private String sink_field_type;
    private String[] validator;
    private Map transformer;

    public String getSource_field_name() {
        return source_field_name;
    }

    public void setSource_field_name(String source_field_name) {
        this.source_field_name = source_field_name;
    }

    public String getSource_field_type() {
        return source_field_type;
    }

    public void setSource_field_type(String source_field_type) {
        this.source_field_type = source_field_type;
    }

    public String getSink_field_name() {
        return sink_field_name;
    }

    public void setSink_field_name(String sink_field_name) {
        this.sink_field_name = sink_field_name;
    }

    public String getSink_field_type() {
        return sink_field_type;
    }

    public void setSink_field_type(String sink_field_type) {
        this.sink_field_type = sink_field_type;
    }

    public String[] getValidator() {
        return validator;
    }

    public void setValidator(String[] validator) {
        this.validator = validator;
    }

    public Map getTransformer() {
        return transformer;
    }

    public void setTransformer(Map transformer) {
        this.transformer = transformer;
    }
}
