package com.webank.wedatasphere.exchangis.datasource.dto;

public class ExchangisDataSourceDTO {
    private final String id;
    private final String classifier;
    private final String name;
    private String option;
    private String description;
    private String icon;

    public ExchangisDataSourceDTO(String id, String classifier, String name) {
        this.id = id;
        this.classifier = classifier;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getName() {
        return name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
