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

package com.webank.wedatasphere.exchangis.executor.task.log;

import com.webank.wedatasphere.exchangis.executor.task.TaskLog;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import com.google.common.collect.EvictingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Queue;

/**
 * @author davidhua
 * 2019/12/25
 */
public class DefaultLocalTaskLog implements TaskLog {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLocalTaskLog.class);
    private File localFile;
    public DefaultLocalTaskLog(File localFile){
        this.localFile = localFile;
    }
    @Override
    public LogResult fetchLog(long startLine, long windSize) {
        if(localFile.exists()){
            // Read file
            StringBuilder logContentBuffer = new StringBuilder();
            Queue<String> queue = EvictingQueue.create(Math.abs((int) windSize));
            long toLineNum = 0;
            LineNumberReader reader = null;
            boolean isEnd = false;
            try {
                //reader = new LineNumberReader(new FileReader(logFile));
                reader = new LineNumberReader(new InputStreamReader(new FileInputStream(localFile), "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    toLineNum = reader.getLineNumber();
                    if(windSize >= 0 && toLineNum > startLine){
                        queue.offer(line);
                        if(queue.size() >= windSize){
                            break;
                        }
                    }else if (windSize < 0){
                        if(startLine >= 0 && toLineNum >= startLine){
                            toLineNum = startLine - queue.size();
                            break;
                        }
                        queue.offer(line);
                    }
                }
                if(startLine < 0){
                    if(windSize >= 0){
                        startLine = 0;
                    }else{
                        startLine = toLineNum;
                        toLineNum = startLine - queue.size();
                    }
                }
                while(!queue.isEmpty()){
                    line = queue.poll();
                    logContentBuffer.append(line).append("\n");
                }
                // Result
                return new LogResult((int)startLine, (int)toLineNum, logContentBuffer.toString(), isEnd);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
        return new LogResult((int)startLine, -1, "Read log fail, seems that the node has lost the task process", true);
    }
}
