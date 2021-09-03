package com.webank.wedatasphere.exchangis.job.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.exchangis.job.datax.handler.HiveJobHandler;
import com.webank.wedatasphere.exchangis.job.datax.handler.MysqlJobHandler;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.domain.LaunchTask;
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
    public LaunchTask[] buildJob(ExchangisJob job) {
        String content = job.getContent();
        ExchangisSubJob[] exchangisSubJobs = gson.fromJson(content, ExchangisSubJob[].class);
        LaunchTask[] tasks = new LaunchTask[exchangisSubJobs.length];

        for (int i = 0; i < exchangisSubJobs.length; i++) {
            ExchangisSubJob subjob = exchangisSubJobs[i];
            LaunchTask task = new LaunchTask();

            task.setContent(generateContents(subjob));
            task.setSettings(generateSettings(subjob));

            tasks[i] = task;
        }

        return tasks;
    }

    private List generateContents(ExchangisSubJob subjob) {
        DataSourceTypeEnum source = getDataSourceType(subjob.getDataSources().get("source_id").toString());
        DataSourceTypeEnum sink = getDataSourceType(subjob.getDataSources().get("sink_id").toString());
        JobHandler jobhandler;

        Reader reader = null;
        Writer writer = null;
        List contents = new ArrayList();

        switch (source) {
            case MYSQL:
                jobhandler = new MysqlJobHandler();
                reader = jobhandler.handlerReader(subjob);
                break;
            case HIVE:
                jobhandler = new HiveJobHandler();
                reader = jobhandler.handlerReader(subjob);
                break;
            default:
                break;
        }

        switch (sink) {
            case MYSQL:
                jobhandler = new MysqlJobHandler();
                writer = jobhandler.handlerWriter(subjob);
                break;
            case HIVE:
                jobhandler = new HiveJobHandler();
                writer = jobhandler.handlerWriter(subjob);
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

        for (int j = 0; j < settings.size(); j++) {
            Setting setting = gson.fromJson(settings.get(j).toString(), Setting.class);
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
