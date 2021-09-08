package com.webank.wedatasphere.exchangis.job.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.datax.domain.LaunchCode;
import com.webank.wedatasphere.exchangis.job.datax.handler.HiveJobHandler;
import com.webank.wedatasphere.exchangis.job.datax.handler.MysqlJobHandler;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.domain.Setting;
import com.webank.wedatasphere.exchangis.job.enums.DataSourceTypeEnum;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataXJobBuilder implements ExchangisJobBuilder {

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
        DataSourceTypeEnum source = getDataSourceType(subjob.getDataSources().get("source_id").toString());
        DataSourceTypeEnum sink = getDataSourceType(subjob.getDataSources().get("sink_id").toString());
        JobHandler jobhandler;

        Reader reader = null;
        Writer writer = null;
        List contents = new ArrayList();

        switch (source) {
            case MYSQL:
                jobhandler = new MysqlJobHandler();
                reader = jobhandler.handlerReader(subjob, jobId);
                break;
            case HIVE:
                jobhandler = new HiveJobHandler();
                reader = jobhandler.handlerReader(subjob, jobId);
                break;
            default:
                break;
        }

        switch (sink) {
            case MYSQL:
                jobhandler = new MysqlJobHandler();
                writer = jobhandler.handlerWriter(subjob, jobId);
                break;
            case HIVE:
                jobhandler = new HiveJobHandler();
                writer = jobhandler.handlerWriter(subjob, jobId);
                break;
            default:
                break;
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

    private DataSourceTypeEnum getDataSourceType(String id) {
        if (id.toLowerCase().startsWith("hive")) {
            return DataSourceTypeEnum.HIVE;
        } else if (id.toLowerCase().startsWith("mysql")) {
            return DataSourceTypeEnum.MYSQL;
        } else return DataSourceTypeEnum.TEXT;
    }
}
