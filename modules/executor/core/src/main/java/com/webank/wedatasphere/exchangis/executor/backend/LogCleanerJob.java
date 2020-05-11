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

package com.webank.wedatasphere.exchangis.executor.backend;

import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @author davidhua
 * 2020/3/28
 */
public class LogCleanerJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(LogCleanerJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ExecutorConfiguration configuration = null;
        try {
            configuration = AppUtil.getBean(ExecutorConfiguration.class);
        }catch(Exception e){
            //Ignore
        }
        if(null == configuration){
            return;
        }
        String historyPath = configuration.getJobLogHistory();
        Long limitNum = configuration.getJobLogLimitNum();
        if(StringUtils.isNotBlank(historyPath)){
            File historyDir = new File(historyPath);
            if(historyDir.isDirectory()){
                List<File> logFiles = new ArrayList<>(FileUtils.listFiles(historyDir, new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return true;
                    }

                    @Override
                    public boolean accept(File file, String s) {
                        return true;
                    }
                }, null));
                LOG.info("Scan to clean history log in [" + historyPath + "], actual number: [" + logFiles.size() + "/" + limitNum + "]");
                StringBuilder cleanPrint = new StringBuilder("");
                if(logFiles.size() > limitNum) {
                    long count = 0;
                    //First to do sort
                    logFiles.sort((o1, o2) -> {
                        long diff = o1.lastModified() - o2.lastModified();
                        if (diff > 0)
                            return -1;
                        else if (diff == 0)
                            return 0;
                        else
                            return 1;
                    });
                    for(int i = Math.toIntExact(limitNum); i < logFiles.size(); i ++, count++){
                        File f = logFiles.get(i);
                        if(!f.delete()){
                            LOG.trace("Fail to delete logFile:[" + f.getName() +"] in history log directory");
                        }else{
                            if(count == 0){
                                cleanPrint.append(f.getName());
                            }else if(count < 10){
                                cleanPrint.append(",").append(f.getName());
                            }
                        }
                    }
                    LOG.info("Final to delete [" + count + "] logs, [" + cleanPrint.toString() + " ...]");
                }
            }
        }
    }
}
