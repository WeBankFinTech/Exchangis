package com.webank.wedatasphere.exchangis.job.domain;

/**
 * @author jefftlin
 * @create 2022-09-15
 **/
public enum OperationType {

    /**
     * job operation:
     *      query project
     */
    JOB_QUERY("JOB_QUERY"),

    /**
     * job operation:
     *      create jpb
     *      update job info
     *      update job config
     *      update job content
     *      delete job
     */
    JOB_ALTER("JOB_ALTER"),

    /**
     * job operation:
     *      job execute
     *      job kill
     *      sub job delete
     */
    JOB_EXECUTE("JOB_EXECUTE"),

    /**
     * job operation:
     *      job release
     */
    JOB_RELEASE("JOB_RELEASE");

    private String type;

    OperationType(String type) {
        this.type = type;
    }
}
