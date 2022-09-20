package com.webank.wedatasphere.exchangis.job.server.render.transform.processor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.NotNull;

/**
 * Request object of processor
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProcessorRequestVo {

    /**
     * Code content
     */
    private String code;

    /**
     * Job id
     */
    @NotNull(groups = {InsertGroup.class}, message = "Job id cannot be null (任务ID不能为空）")
    private String jobId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
