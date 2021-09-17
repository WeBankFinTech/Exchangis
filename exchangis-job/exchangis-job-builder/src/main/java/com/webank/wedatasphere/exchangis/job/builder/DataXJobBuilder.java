package com.webank.wedatasphere.exchangis.job.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.datax.domain.LaunchCode;
import com.webank.wedatasphere.exchangis.job.datax.handler.DataxJobHandler;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.domain.Setting;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;
import com.webank.wedatasphere.exchangis.job.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataXJobBuilder implements ExchangisJobBuilder {

    protected static final String HANDLER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.datax.handler";
    protected static final String READER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.datax.reader";
    protected static final String WRITER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.datax.writer";

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Override
    public ExchangisLaunchTask[] buildJob(ExchangisJob job) throws ExchangisDataSourceException {

        String content = job.getContent();
        Long jobId = job.getId();
        ExchangisSubJob[] exchangisSubJobs = gson.fromJson(content, ExchangisSubJob[].class);
        ExchangisLaunchTask[] launchTasks = new ExchangisLaunchTask[exchangisSubJobs.length];

        for (int i = 0; i < exchangisSubJobs.length; i++) {
            ExchangisSubJob subjob = exchangisSubJobs[i];
            LaunchCode code = new LaunchCode();

            code.setContent(generateContents(subjob, jobId));
            code.setSettings(generateSettings(subjob));
            ExchangisLaunchTask task = new ExchangisLaunchTask();
            task.setCode(gson.toJson(code));
            task.setEngineType(job.getEngineType());
            task.setCreator(job.getCreateUser());
            task.setExecuteUser(job.getProxyUser());
            task.setRunType((String) subjob.getTransforms().get("type"));

            launchTasks[i] = task;
        }

        return launchTasks;
    }

    private List generateContents(ExchangisSubJob subjob, Long jobId) throws ExchangisDataSourceException {
        String source = getDataSourceType(subjob.getDataSources().get("source_id").toString());
        String sink = getDataSourceType(subjob.getDataSources().get("sink_id").toString());

        List<String> classList = Utils.getClazzName(HANDLER_PACKAGE_NAME, true);
        List<String> newClassList = new ArrayList<>(classList.size());
        for (int i = 0; i < classList.size(); i++) {
            newClassList.add(classList.get(i).replace(HANDLER_PACKAGE_NAME + ".", "").replace("JobHandler", ""));
        }

        JobHandler jobhandler;
        Reader reader;
        Writer writer;
        List contents = new ArrayList();

        try {
            if (newClassList.stream().anyMatch(c -> c.equalsIgnoreCase(source))) {
                String name = newClassList.stream().filter(f -> f.equalsIgnoreCase(source)).findAny().get();
                jobhandler = (JobHandler) Class.forName(HANDLER_PACKAGE_NAME + "." + name + "JobHandler").newInstance();
                reader = (Reader) Class.forName(READER_PACKAGE_NAME + "." + name + "Reader").newInstance();
            } else {
                jobhandler = new DataxJobHandler();
                reader = new Reader();
            }
            jobhandler.handleReader(subjob, jobId, reader);

            if (newClassList.stream().anyMatch(c -> c.equalsIgnoreCase(sink))) {
                String name = newClassList.stream().filter(f -> f.equalsIgnoreCase(sink)).findAny().get();
                jobhandler = (JobHandler) Class.forName(HANDLER_PACKAGE_NAME + "." + name + "JobHandler").newInstance();
                writer = (Writer) Class.forName(WRITER_PACKAGE_NAME + "." + name + "Writer").newInstance();

            } else {
                jobhandler = new DataxJobHandler();
                writer = new Writer();
            }
            jobhandler.handleWriter(subjob, jobId, writer);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ExchangisDataSourceException(23001, e.getLocalizedMessage());
        }

        contents.add(reader);
        contents.add(writer);

        return contents;
    }

    private Map generateSettings(ExchangisSubJob subjob) {
        List settings = subjob.getSettings();
        Map tasksettings = new HashMap();
        Map speedsettings = new HashMap();

        for (Object o : settings) {
            Setting setting = gson.fromJson(o.toString(), Setting.class);
            speedsettings.put(setting.getConfig_key(), setting.getConfig_value());
        }
        tasksettings.put("speed", speedsettings);

        return tasksettings;
    }

    private String getDataSourceType(String id) {
        return id.substring(0, id.indexOf("."));

    }

}
