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

package com.webank.wedatasphere.exchangis.job.config.handlers;

import com.webank.wedatasphere.exchangis.common.util.Unicode;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.TransportType;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/4/9
 */
@Service(JobDataConfHandler.PREFIX + "hdfs")
public class HdfsJobDataConfHandler extends AbstractJobDataConfHandler{
    private static final String HDFS_PATH = "path";
    private static final String HDFS_FILETYPE = "fileType";
    private static final String HDFS_FIELD_DELIMITER = "fieldDelimiter";
    private static final String HDFS_COMPRESS = "compress";
    private static final String HDFS_ENCODING = "encoding";
    private static final String HDFS_FILE_NAME = "fileName";
    private static final String HDFS_WRITE_MODE = "writeMode";

    private static final Logger logger = LoggerFactory.getLogger(HdfsJobDataConfHandler.class);

    private static final String[] SUPPORT_COMPRESS = new String[]{"gzip", "bz2", "zip", "lzo", "deflate",
            "lzo_deflate", "snappy", "hadoop-snappy", "framing-snappy", "none", "snappy"};

    private static final String[] SUPPORT_READ_FILE_TYPE = new String[]{"text", "orc", "rc", "seq", "csv", "hfile"};

    private static final String[] SUPPORT_WRITE_FILE_TYPE = new String[]{"text", "orc"};
    @Override
    protected String[] connParamNames() {
        return new String[]{
                Constants.PARAM_HDFS_PATH,  Constants.PARAM_HADOOP_CONF_LIST, Constants.PARAM_KERBEROS_BOOLEAN,
                Constants.PARAM_KERBEROS_FILE_PRINCILE, Constants.PARAM_KB_FILE_PATH,
                Constants.PARAM_LADP_USERNAME, Constants.PARAM_LADP_PASSWORD
        };
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        String path = String.valueOf(dataFormParams.getOrDefault(HDFS_PATH, ""));
       if(StringUtils.isBlank(path)){
           throw new JobDataParamsInValidException("exchange.job.handler.path.notNull");
       }
       if(!new File(path).isAbsolute()){
           throw new JobDataParamsInValidException("exchange.job.handler.path.absolute");
       }
       String encoding = String.valueOf(dataFormParams.getOrDefault(HDFS_ENCODING, ""));
       if(StringUtils.isNotBlank(encoding)){
           try{
               Charsets.toCharset(encoding);
           }catch(Exception e){
               throw new JobDataParamsInValidException("exchange.job.handler.encoding.wrong");
           }
       }
       String compress = String.valueOf(dataFormParams.getOrDefault(HDFS_COMPRESS, ""));
       if(StringUtils.isNotBlank(compress)){
           if(!isSupport(compress, SUPPORT_COMPRESS)){
               throw new JobDataParamsInValidException("exchange.job.handler.compress.notSupport");
           }
       }
        TransportType transportType = TransportType.type(
                String.valueOf(dataFormParams.getOrDefault(JobConstants.CONFIG_TRANSPORT_TYPE, "")));
        if(transportType == TransportType.RECORD){
            String fileType = String.valueOf(dataFormParams.getOrDefault(HDFS_FILETYPE, ""));
            if(StringUtils.isBlank(fileType)){
                throw new JobDataParamsInValidException("exchange.job.handler.fileType.notNull");
            }
        }
    }


    @Override
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        TransportType transportType = TransportType.type(
                String.valueOf(dataFormParams.getOrDefault(JobConstants.CONFIG_TRANSPORT_TYPE, "")));
        if(transportType == TransportType.RECORD){
            String fileType = String.valueOf(dataFormParams.getOrDefault(HDFS_FILETYPE, ""));
            if(!StringUtils.isBlank(fileType) && !isSupport(fileType, SUPPORT_READ_FILE_TYPE)){
                throw new JobDataParamsInValidException("exchange.job.handler.fileType.notSupport");
            }
        }
    }

    @Override
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        TransportType transportType = TransportType.type(
                String.valueOf(dataFormParams.getOrDefault(JobConstants.CONFIG_TRANSPORT_TYPE, "")));
        if(transportType == TransportType.RECORD){
            String fileType = String.valueOf(dataFormParams.getOrDefault(HDFS_FILETYPE, ""));
            if(!StringUtils.isBlank(fileType) && !isSupport(fileType, SUPPORT_WRITE_FILE_TYPE)){
                throw new JobDataParamsInValidException("exchange.job.handler.fileType.notSupport");
            }
        }
        String fileName = String.valueOf(dataFormParams.getOrDefault(HDFS_FILE_NAME, ""));
        if(StringUtils.isNotBlank(fileName)){
            dataFormParams.put(HDFS_FILE_NAME, StringEscapeUtils.escapeEcmaScript(avoidSeparator(fileName)));
        }
    }

    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {
        String fieldDelimiter = String.valueOf(dataFormParams.getOrDefault(HDFS_FIELD_DELIMITER, ""));
        if(StringUtils.isNotEmpty(fieldDelimiter)){
            if((fieldDelimiter = StringEscapeUtils.unescapeJava(fieldDelimiter)).length() > 1){
                throw new JobDataParamsInValidException("exchange.job.handler.delimiter.single");
            }
            dataFormParams.put(HDFS_FIELD_DELIMITER, Unicode.unicodeToString(fieldDelimiter));
        }
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(5);
        result.put(HDFS_PATH, dataConfParams.get(HDFS_PATH));
        result.put(HDFS_FILETYPE, dataConfParams.getOrDefault(HDFS_FILETYPE, ""));
        Object delimiter = dataConfParams.getOrDefault(HDFS_FIELD_DELIMITER, "");
        if(StringUtils.isNotBlank(String.valueOf(delimiter))){
            result.put(HDFS_FIELD_DELIMITER, StringEscapeUtils.escapeJava(String.valueOf(delimiter)));
        }
        result.put(HDFS_COMPRESS, dataConfParams.getOrDefault(HDFS_COMPRESS, ""));
        result.put(HDFS_ENCODING, dataConfParams.getOrDefault(HDFS_ENCODING, ""));
        return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(HDFS_FILE_NAME, dataConfParams.getOrDefault(HDFS_FILE_NAME, ""));
        result.put(HDFS_WRITE_MODE, dataConfParams.getOrDefault(HDFS_WRITE_MODE, ""));
        return result;
    }
}
