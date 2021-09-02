package com.webank.wedatasphere.exchangis.job.handler;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisSourceJob;
import com.webank.wedatasphere.exchangis.job.reader.MysqlReader;
import com.webank.wedatasphere.exchangis.job.reader.Reader;
import com.webank.wedatasphere.exchangis.job.writer.MysqlWriter;
import com.webank.wedatasphere.exchangis.job.writer.Writer;

import java.util.HashMap;
import java.util.Map;

public class MysqlJobHandler extends DataxJobHandler {
    @Override
    public Reader handlerreader(ExchangisSourceJob job) {
        MysqlReader reader = new MysqlReader();
        Map params = new HashMap<String, String>();
        reader.setName("mysqlreader");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        reader.setParameter(params);
        return reader;
    }

    @Override
    public Writer handlerwriter(ExchangisSourceJob job) {
        MysqlWriter writer = new MysqlWriter();
        Map params = new HashMap<String, String>();
        writer.setName("mysqlwriter");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        writer.setParameter(params);
        return writer;
    }
}
