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

import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/5/20
 */
@Service(JobDataConfHandler.PREFIX + "sftp")
public class SftpJobDataConfHandler extends AbstractJobDataConfHandler{
    private static final Logger LOG = LoggerFactory.getLogger(SftpJobDataConfHandler.class);

    private static final String SFTP_PATH = "path";
    private static final String SFTP_COMPRESS = "compress";
    private static final String SFTP_ENCODING = "encoding";
    private static final String SFTP_FIELD_DELIMITER = "fieldDelimiter";
    private static final String SFTP_FILE_NAME = "fileName";
    private static final String SFTP_TRANSIT = "transit";
    private static final String SFTP_WRITE_MODE = "writeMode";

    private static final String[] SUPPORT_COMPRESS_READER = new String[]{"zip", "gzip", "bzip2"};
    @Override
    protected String[] connParamNames() {
        return new String[]{Constants.PARAM_DEFAULT_PASSWORD,
                Constants.PARAM_DEFAULT_USERNAME, Constants.PARAM_SFTP_HOST,
                Constants.PARAM_SFTP_PORT
        };
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        String path = String.valueOf(dataFormParams.getOrDefault(SFTP_PATH, ""));
        if(StringUtils.isBlank(path)){
            throw new JobDataParamsInValidException("exchange.job.handler.path.notNull");
        }
        if(!new File(path).isAbsolute()){
            throw new JobDataParamsInValidException("exchange.job.handler.path.absolute");
        }
        try{
            Charsets.toCharset(String.valueOf(dataFormParams.getOrDefault(SFTP_ENCODING, Charsets.UTF_8.displayName())));
        }catch(Exception e){
            throw new JobDataParamsInValidException("exchange.job.handler.encoding.wrong");
        }
    }
    @Override
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        String compress = String.valueOf(dataFormParams.getOrDefault(SFTP_COMPRESS, ""));
        if(StringUtils.isNotBlank(compress)){
            boolean isSupport = isSupport(compress, SUPPORT_COMPRESS_READER);
            if(!isSupport){
                throw new JobDataParamsInValidException("exchange.job.handler.compress.notSupport");
            }
        }
    }

    @Override
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        String fileName = String.valueOf(dataFormParams.getOrDefault(SFTP_FILE_NAME, ""));
        if(StringUtils.isNotBlank(fileName)){
            dataFormParams.put(SFTP_FILE_NAME, StringEscapeUtils.escapeEcmaScript(avoidSeparator(fileName)));
        }
    }

    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {
        String fieldDelimiter = String.valueOf(dataFormParams.getOrDefault(SFTP_FIELD_DELIMITER, ""));
        if(StringUtils.isNotEmpty(fieldDelimiter)){
            if((fieldDelimiter = StringEscapeUtils.unescapeJava(fieldDelimiter)).length() > 1){
                throw new JobDataParamsInValidException("exchange.job.handler.delimiter.single");
            }
            dataFormParams.put(SFTP_FIELD_DELIMITER, fieldDelimiter);
        }
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(4);
        result.put(SFTP_PATH, dataConfParams.get(SFTP_PATH));
        result.put(SFTP_COMPRESS, dataConfParams.getOrDefault(SFTP_COMPRESS, ""));
        result.put(SFTP_ENCODING, dataConfParams.getOrDefault(SFTP_ENCODING, ""));
        result.put(SFTP_FIELD_DELIMITER, StringEscapeUtils.escapeJava(
                String.valueOf(dataConfParams.getOrDefault(SFTP_FIELD_DELIMITER, ""))));
        result.put(SFTP_TRANSIT, dataConfParams.getOrDefault(SFTP_TRANSIT, true));
        return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(SFTP_FILE_NAME, dataConfParams.getOrDefault(SFTP_FILE_NAME, ""));
        result.put(SFTP_WRITE_MODE, dataConfParams.getOrDefault(SFTP_WRITE_MODE, ""));
        return result;
    }

    @Override
    protected Map<String, Object> postGetReader(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(Constants.PARAM_SKIP_HEADER, Boolean.parseBoolean(
                String.valueOf(dataConfParams.getOrDefault(Constants.PARAM_SKIP_HEADER, false))));
        return result;
    }

}
