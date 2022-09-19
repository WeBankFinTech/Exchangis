package com.webank.wedatasphere.exchangis.job.auditlog;

/**
 * @author tikazhang
 * @Date 2022/9/19 20:16
 */
public enum  OperateTypeEnum {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    COPY("copy"),
    EXPORT("export"),
    IMPORT("import"),
    ;
    private String name;
    OperateTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
