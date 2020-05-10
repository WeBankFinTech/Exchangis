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

package com.webank.wedatasphere.exchangis.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author davidhua
 * 2020/4/19
 */
public class ProcessUtil{
    /**
     * Mount PID file
     * @param pidFile
     * @throws IOException
     */
    public static void mountPIDFile(String pidFile) throws IOException {
        if(StringUtils.isNotBlank(pidFile)){
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0].trim();
            Files.write(Paths.get(pidFile), pid.getBytes());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Files.delete(Paths.get(pidFile));
                } catch (IOException e) {
                    //Ignore
                }
            }));
        }
    }
}
