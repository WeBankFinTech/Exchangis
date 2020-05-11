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

package com.webank.wedatasphere.exchangis.executor.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author davidhua
 * 2019/12/23
 */
public class WorkSpace {
    private static final Logger LOG = LoggerFactory.getLogger(WorkSpace.class);

    public static String createLocalDir(String baseDir, long jobId, long taskId){
        String directory = baseDir + IOUtils.DIR_SEPARATOR_UNIX + jobId + "_" + taskId;
        File workDir = new File(directory);
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new RuntimeException("Create workspace directory: " + workDir + " failed");
        }
        return directory;
    }

    public static String createLocalDirIfNotExist(String baseDir, long jobId, long taskId){
        String directory = baseDir + IOUtils.DIR_SEPARATOR_UNIX + jobId + "_" + taskId;
        File workDir = new File(directory);
        if(workDir.mkdirs()){
            LOG.trace("Create directory: " + directory);
        }
        return directory;
    }

    public static String getLocalSpace(String baseDir, long jobId, long taskId){
        return baseDir + IOUtils.DIR_SEPARATOR_UNIX + jobId + "_" + taskId;
    }

    /**
     * Must be an empty directory
     * @param space
     */
    public static boolean deleteLocalSpace(String space){
        if(StringUtils.isNotBlank(space)) {
            return new File(space).delete();
        }
        return false;
    }
}
