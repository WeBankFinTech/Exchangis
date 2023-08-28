package com.webank.wedatasphere.exchangis.project.entity.domain;

/**
 * @author jefftlin
 * @create 2022-09-13
 **/
public enum OperationType {

    /**
     * project operation:
     *      query project
     */
    PROJECT_QUERY("PROJECT_QUERY"),

    /**
     * project operation:
     *      update project
     *      delete project
     */
    PROJECT_ALTER("PROJECT_ALTER");

    private String type;

    OperationType(String type) {
        this.type = type;
    }
}
