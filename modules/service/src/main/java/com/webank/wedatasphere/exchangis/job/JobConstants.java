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

package com.webank.wedatasphere.exchangis.job;


/**
 * @author enjoyyin
 * 2018/10/29
 */
public class JobConstants {
    public static final long DEFAULT_JOB_TIMEOUT_IN_SECONDS = 43200L;
    public static final String DEFAULT_TEMPLATE_LOC = "/job_templates";
    public static final String DATAX_TEMPLATE_LOC = DEFAULT_TEMPLATE_LOC + "/datax";
    public static final String SQOOP_TEMPLATE_LOC = DEFAULT_TEMPLATE_LOC + "/sqoop";

    public static final String EXECUTOR_NAME="exchangis-executor";

    public static final String FORM_DATATYPE_NAME = "type";
    public static final String FORM_DATASOURCE_NAME_NAME = "name";
    public static final String FORM_DATASOURCE_MODEL_ID = "modelId";
    public static final String FORM_DATASOURCE_MODEL_NAME = "modelName";
    public static final String FORM_DATASOURCE_AUTH_ENTITY = "authEntity";
    public static final String FORM_DATASOURCE_AUTH_CREDEN = "authCreden";

    public static final String CONFIG_DATASOURCEID_NAME = "datasourceId";
    public static final String CONFIG_READER_NAME = "reader";
    public static final String CONFIG_WRITER_NAME = "writer";
    public static final String CONFIG_TRANSFORMER_NAME = "transformer";
    public static final String CONFIG_COLUM_NAME = "column";
    public static final String CONFIG_SPEED_BYTE_NAME = "speedByte";
    public static final String CONFIG_SPEED_RECORD_NAME = "speedRecord";
    public static final String CONFIG_SYNC_METADATA = "syncMeta";
    public static final String CONFIG_ERRORLIMIT_RECORD = "errorRecord";
    public static final String CONFIG_TRANSPORT_TYPE = "transportType";
    public static final String CONFIG_USE_POSTPROCESSOR= "usePostProcess";
    public static final String CONFIG_WRITE_MODE = "writeMode";
    public static final String CONFIG_ADVANCE_MEMORY = "mMemory";
    public static final String CONFIG_ADVANCE_PARALLEL = "mParallel";
}
