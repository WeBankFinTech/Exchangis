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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The http form of job config
 * @author enjoyyin
 * 2018/10/29
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobConfForm {
    /**
     * Source parameters
     */
    @NotEmpty(message = "{udes.domain.jobConf.srcParams.notEmpty}")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> dataSrcParams = new HashMap<>();
    /**
     * Dest parameters
     */
    @NotEmpty(message = "{udes.domain.jobConf.destParams.notEmpty}")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> dataDstParams = new HashMap<>();
    /**
     * Mapping
     */
    @Valid
    private List<ColumnMap> columnMaps = new ArrayList<>();

    /**
     * Speed
     */
    @Valid
    private Speed speed = new Speed();

    /**
     * Error limit
     */
    @Valid
    private ErrorLimit errorLimit = new ErrorLimit();

    /**
     * Advance
     */
    @Valid
    private Advance advance = new Advance();

    /**
     * Transport type
     */
    private String transportType = "record";

    /**
     * If use post process
     */
    private boolean usePostProcess;

    /**
     * If need to synchronize meta data
     */
    private boolean syncMeta;

    /**
     * Code block
     */
    private String procSrcCode;

    public boolean isSyncMeta() {
        return syncMeta;
    }

    public void setSyncMeta(boolean syncMeta) {
        this.syncMeta = syncMeta;
    }

    public String getTransportType() {
        return transportType;
    }
    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Map<String, Object> getDataSrcParams() {
        return dataSrcParams;
    }

    public void setDataSrcParams(Map<String, Object> dataSrcParams) {
        this.dataSrcParams = dataSrcParams;
    }

    public Map<String, Object> getDataDstParams() {
        return dataDstParams;
    }

    public void setDataDstParams(Map<String, Object> dataDstParams) {
        this.dataDstParams = dataDstParams;
    }

    public List<ColumnMap> getColumnMaps() {
        return columnMaps;
    }

    public void setColumnMaps(List<ColumnMap> columnMaps) {
        this.columnMaps = columnMaps;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public ErrorLimit getErrorLimit() {
        return errorLimit;
    }

    public void setErrorLimit(ErrorLimit errorLimit) {
        this.errorLimit = errorLimit;
    }

    public Advance getAdvance() {
        return advance;
    }

    public void setAdvance(Advance advance) {
        this.advance = advance;
    }

    public boolean isUsePostProcess() {
        return usePostProcess;
    }

    public void setUsePostProcess(boolean usePostProcess) {
        this.usePostProcess = usePostProcess;
    }

    public String getProcSrcCode() {
        return procSrcCode;
    }

    public void setProcSrcCode(String procSrcCode) {
        this.procSrcCode = procSrcCode;
    }

    public static class Advance{
        /**
         * Max memory
         */
        @Min(value=0, message = "{udes.domain.jobConf.mMemory.min}")
//        @Max(value=32, message = "{udes.domain.jobConf.mMemory.max}")
        private int mMemory;

        @JsonIgnore
        private String memoryUnit = "M";

        /**
         * Max parallel
         */
        @Min(value=0, message = "{udes.domain.jobConf.mParallel.min}")
        @Max(value=999, message = "{udes.domain.jobConf.mParallel.max}")
        private int mParallel;

        public int getmMemory() {
            return mMemory;
        }

        public void setmMemory(int mMemory) {
            this.mMemory = mMemory;
        }

        public int getmParallel() {
            return mParallel;
        }

        public void setmParallel(int mParallel) {
            this.mParallel = mParallel;
        }

        public String getMemoryUnit() {
            return memoryUnit;
        }

        public void setMemoryUnit(String memoryUnit) {
            this.memoryUnit = memoryUnit;
        }
    }
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Speed{

        /**
         * record/s
         */
        @Min(value = 0, message = "{udes.domain.jobConf.speed.record.min}")
        private int record;

        /**
         * MB/s
         */
        @Min(value = 0, message = "{udes.domain.jobConf.speed.mBytes.min}")
        private int mBytes;

        public int getRecord() {
            return record;
        }

        public void setRecord(int record) {
            this.record = record;
        }

        public int getmBytes() {
            return mBytes;
        }

        public void setmBytes(int mBytes) {
            this.mBytes = mBytes;
        }
    }
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ErrorLimit{
        @Min(value =0, message = "{udes.domain.jobConf.errorLimit.record.min}")
        private int record;

        public int getRecord() {
            return record;
        }

        public void setRecord(int record) {
            this.record = record;
        }
    }
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ColumnMap{
        /**
         * Source index
         */
        @NotNull(message = "{udes.domain.jobConf.column.srcIndex.null}")
        private Integer srcIndex;
        /**
         * Source name
         */
        @NotBlank(message = "{udes.domain.jobConf.column.srcName.notBlank}")
        private String srcName;
        /**
         * Source type
         */
        @NotBlank(message = "{udes.domain.jobConf.column.srcType.notBlank}")
        private String srcType;
        /**
         * Dest name
         */
        @NotBlank(message = "{udes.domain.jobConf.column.dstName.notBlank}")
        private String dstName;
        /**
         * Dest type
         */
        @NotBlank(message = "{udes.domain.jobConf.column.dstType.notBlank}")
        private String dstType;
        /**
         * Verify function string
         */
        private String verifyFunc;
        /**
         * Transform function string
         */
        private String transforFunc;
        public String getSrcName() {
            return srcName;
        }

        public void setSrcName(String srcName) {
            this.srcName = srcName;
        }

        public String getSrcType() {
            return srcType;
        }

        public void setSrcType(String srcType) {
            this.srcType = srcType;
        }

        public String getDstName() {
            return dstName;
        }

        public void setDstName(String dstName) {
            this.dstName = dstName;
        }

        public String getDstType() {
            return dstType;
        }

        public void setDstType(String dstType) {
            this.dstType = dstType;
        }

        public String getVerifyFunc() {
            return verifyFunc;
        }

        public void setVerifyFunc(String verifyFunc) {
            this.verifyFunc = verifyFunc;
        }

        public String getTransforFunc() {
            return transforFunc;
        }

        public void setTransforFunc(String transforFunc) {
            this.transforFunc = transforFunc;
        }

        public Integer getSrcIndex() {
            return srcIndex;
        }

        public void setSrcIndex(Integer srcIndex) {
            this.srcIndex = srcIndex;
        }
    }
}
