/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.job.domain;

/**
 * Created by devendeng on 2018/8/24.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Report of job
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobReport {
    private Long id;
    @NotNull(message = "{udes.domain.jobReport.id.notNull}")
    private Long jobId;
    private Double totalCosts;
    private Long byteSpeedPerSecond;
    private Long recordSpeedPerSecond;
    private Long totalReadRecords;
    private Long totalReadBytes;
    private Long totalErrorRecords;
    private Long transformerTotalRecords;
    private Long transformerFailedRecords;
    private Long transformerFilterRecords;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Double getTotalCosts() {
        return totalCosts;
    }

    public void setTotalCosts(Double totalCosts) {
        this.totalCosts = totalCosts;
    }

    public Long getByteSpeedPerSecond() {
        return byteSpeedPerSecond;
    }

    public void setByteSpeedPerSecond(Long byteSpeedPerSecond) {
        this.byteSpeedPerSecond = byteSpeedPerSecond;
    }

    public Long getRecordSpeedPerSecond() {
        return recordSpeedPerSecond;
    }

    public void setRecordSpeedPerSecond(Long recordSpeedPerSecond) {
        this.recordSpeedPerSecond = recordSpeedPerSecond;
    }

    public Long getTotalReadRecords() {
        return totalReadRecords;
    }

    public void setTotalReadRecords(Long totalReadRecords) {
        this.totalReadRecords = totalReadRecords;
    }

    public Long getTotalReadBytes() {
        return totalReadBytes;
    }

    public void setTotalReadBytes(Long totalReadBytes) {
        this.totalReadBytes = totalReadBytes;
    }

    public Long getTotalErrorRecords() {
        return totalErrorRecords;
    }

    public void setTotalErrorRecords(Long totalErrorRecords) {
        this.totalErrorRecords = totalErrorRecords;
    }

    public Long getTransformerTotalRecords() {
        return transformerTotalRecords;
    }

    public void setTransformerTotalRecords(Long transformerTotalRecords) {
        this.transformerTotalRecords = transformerTotalRecords;
    }

    public Long getTransformerFailedRecords() {
        return transformerFailedRecords;
    }

    public void setTransformerFailedRecords(Long transformerFailedRecords) {
        this.transformerFailedRecords = transformerFailedRecords;
    }

    public Long getTransformerFilterRecords() {
        return transformerFilterRecords;
    }

    public void setTransformerFilterRecords(Long transformerFilterRecords) {
        this.transformerFilterRecords = transformerFilterRecords;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
