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

package com.webank.wedatasphere.exchangis.executor.task.process.sqoop;

import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.JobUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.executor.service.CallBackService;
import com.webank.wedatasphere.exchangis.executor.task.AbstractTaskConfigBuilder;
import com.webank.wedatasphere.exchangis.executor.task.TaskConfigBuilder;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Build task configuration for SQOOP
 * @author davidhua
 * 2019/12/18
 */
@Service(TaskConfigBuilder.PREFIX + "sqoop")
public class SqoopTaskConfigBuilder extends AbstractTaskConfigBuilder {

    public static final String CONFIG_COMMAND = "command";

    private static final String READER_PARAM_PATH = "reader";
    private static final String WRITER_PARAM_PATH = "writer";
    private static final String TOOL_PARAM_PATH = "tool";
    private static final String SETTINGS_PARAM_PATH = "settings";

    private static final String OPTION_KEY_PATTERN = "[-]{1,2}[.\\d\\w_-]+";
    private static final Pattern OPTION_PARAM_PATTERN = Pattern.compile("\\{(\\w+)}?");
    @Resource
    private CallBackService callBackService;

    @Override
    public TaskConfiguration build(long jobId, long taskId, String jobConfig, Map<String, Object> taskParams) {
        String taskConfJson;
        if(null != taskParams.get(DefaultParams.Task.PARAM_DATE)){
            long time = Long.parseLong(String.valueOf(taskParams.get(DefaultParams.Task.PARAM_DATE)));
            taskConfJson = JobUtils.render(jobConfig, time);
        }else{
            taskConfJson = JobUtils.render(jobConfig);
        }
        TaskConfiguration taskConfig = TaskConfiguration.from(taskConfJson);
        Map<String, String> optionMap = new TreeMap<>(
                String::compareTo
        );
        injectDataSourceParamToConf(callBackService, taskConfig, READER_PARAM_PATH);
        injectDataSourceParamToConf(callBackService, taskConfig, WRITER_PARAM_PATH);
        StringBuilder builder =  new StringBuilder();
        parseTaskConfig(taskConfig, "", TOOL_PARAM_PATH, optionMap);
        if(optionMap.size() !=  1){
            throw new IllegalArgumentException("Cannot find the SQOOP Tool option: [" + Json.toJson(optionMap.toString(), null) +"]");
        }
        optionMap.forEach((key, value) -> builder.append(" ").append(value).append(" "));
        optionMap.clear();
        parseTaskConfig(taskConfig, READER_PARAM_PATH, OPTION_KEY_PATTERN, optionMap);
        parseTaskConfig(taskConfig, WRITER_PARAM_PATH, OPTION_KEY_PATTERN, optionMap);
        parseTaskConfig(taskConfig, SETTINGS_PARAM_PATH, OPTION_KEY_PATTERN, optionMap);
        taskParams.forEach((key, value) ->{
            if(key.matches(OPTION_KEY_PATTERN)){
                optionMap.put(key, String.valueOf(value));
            }
        });
        try {
            String password = optionMap.get("--password");
            if (StringUtils.isNotBlank(password)) {
                optionMap.put("--password", String.valueOf(CryptoUtils.string2Object(password)));
            }
        }catch(Exception e){
            //ignore
        }
        optionMap.forEach((key, value) -> builder.append(" ").append(key).append(" ").append(value).append(" "));
        taskConfig.set(CONFIG_COMMAND, builder.toString());
        return taskConfig;
    }

    /**
     * Parse task configuration and build option map
     * @param jobConfig
     * @param path
     * @param optionMap
     */
    private void parseTaskConfig(TaskConfiguration jobConfig, String path, String pattern,
                                 Map<String, String> optionMap){
        TaskConfiguration subConf = jobConfig.getConfiguration(path);
        Set<String> keys = subConf.getKeys(1);
        keys.forEach(key ->{
            if(key.trim().matches(pattern)){
                String jsonStr = subConf.getString(key, "{}").trim();
                List<SqoopOptionVo> sqoopOptionVos = new ArrayList<>();
                if(jsonStr.startsWith("[") && jsonStr.endsWith("]")){
                     sqoopOptionVos = Json.fromJson(jsonStr, SqoopOptionVo.class);
                }else {
                     sqoopOptionVos.add(Json.fromJson(jsonStr, SqoopOptionVo.class));
                }
                assert sqoopOptionVos != null;
                sqoopOptionVos.forEach(sqoopOptionVo -> {
                    assert sqoopOptionVo != null;
                    Pair<String, String> option = parseToOption(key, sqoopOptionVo, subConf);
                    if(StringUtils.isNotBlank(option.getLeft())){
                        optionMap.put(option.getLeft(), option.getRight());
                    }
                });
            }
        });
    }

    /**
     * Parse the option object to option parameter
     * @param optionName
     * @param sqoopOptionVo
     * @param configuration
     * @return
     */
    private Pair<String, String> parseToOption(String optionName, SqoopOptionVo sqoopOptionVo, TaskConfiguration configuration){
        if(StringUtils.isNotBlank(sqoopOptionVo.getName())){
            //replace option name
            optionName = sqoopOptionVo.getName();
        }
        MutablePair<String, String> option = new MutablePair<>();
        Map<String, String> relateMap = sqoopOptionVo.getCondition();
        for(Map.Entry<String, String> entry : relateMap.entrySet()){
            String value = configuration.getString(entry.getKey());
            boolean match = (StringUtils.isBlank(value) && StringUtils.isBlank(entry.getValue())) ||
                    (StringUtils.isNotBlank(value) && value.matches(entry.getValue()));
            if(match){
                continue;
            }
            return option;
        }
        String value = sqoopOptionVo.getValue();
        if(StringUtils.isNotBlank(value)){
            Map<String, Object> params = sqoopOptionVo.getParams();
            Matcher matcher = OPTION_PARAM_PATTERN.matcher(value);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                Object paramValue = params.getOrDefault(matcher.group(1), "");
                String result;
                if(paramValue instanceof List){
                    result = StringUtils.join((List)paramValue, ",");
                }else if (paramValue instanceof Map){
                    result = Json.toJson(paramValue, null);
                }else{
                    result = String.valueOf(paramValue);
                }
                if (StringUtils.isNotBlank(result)) {
                    matcher.appendReplacement(sb, result);
                } else {
                    matcher.appendReplacement(sb, "");
                }
            }
            matcher.appendTail(sb);
            value = sb.toString();
        }
        if(StringUtils.isNotBlank(value) || sqoopOptionVo.isRequire()){
            option.setLeft(optionName);
            option.setRight(StringUtils.isBlank(value)?"":value);
        }
        return option;
    }
}
