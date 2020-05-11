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

package com.webank.wedatasphere.exchangis.executor.task.process;

import com.webank.wedatasphere.exchangis.common.util.MemUtils;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.JobContainer;
import com.webank.wedatasphere.exchangis.executor.exception.TaskResAllocException;
import com.webank.wedatasphere.exchangis.executor.exception.TaskTimeoutException;
import com.webank.wedatasphere.exchangis.executor.resource.Resource;
import com.webank.wedatasphere.exchangis.executor.task.log.DefaultLocalTaskLog;
import com.webank.wedatasphere.exchangis.executor.task.TaskLog;
import com.webank.wedatasphere.exchangis.executor.util.RunShell;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import com.webank.wedatasphere.exchangis.executor.util.WorkSpace;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author davidhua
 * 2019/2/20
 */
public abstract class AbstractJavaInternalTaskProcess extends AbstractTaskProcess {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJavaInternalTaskProcess.class);
    private static final String WINDOWS_TAG = "window";

    private static final int WAIT_FOR_ALLOCATE_TIME = 5;
    private static final int CHECK_ALLOCATE_INTERVAL = 200;

    private Process process;

    private TaskLog taskLog;
    public AbstractJavaInternalTaskProcess(long jobId, long taskId, String execUser,
                                    TaskConfiguration taskConfig, ExecutorConfiguration execConfig,
                                    Map<String, Object> engineParams){
        super(jobId, taskId, execUser, taskConfig, execConfig, engineParams);
    }
    protected final Process getProcess(){
        return process;
    }

    @Override
    public void init() {
        workDir = new File(WorkSpace.createLocalDirIfNotExist(getExecConfig().getJobLogDir(),
                getJobId(),  getTaskId()));
        taskLog = new DefaultLocalTaskLog(new File(workDir, logFileName()));
        initInternal();
        String execUser = getExecUser();
        //try to switch user, change the owner of working directory
        if(needToSwitchUser(execUser)){
            int code = changeOwnerRecursive(workDir, execUser);
            if(code != 0){
                throw new RuntimeException("Task: " + getTaskId() + " switch to user:'" + execUser + "' failed");
            }
        }
    }

    @Override
    public void clean() {
        if(null == workDir){
            workDir = new File(WorkSpace.getLocalSpace(getExecConfig().getJobLogDir(),
                    getJobId(), getTaskId()));
        }
        if(workDir.exists()){
            try {
                clean(workDir);
            }finally{
                deleteRecurse(workDir);
            }
        }
    }

    @Override
    public final int execute() {
        process = executeInternal();
        if(null == process){
            throw new TaskResAllocException("process == null");
        }
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            //If is necessary ?
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    @Override
    public Future<Integer> executeAsync() {
        try {
            process = executeInternal();
            if (process == null){
                throw new TaskResAllocException("process == null");
            }
            long end = System.currentTimeMillis() + WAIT_FOR_ALLOCATE_TIME * 1000;
            boolean allocate = checkIfAlloc();
            boolean alive = true;
            while(!allocate  && System.currentTimeMillis() < end){
                Thread.sleep(CHECK_ALLOCATE_INTERVAL);
                allocate = checkIfAlloc();
                if (!alive){
                    break;
                }
                alive = process.isAlive();
            }
            if(!allocate){
                throw new TaskResAllocException();
            }
            return new JavaProcessFuture();
        }catch(Exception e){
            throw new TaskResAllocException(e.getMessage(), e);
        }
    }

    @Override
    public LogResult log(long startLine, long windSize) {
        if(null != taskLog){
            return taskLog.fetchLog(startLine, windSize);
        }
        return new LogResult((int)startLine, -1, "Cannot read log, this task doesn't init", true);
    }

    @Override
    public final void destroy() {
        if(null != process) {
            try {
                destroy(process);
            } finally {
                if (!process.isAlive()) {
                    process = null;
                }
            }
        }
    }

    @Override
    public Resource getResource() {
        return new Resource(getTaskId(), 0, 0);
    }

    @Override
    public boolean isAlive() {
        return null != process && process.isAlive();
    }


    private class JavaProcessFuture implements Future<Integer>{

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if(mayInterruptIfRunning){
                process.destroy();
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public boolean isDone() {
            return !process.isAlive();
        }

        @Override
        public Integer get() throws InterruptedException, ExecutionException {
            return process.waitFor();
        }

        @Override
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            boolean result = process.waitFor(timeout, unit);
            if(!result){
                throw new TaskTimeoutException();
            }
            return process.waitFor();
        }
    }


    @Override
    public InputStream getInputStream() {
        if(process == null){
            return null;
        }
        return process.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        if(process == null){
            return null;
        }
        return process.getErrorStream();
    }

    private int changeOwnerRecursive(File workDir, String execUser){
        int code = 0;
        try{
            ProcessBuilder builder = RunShell.createProcBuilder(StringUtils.join(new String[]{"sudo chown -R ", execUser,
                workDir.getPath()}, " "), null, workDir);
            code = builder.start().waitFor();
        }catch(IOException e){
            throw new RuntimeException("IO error while switching user", e);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return code;
    }

    private int getProcessId(Process process){
        int processId = -1;
        try{
            File  pidFile = new File(workDir + File.separator + pidFileName());
            if(pidFile.exists()){
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(pidFile), "UTF-8"))) {
                    processId = Integer.parseInt(reader.readLine());
                } catch (IOException e) {
                    LOG.error("Read 'pid' file error: " + e.getMessage());
                } catch (Exception e) {
                    LOG.error("Get pid error:" + e.getMessage());
                }
                if (processId < 0 && process != null) {
                    Field f = process.getClass().getDeclaredField("pid");
                    f.setAccessible(true);
                    processId = f.getInt(process);
                }
            }
        }catch(Throwable throwable){
            LOG.error("Pid get occur exception: " + throwable.getMessage(), throwable);
        }
        return processId;
    }

    private void deletePidFile(){
        File pidFile = new File(workDir + File.separator + pidFileName());
        if(pidFile.exists()){
            deleteRecurse(pidFile);
        }
    }

    private void deleteRecurse(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(null != files){
                for(File f : files){
                    deleteRecurse(f);
                }
            }
            if(!file.delete() && file.exists()){
                LOG.error("IO_ERROR: cannot delete directory: {}, please check the permission", file.getAbsolutePath());
            }
        }else{
            if(!file.delete() && file.exists()){
                LOG.error("IO_ERROR: cannot delete file: {}, please check the permission", file.getAbsolutePath());
            }
        }
    }

    protected void killProcess(int pid) throws IOException, InterruptedException {
        String osName = System.getProperty("os.name");
        if(StringUtils.isNotBlank(osName) && osName.toLowerCase().contains(WINDOWS_TAG)){
            // kill all process (sub) windows
            Runtime.getRuntime().exec("taskkill -PID" + pid + " -T -F");
        }else{
            LOG.debug("Attempt to kill the process with cmd [kill -SIGTERM ]");
            String userDir = System.getProperty("user.dir", "");
            int code = Runtime.getRuntime().exec(userDir + "/bin/task-kill.sh " + pid).waitFor();
            if(code != 0){
                throw new RuntimeException("Return code is " + code);
            }
        }
    }
    /**
     * destroy internally
     * @param process java process object
     */
    protected void destroy(@Nullable Process process){
        try{
            int pid = getProcessId(process);
            if(pid != -1){
                killProcess(pid);
                //delete pid
                deletePidFile();
                LOG.info("Destroy job {} taskId {} complete.", getJobId(), getTaskId());
            }
        }catch (Exception e){
            LOG.error("Kill process of task{} error.", getTaskId(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * clean local working directory
     * @param workDir
     */
    protected void clean(File workDir){
        if(needToSwitchUser(getExecUser())){
            changeOwnerRecursive(workDir, System.getProperty("user.name", ""));
        }
        LOG.info("Clean the job {} task {} working directory: {}", getJobId(), getTaskId(), workDir.getPath());
        File[] files = workDir.listFiles();
        if(null != files){
            for(File file : files){
                if(file.getName().equals(logFileName())){
                    File dist = new File(getExecConfig().getJobLogHistory(), getJobId() + "_" + getTaskId() + ".log");
                    try{
                        FileUtils.copyFile(file, dist);
                    }catch(IOException e){
                        throw new RuntimeException("IO_ERROR: copy file: " + file.getAbsolutePath() +
                                " to dist: " + dist.getAbsolutePath(), e);
                    }
                }
                if(file.getName().equals(pidFileName())){
                    try {
                        //try to kill process
                        String pidStr = FileUtils.readFileToString(file);
                        if(StringUtils.isNotBlank(pidStr)){
                            killProcess(Integer.parseInt(pidStr.trim()));
                        }
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
    }

    protected boolean needToSwitchUser(String execUser){
        String procUser = System.getProperty("user.name", "");
        return StringUtils.isNotBlank(execUser) &&
                procUser.equals(getExecConfig().getLinuxSudoUser()) &&
                !execUser.equals(procUser);
    }

    protected boolean checkIfAlloc(){
        return true;
    }

    /**
     * If allocate resource (memory) succeed
     * @return result
     */
    protected  boolean isAlloc(){
        return true;
    }

    /**
     * Get log file name
     * @return log file name
     */
    protected String logFileName(){
        return JobContainer.LOG_SYS_NAME;
    }

    /**
     * Get pid file name
     * @return pid file name
     */
    protected String pidFileName(){
        return JobContainer.PID_NAME;
    }
    /**
     * Init
     */
    protected abstract void initInternal();
    /**
     * Execute internally
     * @return java process object
     */
    protected abstract Process executeInternal();

    /**
     * Java cmd builder
     */
    protected static final class JavaCommandBuilder{

        private static final String DEFAULT_CLASSPATH_SEPARATOR ;
        private static final MutablePair<String, String> HEAP_DUMP = new MutablePair<>("", "-XX:+HeapDumpOnOutOfMemoryError");
        private static final MutablePair<String, String> FILE_ENCODING_PROP = new MutablePair<>("-Dfile.encoding", "UTF-8");
        private static final MutablePair<String, String> SECURITY_EGD = new MutablePair<>("-Djava.security.egd", "file:///dev/urandom");
        /**
         * Java execute Path
         */
        private String javaPath;

        /**
         * Main class
         */
        private String mainClass;
        /**
         * Classpath
         */
        private MutablePair<String, String> classPath = new MutablePair<>("-classpath", ".");
        /**
         * Xmx
         */
        private Triple<String, Long, MemUtils.StoreUnit> jvmXmx;
        /**
         * Xms
         */
        private Triple<String, Long, MemUtils.StoreUnit> jvmXms;
        /**
         * Default props
         */
        private List<Pair<String, String>> props = new ArrayList<>();

        /**
         * Command props
         */
        private List<Pair<String, String>> cmdProps = new ArrayList<>();

        static{
            String osName = System.getProperty("os.name", "").toLowerCase();
            if(osName.indexOf("windows") > 0){
                DEFAULT_CLASSPATH_SEPARATOR = ";";
            }else{
                DEFAULT_CLASSPATH_SEPARATOR = ":";
            }
        }
        public JavaCommandBuilder(String javaPath, String mainClass){
            this.javaPath = javaPath;
            this.mainClass = mainClass;
            this.props.add(HEAP_DUMP);
            this.props.add(FILE_ENCODING_PROP);
            this.props.add(SECURITY_EGD);
        }

        public JavaCommandBuilder Xmx(long size, MemUtils.StoreUnit unit){
            this.jvmXmx = new MutableTriple<>("-Xmx", size, unit);
            return this;
        }

        public JavaCommandBuilder Xms(long size, MemUtils.StoreUnit unit){
            this.jvmXms = new MutableTriple<>("-Xms", size, unit);
            return this;
        }

        public JavaCommandBuilder classPath(String name){
            //Just support linux
            this.classPath.setRight(name + DEFAULT_CLASSPATH_SEPARATOR + this.classPath.getRight());
            return this;
        }
        public JavaCommandBuilder prop(String name, String value){
            props.add(new MutablePair<>(name, value));
            return this;
        }

        public JavaCommandBuilder prop(String value){
            props.add(new MutablePair<>("", value));
            return this;
        }

        public JavaCommandBuilder prop(MutablePair<String, String> pair){
            props.add(pair);
            return this;
        }

        public JavaCommandBuilder cmdProp(String name, String value){
            cmdProps.add(new MutablePair<>(name, value));
            return this;
        }

        public JavaCommandBuilder cmdProp(String value){
            cmdProps.add(new MutablePair<>("", value));
            return this;
        }

        public JavaCommandBuilder cmdProp(MutablePair<String, String> pair){
            cmdProps.add(pair);
            return this;
        }

        public String build(){
            List<String> commandSliceList = new ArrayList<>();
            commandSliceList.add(javaPath);
            commandSliceList.add(jvmXmx.getLeft() + jvmXmx.getRight().toMB(jvmXmx.getMiddle()) + "m");
            commandSliceList.add(jvmXms.getLeft() + jvmXms.getRight().toMB(jvmXms.getMiddle()) + "m");
            commandSliceList.add(classPath.getLeft());
            commandSliceList.add(classPath.getRight());
            props.forEach(prop -> {
                if(StringUtils.isNotBlank(prop.getLeft())) {
                    commandSliceList.add(prop.getLeft() + "=" + prop.getRight());
                }else {
                    commandSliceList.add(prop.getRight());
                }
            });
            commandSliceList.add(mainClass);
            cmdProps.forEach(prop -> {
                if(StringUtils.isNotBlank(prop.getLeft())) {
                    commandSliceList.add(prop.getLeft() + " " + prop.getRight());
                }else {
                    commandSliceList.add(prop.getRight());
                }
            });
            return StringUtils.join(commandSliceList, " ");
        }
    }
}
