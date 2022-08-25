package com.webank.wedatasphere.exchangis.job.server.restful.external;

import java.util.Arrays;

/**
 * @author tikazhang
 * @Date 2022/3/14 20:37
 */
public enum ModuleEnum {

    SQOOP_IDS("sqoopIds", "SQOOP ids"),

    DATAX_IDS("dataXIds", "DATAX ids");

    private String name;
    private String desc;

    ModuleEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static ModuleEnum getEnum(String name) {
        return Arrays.stream(ModuleEnum.values()).filter(e -> e.getName().equals(name)).findFirst().orElseThrow(NullPointerException::new);
    }

    public String getName() {
        return name;
    }
}
