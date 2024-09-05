package com.webank.wedatasphere.exchangis.privilege.domain;

import java.util.Arrays;
import java.util.List;

public enum PermissionDefine {

    PROJECT_PERMISSION("项目权限", "project_permission", ProjectPermissionDefine.class),

    JOB_PERMISSION("作业权限", "job_permission", JobPermissionDefine.class),

    DATASOURCE_PERMISSION("数据源权限", "datasource_permission", DataSourcePermissionDefine.class);

    private String name_cn;

    private String name_en;

    private Class handleClass;

    public String getName_cn() {
        return name_cn;
    }

    public String getName_en() {
        return name_en;
    }

    public Class getHandleClass() {
        return handleClass;
    }

    PermissionDefine(String name_cn, String name_en, Class handleClass) {
        this.name_cn = name_cn;
        this.name_en = name_en;
        this.handleClass = handleClass;
    }

    public static List<PermissionDefine> getPermissionDefines() {
        return Arrays.asList(PROJECT_PERMISSION, JOB_PERMISSION, DATASOURCE_PERMISSION);
    }

}
