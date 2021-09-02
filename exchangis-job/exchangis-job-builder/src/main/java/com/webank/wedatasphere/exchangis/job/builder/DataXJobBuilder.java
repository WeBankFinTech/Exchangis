package com.webank.wedatasphere.exchangis.job.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSourceJob;
import com.webank.wedatasphere.exchangis.job.domain.LaunchTask;
import com.webank.wedatasphere.exchangis.job.domain.Setting;
import com.webank.wedatasphere.exchangis.job.enums.DataSourceEnum;
import com.webank.wedatasphere.exchangis.job.handler.HiveJobHandler;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;
import com.webank.wedatasphere.exchangis.job.handler.MysqlJobHandler;
import com.webank.wedatasphere.exchangis.job.reader.Reader;
import com.webank.wedatasphere.exchangis.job.writer.Writer;

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
        ExchangisSourceJob[] exchangisSourceJobs = gson.fromJson(content, ExchangisSourceJob[].class);
        LaunchTask[] tasks = new LaunchTask[exchangisSourceJobs.length];

        for (int i = 0; i < exchangisSourceJobs.length; i++) {
            ExchangisSourceJob subjob = exchangisSourceJobs[i];
            DataSourceEnum source = getDataSourceType(subjob.getDataSources().get("source_id").toString());
            DataSourceEnum sink = getDataSourceType(subjob.getDataSources().get("sink_id").toString());
            JobHandler jobhandler;
            Reader reader = null;
            Writer writer = null;
            LaunchTask task = new LaunchTask();
            List contents = new ArrayList();

            switch (source) {
                case MYSQL:
                    jobhandler = new MysqlJobHandler();
                    reader = jobhandler.handlerreader(subjob);
                    break;
                case HIVE:
                    jobhandler = new HiveJobHandler();
                    reader = jobhandler.handlerreader(subjob);
                    break;
                default:
                    break;
            }

            switch (sink) {
                case MYSQL:
                    jobhandler = new MysqlJobHandler();
                    writer = jobhandler.handlerwriter(subjob);
                    break;
                case HIVE:
                    jobhandler = new HiveJobHandler();
                    writer = jobhandler.handlerwriter(subjob);
                    break;
                default:
                    break;
            }

            contents.add(reader);
            contents.add(writer);
            task.setContent(contents);

            List settings = subjob.getSettings();
            Map tasksettings = new HashMap();
            Map speedsettings = new HashMap();

            for (int j = 0; j < settings.size(); j++) {
                Setting setting = gson.fromJson(settings.get(j).toString(), Setting.class);
                speedsettings.put(setting.getConfig_key(), setting.getConfig_value());
            }
            tasksettings.put("speed", speedsettings);
            task.setSettings(tasksettings);
            tasks[i] = task;
        }

        return tasks;
    }

    private DataSourceEnum getDataSourceType(String id) {
        if (id.contains("HIVE")) {
            return DataSourceEnum.HIVE;
        } else if (id.contains("MYSQL")) {
            return DataSourceEnum.MYSQL;
        } else return DataSourceEnum.TEXT;
    }
}
