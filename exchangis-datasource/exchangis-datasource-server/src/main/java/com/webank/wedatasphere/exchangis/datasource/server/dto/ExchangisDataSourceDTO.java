package com.webank.wedatasphere.exchangis.datasource.server.dto;

public class ExchangisDataSourceDTO {
    private final String type;
    private final String description;
    private final String icon;

    public ExchangisDataSourceDTO(String type, String description, String icon) {
        this.type = type;
        this.description = description;
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
