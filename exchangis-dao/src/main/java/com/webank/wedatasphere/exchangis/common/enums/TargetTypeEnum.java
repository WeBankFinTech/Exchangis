package com.webank.wedatasphere.exchangis.common.enums;

/**
 * @author tikazhang
 * @Date 2022/9/19 20:22
 */
public enum TargetTypeEnum {
    /**
     * 项目
     */
    PROJECT("project"),
    /**
     * 作业
     */
    JOB("job"),
    /**
     * 数据源
     */
    DATASOURCE("datasource"),
    /**
     * 任务
     */
    TASK("task"),
    ;
    private String name;

    TargetTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
