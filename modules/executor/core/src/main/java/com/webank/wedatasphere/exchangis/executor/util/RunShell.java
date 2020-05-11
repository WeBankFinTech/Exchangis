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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by devendeng on 2018/9/11.
 */
public class RunShell {
    private static Logger log = LoggerFactory.getLogger(RunShell.class);
    private List<String> cmd;
    private Process process;
    private int exitCode = -999;

    public RunShell(String command) {
        cmd = new ArrayList<>();
        cmd.add("/bin/bash");
        cmd.add("-c");
        cmd.add(command);

    }

    public static ProcessBuilder createProcBuilder(String command,
                                                   Map<String, String> env, File workDir){

        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
        if(null != env){
            builder.environment().putAll(env);
        }
        builder.directory(workDir);
        return builder;
    }

    public int run() {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        try {
            process = builder.start();
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            if(e instanceof InterruptedException){
                Thread.currentThread().interrupt();
            }
            log.error("Run shell: [" + cmd +"] Error", e);
        }
        return exitCode;
    }

    public String getResult(){
        StringBuilder result = new StringBuilder();
        BufferedReader input;
        try {
            if (exitCode == 0) {
                 input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } else {
                input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
        }catch(IOException e){
            String message = "Get result from cmd: ["+ cmd+"] Error";
            throw new RuntimeException(message, e);
        }
        return result.toString().trim();
    }

}
