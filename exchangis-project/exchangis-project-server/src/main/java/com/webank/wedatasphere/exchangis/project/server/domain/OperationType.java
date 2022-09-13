package com.webank.wedatasphere.exchangis.project.server.domain;

/**
 * @author jefftlin
 * @create 2022-09-13
 **/
public enum OperationType {

    VIEW("view"),

    EDIT("edit"),

    EXEC("exec"),

    DELETE("delete");

    private String type;

    OperationType(String type) {
        this.type = type;
    }
}
