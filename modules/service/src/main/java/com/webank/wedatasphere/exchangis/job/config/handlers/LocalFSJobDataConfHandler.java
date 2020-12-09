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
 * 2019/3/21
 */
@Service(JobDataConfHandler.PREFIX +"local_fs")
public class LocalFSJobDataConfHandler extends AbstractJobDataConfHandler{
    private static final Logger logger = LoggerFactory.getLogger(LocalFSJobDataConfHandler.class);

    private static final String FS_ENCODING="encoding";
    private static final String FS_COMPRESS="compress";
    private static final String FS_PATH="path";
    private static final String FS_FILE_NAME="fileName";
    private static final String FS_FIELD_DELIMITER="fieldDelimiter";
    private static final String FS_WRITE_MODE="writeMode";

    private static final String[] SUPPORT_COMPRESS_WRITER = new String[]{ "gzip", "bzip2"};

    private static final String[] SUPPORT_COMPRESS_READER = new String[]{"zip", "gzip", "bzip2"};
  @Override
    protected String[] connParamNames() {
        return new String[0];
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        String path = String.valueOf(
                dataFormParams.getOrDefault(FS_PATH, "")
        );
        if(StringUtils.isBlank(path)){
            throw new JobDataParamsInValidException("exchange.job.handler.path.notNull");
        }
        if(!new File(path).isAbsolute()){
            throw new JobDataParamsInValidException("exchange.job.handler.path.absolute");
        }
        String encoding = String.valueOf(dataFormParams.getOrDefault(FS_ENCODING, ""));
        if(StringUtils.isNotBlank(encoding)){
            try{
                Charsets.toCharset(encoding);
            }catch(Exception e){
                throw new JobDataParamsInValidException("exchange.job.handler.encoding.wrong");
            }
        }
    }

    @Override
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
//        if(StringUtils.isBlank(String.valueOf(
//                dataFormParams.getOrDefault(FS_FILE_NAME, "")
//        ))){
//            throw new JobDataParamsInValidException("写入文件名不能为空");
//        }
        String compress = String.valueOf(dataFormParams.getOrDefault(FS_COMPRESS, ""));
        if(StringUtils.isNotBlank(compress)){
            boolean isSupport = isSupport(compress, SUPPORT_COMPRESS_WRITER);
            if(!isSupport){
                throw new JobDataParamsInValidException("exchange.job.handler.compress.notSupport");
            }
        }
        String fileName = String.valueOf(dataFormParams.getOrDefault(FS_FILE_NAME, ""));
        if(StringUtils.isNotBlank(fileName)){
            dataFormParams.put(FS_FILE_NAME, StringEscapeUtils.escapeEcmaScript(
                    avoidSeparator(fileName)));
        }
    }
    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {
        String fieldDelimiter = String.valueOf(dataFormParams.getOrDefault(FS_FIELD_DELIMITER, ""));
        if(StringUtils.isNotEmpty(fieldDelimiter)){
            if((fieldDelimiter = StringEscapeUtils.unescapeJava(fieldDelimiter)).length() > 1){
                throw new JobDataParamsInValidException("exchange.job.handler.delimiter.single");
            }
            dataFormParams.put(FS_FIELD_DELIMITER, fieldDelimiter);
        }
    }
    @Override
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        String compress = String.valueOf(dataFormParams.getOrDefault(FS_COMPRESS, ""));
        if(StringUtils.isNotBlank(compress)){
            boolean isSupport = isSupport(compress, SUPPORT_COMPRESS_READER);
            if(!isSupport){
                throw new JobDataParamsInValidException("exchange.job.handler.compress.notSupport");
            }
        }
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
       Map<String, Object> result = new HashMap<>(4);
       result.put(FS_PATH, dataConfParams.get(FS_PATH));
       result.put(FS_COMPRESS, dataConfParams.getOrDefault(FS_COMPRESS, ""));
       result.put(FS_ENCODING, dataConfParams.getOrDefault(FS_ENCODING, ""));
       result.put(FS_FIELD_DELIMITER, StringEscapeUtils.escapeJava(
               String.valueOf(dataConfParams.getOrDefault(FS_FIELD_DELIMITER, ""))));
       return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
       Map<String, Object> result = new HashMap<>(1);
       result.put(FS_FILE_NAME, dataConfParams.getOrDefault(FS_FILE_NAME, ""));
       result.put(FS_WRITE_MODE, dataConfParams.getOrDefault(FS_WRITE_MODE, ""));
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
