package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DataxJobHandler implements JobHandler {

    @Autowired
    ExchangisDataSourceService dataSourceService;

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

}
