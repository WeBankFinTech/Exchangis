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

package com.webank.wedatasphere.exchangis.executor.task.process.datax;

import com.webank.wedatasphere.exchangis.common.util.JobUtils;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.task.AbstractTaskConfigBuilder;
import com.webank.wedatasphere.exchangis.executor.task.TaskConfigBuilder;
import com.webank.wedatasphere.exchangis.executor.util.HttpClientUtil;
import com.webank.wedatasphere.exchangis.executor.service.CallBackService;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import com.webank.wedatasphere.exchangis.executor.util.WorkSpace;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration.SPLIT_CHAR;


/**
 * @author davidhua
 * 2019/2/22
 */
@Service(TaskConfigBuilder.PREFIX + "datax")
public class DataxTaskConfigBuilder extends AbstractTaskConfigBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(DataxTaskConfigBuilder.class);
    private static final String READER_PARAM_PATH = "job.content[0].reader.parameter";
    private static final String WRITER_PARAM_PATH = "job.content[0].writer.parameter";

    @Resource
    private CallBackService callBackService;

    @Resource
    private ExecutorConfiguration execConfig;
    @Override
    public TaskConfiguration build(long jobId, long taskId, String jobConfig, Map<String, Object> params) {
        //Work directory
        String workDir = WorkSpace.createLocalDirIfNotExist(execConfig.getJobLogDir(), jobId, taskId);
        //Render time placeholder in job configuration
        String taskConfig;
        try {
            if (null != params.get(DefaultParams.Task.PARAM_DATE)) {
                long time = Long.parseLong(String.valueOf(params.get(DefaultParams.Task.PARAM_DATE)));
                taskConfig = JobUtils.render(jobConfig, time);
            } else {
                taskConfig = JobUtils.render(jobConfig);
            }
            //Parse to task configuration
            TaskConfiguration configuration = TaskConfiguration.from(taskConfig);
            injectDataSourceParam(workDir, configuration, READER_PARAM_PATH, 0);
            injectDataSourceParam(workDir, configuration, WRITER_PARAM_PATH, 1);
            //Inject increment parameters
            injectIncrementParam(configuration, params, READER_PARAM_PATH);
            LOG.info("Task configuration {}" + configuration.toJson());
            return configuration;
        } catch (RuntimeException e){
            WorkSpace.deleteLocalSpace(workDir);
            throw e;
        }
    }

    private void injectIncrementParam(TaskConfiguration configuration, Map<String, Object> taskParams, String name){
        if(taskParams.containsKey(DefaultParams.Task.PARAM_INCR_BEGIN_TIME)){
            configuration.set(StringUtils.join(new String[]{name, DefaultParams.Task.PARAM_INCR_BEGIN_TIME}, SPLIT_CHAR),
                    taskParams.getOrDefault(DefaultParams.Task.PARAM_INCR_BEGIN_TIME, 0));
        }
        if(taskParams.containsKey(DefaultParams.Task.PARAM_INCR_END_TIME)){
            configuration.set(StringUtils.join(new String[]{name, DefaultParams.Task.PARAM_INCR_END_TIME}, SPLIT_CHAR),
                    taskParams.getOrDefault(DefaultParams.Task.PARAM_INCR_END_TIME, 0));
        }
    }
    private void injectDataSourceParam(String workDir, TaskConfiguration configuration,
                                       String path, int position){
        HttpClientUtil httpClientUtil = HttpClientUtil.getHttpClientUtil();
        super.injectDataSourceParamToConf(callBackService, configuration, path);
        String authType = configuration
                .getString(StringUtils.join(new String[]{path, Constants.PARAM_AUTH_TYPE}, SPLIT_CHAR),"");
        String[] urlKeysToSearch = new String[]{};
        if(AuthType.KERBERS.equals(authType)){
            urlKeysToSearch = new String[]{Constants.PARAM_KB_FILE_PATH};
        }else if(AuthType.KEYFILE.equals(authType)){
            urlKeysToSearch = new String[]{Constants.PARAM_KEY_FILE_PATH};
        }else if(StringUtils.isBlank(authType)){
            //If auth type is null or empty, means that the job configuration doesn't have the data source id,
            // In order to find url key, search all possibilities
            urlKeysToSearch = new String[]{Constants.PARAM_KB_FILE_PATH, Constants.PARAM_KEY_FILE_PATH};
        }
        //Search key path
        for(String searchKey : urlKeysToSearch){
            List<String> keyPaths = TaskConfiguration.searchKeyPaths(configuration, path, searchKey);
            if(!keyPaths.isEmpty()){
                String absKeyPath = StringUtils.join(new String[]{path, keyPaths.get(0)}, SPLIT_CHAR);
                String url = configuration.getString(absKeyPath);
                if(StringUtils.isNotBlank(url)){
                    String localPath = downloadAuthFile(httpClientUtil, url, workDir, position);
                    configuration.set(absKeyPath, localPath);
                }
            }
        }
    }

    private String downloadAuthFile(HttpClientUtil httpClientUtil, String url,
                                    String workDir, int position){
        String localPath = workDir + IOUtils.DIR_SEPARATOR_UNIX +
                Constants.AUTH_FILE_NAME + position;
        httpClientUtil.downLoad(url, localPath);
        return localPath;
    }
}
