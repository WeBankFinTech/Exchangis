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

package com.webank.wedatasphere.exchangis.executor.daemons;

import com.webank.wedatasphere.exchangis.executor.task.TaskProcess;
import com.webank.wedatasphere.exchangis.executor.task.process.TaskProcessUtils;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author davidhua
 * 2019/2/18
 */
public class LoggerDaemon extends AbstractTaskDaemon{
    private InputStream inputStream;
    private String loggerName;
    private boolean immediateFlush = true;
    private Parser parser = new Parser() {
        @Override
        public boolean acceptLogger(String loggerName) {
            return false;
        }

        @Override
        public void parseLoggerOutput(String output) {

        }
    };
    public LoggerDaemon(String daemonName, String loggerName,
                        TaskProcess taskProcess, InputStream inputStream){
        super(daemonName, taskProcess);
        if(taskProcess instanceof Parser){
            Parser parser = (Parser)taskProcess;
            if(parser.acceptLogger(loggerName)){
                this.parser = parser;
            }
        }
        this.inputStream = inputStream;
        this.loggerName = loggerName;
    }
    @Override
    public void run() {
        if(null != this.inputStream) {
            BufferedWriter writer = null;
            File workDir = TaskProcessUtils.getWorkDir(taskProcess);
            Logger fileLogger = logger;
            try{
                if(null != workDir){
                    writer = new BufferedWriter(new FileWriter(new File(workDir, loggerName), true));
                    BufferedWriter finalWriter = writer;
                    fileLogger = (Logger) Proxy.newProxyInstance(logger.getClass().getClassLoader(),
                            new Class[]{Logger.class}, (proxy, method, args) -> {
                                String pattern = "debug|trace|info|error";
                                if(method.getName().matches(pattern)){
                                    finalWriter.write(args[0] + "\n");
                                    if(immediateFlush){
                                        finalWriter.flush();
                                    }
                                    return null;
                                }
                                return method.invoke(proxy, args);
                            });
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    parser.parseLoggerOutput(line);
                    fileLogger.info(line);
                }
            } catch (Exception e) {
                //Ignore
            } finally{
                if(null != writer){
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        //Ignore
                    }
                }
            }
        }
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

    public interface Parser{
        /**
         * If accept
         * @param loggerName
         * @return
         */
        boolean acceptLogger(String loggerName);

        /**
         * Parse
         * @param output output
         */
        void parseLoggerOutput(String output);
    }
}
