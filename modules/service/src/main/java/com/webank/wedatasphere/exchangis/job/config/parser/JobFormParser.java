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

package com.webank.wedatasphere.exchangis.job.config.parser;

import com.webank.wedatasphere.exchangis.common.util.MemUtils;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.handlers.JobDataConfHandler;
import com.webank.wedatasphere.exchangis.job.config.AbstractJobTemplate;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.dto.Transform;
import com.webank.wedatasphere.exchangis.job.domain.JobConfForm;
import com.webank.wedatasphere.exchangis.job.domain.JobEngine;
import com.webank.wedatasphere.exchangis.job.domain.JobFunction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.webank.wedatasphere.exchangis.job.JobConstants.DATAX_TEMPLATE_LOC;
import static com.webank.wedatasphere.exchangis.job.JobConstants.DEFAULT_TEMPLATE_LOC;

/**
 * Like <em>JobConfBuilder</em>
 * @author enjoyyin
 * 2018/11/22
 */
public class JobFormParser extends AbstractJobTemplate {

    private static final String INJECT_VALUE_PATTERN_STRING = "\"#{$0}\"";

    private static final Pattern PARSE_KEY_PATTERN = Pattern.compile("#\\{([\\w\\W]+)}");
    private static final Logger logger = LoggerFactory.getLogger(JobFormParser.class);

    private JobConfForm form = new JobConfForm();

    private Map<JobFunction.FunctionType, Map<String, String>> jobFuncNameRefMap = new HashMap<>();

    private String configTemplateLocation;
    private List<Transform> transforms = new ArrayList<>();
    private Map<String, Object> readerDataConfParams = new HashMap<>();
    private Map<String, Object> writerDataConfParams = new HashMap<>();

    private JobFormParser(String configTemplateLocation){
        this.configTemplateLocation = configTemplateLocation;
    }

    public static JobFormParser custom(){
        return new JobFormParser(DATAX_TEMPLATE_LOC);
    }

    public static JobFormParser custom(JobEngine engineEnum){
        return new JobFormParser(null != engineEnum ? DEFAULT_TEMPLATE_LOC + "/" + engineEnum.toString().toLowerCase() : DATAX_TEMPLATE_LOC);
    }

    public static JobFormParser custom(String configTemplateLocation){
        return new JobFormParser(configTemplateLocation);
    }

    public JobFormParser jobFuncNameRef(JobFunction.FunctionType functionType, Map<String, String> funcNameRef){
        jobFuncNameRefMap.put(functionType, funcNameRef);
        return this;
    }
    public JobConfForm parse(){
        return form;
    }

    public JobConfForm parse(String confJson, TypeEnums readerType, TypeEnums writerType){
        long used = System.currentTimeMillis();
        //First to build an model job-config parameter map as a template for parsing
        String jobTemplate = getTemplateContent(this.configTemplateLocation + "/"+ COMMON_TEMPLATE_FILE);
        //Do not use default value
        //Inject template by value pattern
        Map<String, Object> modelParams = Json.fromJson(
                PatternInjectUtils.injectPattern(jobTemplate, INJECT_VALUE_PATTERN_STRING)
                , Map.class);
        //Get actual job-config parameter map, compare it with model map
        Map<String, Object> params = Json.fromJson(confJson, Map.class);
        if(null != modelParams && null != params) {
            compare(modelParams, params, (confName, confValue)->{
                //If reader or writer,compare with their specific template maps
                switch (confName) {
                    case JobConstants.CONFIG_READER_NAME:
                        String readTpl = getTemplateContent(this.configTemplateLocation +
                                READER_DIRECTORY + "/" + readerType.v() + DEFAULT_SUFFIX);
                        if (StringUtils.isNotBlank(readTpl)) {
                            compare(Objects.requireNonNull(Json.fromJson(PatternInjectUtils.inject(readTpl, new HashMap<>(1), false, true, true)
                                    , Map.class)), (Map<String, Object>) confValue, (cfgName, cfgValue) -> readerDataConfParams.put(cfgName, cfgValue));
                        }
                        break;
                    case JobConstants.CONFIG_WRITER_NAME:
                        String writeTpl = getTemplateContent(this.configTemplateLocation +
                                WRITER_DIRECTORY + "/" + writerType.v() + DEFAULT_SUFFIX);
                        if (StringUtils.isNotBlank(writeTpl)) {
                            compare(Objects.requireNonNull(Json.fromJson(PatternInjectUtils.inject(writeTpl, new HashMap<>(1), false, true, true)
                                    , Map.class)), (Map<String, Object>) confValue, (cfgName, cfgValue) -> writerDataConfParams.put(cfgName, cfgValue));
                        }
                        break;
                    default:
                        parse(confName, confValue);
                        break;
                }
            });
        }
        // Build ColumnMaps
        List<JobConfForm.ColumnMap> columnMaps = buildColumnMaps(readerDataConfParams, writerDataConfParams);
        form.setColumnMaps(columnMaps);
        Map<String, Object> readerFormParams =
                postGetHandler(DataConfType.READER, readerType, readerDataConfParams);
        form.setDataSrcParams(readerFormParams);
        Map<String, Object> writerFormParams =
                postGetHandler(DataConfType.WRITER, writerType, writerDataConfParams);
        form.setDataDstParams(writerFormParams);
        logger.trace("used time (in ms):" + (System.currentTimeMillis() - used));
        return form;
    }

    /**
     * Build column map list
     * @param readerDataConfParams
     * @param writerDataConfParams
     * @return
     */
    private List<JobConfForm.ColumnMap> buildColumnMaps(Map<String, Object> readerDataConfParams,
                                                        Map<String, Object> writerDataConfParams){
        List<JobConfForm.ColumnMap> columnMaps = new ArrayList<>();
        if(readerDataConfParams.containsKey(JobConstants.CONFIG_COLUM_NAME)
                && writerDataConfParams.containsKey(JobConstants.CONFIG_COLUM_NAME)){
            List<DataColumn> readerColumns = Json.fromJson(
                    Json.toJson(readerDataConfParams.remove(JobConstants.CONFIG_COLUM_NAME), null),
                    DataColumn.class
            );
            List<DataColumn> writerColumns = Json.fromJson(
                    Json.toJson(writerDataConfParams.remove(JobConstants.CONFIG_COLUM_NAME), null),
                    DataColumn.class
            );
            assert readerColumns != null;
            assert writerColumns != null;
            int readerSize = readerColumns.size();
            int writerSize = writerColumns.size();
            int maxSize = Math.max(readerSize, writerSize);
            for(int i = 0; i < maxSize; i++){
                DataColumn readerColumn = i < readerSize ? readerColumns.get(i) : null;
                DataColumn writerColumn = i < writerSize ? writerColumns.get(i) : null;
                JobConfForm.ColumnMap columnMap = new JobConfForm.ColumnMap();
                if(null != readerColumn) {
                    columnMap.setSrcName(readerColumn.getName());
                    columnMap.setSrcIndex(readerColumn.getIndex());
                }
                if(null != writerColumn) {
                    columnMap.setDstName(writerColumn.getName());
                    columnMap.setDstType(writerColumn.getType());
                }
                columnMaps.add(columnMap);
            }
        }
        injectTransToMap(transforms, columnMaps);
        return columnMaps;
    }

    private void injectTransToMap(List<Transform> transforms, List<JobConfForm.ColumnMap> columnMaps){
        try {
            for (Transform transform : transforms) {
                String name = transform.getName();
                Transform.Parameter parameter = transform.getParameter();
                Map<String, String> funcNameRef = jobFuncNameRefMap.getOrDefault(JobFunction.FunctionType.VERIFY, new HashMap<>());
                if(!funcNameRef.isEmpty() && funcNameRef.containsValue(name)){
                    List<String> paras = parameter.getParas();
                    if(paras.size() < 1){
                        throw new IllegalStateException(name + ", paras size < 1");
                    }
                    if(parameter.getColumnIndex() < columnMaps.size()) {
                        columnMaps.get(parameter.getColumnIndex())
                                .setVerifyFunc(paras.get(0) + "(" + URLDecoder.decode(paras.get(1), "UTF-8") + ")");
                    }
                }else{
                    List<String> paras = parameter.getParas();
                    for(int i = 0; i < paras.size(); i ++){
                        paras.set(i, URLDecoder.decode(paras.get(i), "UTF-8"));
                    }
                    String funcParam = StringUtils.join(paras, ",");
                    if(parameter.getColumnIndex() < columnMaps.size()) {
                        columnMaps.get(parameter.getColumnIndex())
                                .setTransforFunc(name + "(" + funcParam + ")");
                    }
                }
            }
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    private Map<String, Object> postGetHandler(DataConfType confType,
                                               TypeEnums typeEnums,  Map<String, Object> dataConfParams){
        try{
            JobDataConfHandler handler = AppUtil.getBean(JobDataConfHandler.PREFIX + typeEnums.v(),
                    JobDataConfHandler.class);
            dataConfParams = handler.postGetHandle(confType , dataConfParams);
        }catch(Exception e){
            //cannot find the handler
            logger.info(e.getMessage());
        }
        return dataConfParams;
    }
    /**
     * Compare with model and actual map
     * @param model
     * @param actual
     * @param processIfMatch if matched, process
     */
    private void compare(Map<String, Object> model, Map<String, Object> actual,
                         BiConsumer<String, Object> processIfMatch){
        for(Map.Entry<String, Object> entry : model.entrySet()){
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            Object value1 = actual.get(key);
            //same type: Map
            if(value instanceof Map && value1 instanceof Map){
                compare((Map<String, Object>) value, (Map<String, Object>) actual.get(key), processIfMatch);
            }else if(value instanceof List && value1 instanceof List){
                //same type: List
                List l0 = (List)value;
                List l1 = (List)actual.get(key);
                if(l0.size() == l1.size()){
                    for(int i = 0; i < l0.size(); i++){
                        value = l0.get(i);
                        value1 = l1.get(i);
                        if(value instanceof Map && value1 instanceof  Map){
                            compare((Map<String, Object>) value, (Map<String, Object>) value1, processIfMatch);
                        }
                        if(null!= value1 && value instanceof  String){
                            String value0 = String.valueOf(value);
                            Matcher matcher = PARSE_KEY_PATTERN.matcher(value0);
                            if(!matcher.find()){
                                continue;
                            }
                            processIfMatch.accept(matcher.group(1), value1);
                        }
                    }
                }
            }else if(null != value1 && value instanceof String){
                String value0 = String.valueOf(value);
                Matcher matcher = PARSE_KEY_PATTERN.matcher(value0);
                if(!matcher.find()){
                    continue;
                }
                value0 = matcher.group(1);
                //Same key (match), so value0 is the config name, value1 is the config value, process them
                processIfMatch.accept(value0, value1);
            }
        }
    }

    private void parse(String confName, Object confValue){
        switch(confName){
            case JobConstants.CONFIG_SPEED_BYTE_NAME:
                long speed = Long.parseLong(String.valueOf(confValue));
                form.getSpeed().setmBytes((int) (speed * Math.pow(2, -20)));
                break;
            case JobConstants.CONFIG_SPEED_RECORD_NAME:
                int record = Integer.parseInt(String.valueOf(confValue));
                form.getSpeed().setRecord(record);
                break;
            case JobConstants.CONFIG_ERRORLIMIT_RECORD:
                int errorRecord = Integer.parseInt(String.valueOf(confValue));
                form.getErrorLimit().setRecord(errorRecord);
                break;
            case JobConstants.CONFIG_TRANSFORMER_NAME:
                parseTransFormer(Json.toJson(confValue,  null));
                break;
            case JobConstants.CONFIG_TRANSPORT_TYPE:
                form.setTransportType(String.valueOf(confValue));
                break;
            case JobConstants.CONFIG_ADVANCE_MEMORY:
                String mMemory = String.valueOf(confValue);
                //Set to MB
                form.getAdvance().setmMemory((int)MemUtils.convertToMB(Integer.parseInt(mMemory
                        .substring(0, mMemory.length() - 1)), mMemory.substring(mMemory.length() - 1)));
                break;
            case JobConstants.CONFIG_ADVANCE_PARALLEL:
                int mParallel = Integer.parseInt(String.valueOf(confValue));
                form.getAdvance().setmParallel(mParallel);
                break;
            case JobConstants.CONFIG_USE_POSTPROCESSOR:
                form.setUsePostProcess(Boolean.parseBoolean(String.valueOf(confValue)));
                break;
            case JobConstants.CONFIG_SYNC_METADATA:
                form.setSyncMeta(Boolean.parseBoolean(String.valueOf(confValue)));
                break;
            default:
        }
    }

    private void parseTransFormer(String jsonArray){
        this.transforms = Json.fromJson(jsonArray, Transform.class);
    }

}
