package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.webank.wedatasphere.exchangis.job.datax.reader.MysqlReader;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.MysqlWriter;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

import java.util.HashMap;
import java.util.Map;

public class MysqlJobHandler extends DataxJobHandler {
    @Override
    public Reader handlerReader(ExchangisSubJob job) {
        MysqlReader reader = new MysqlReader();
        Map params = new HashMap<String, String>();
        reader.setName("mysqlreader");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        reader.setParameter(params);
        return reader;
    }

    @Override
    public Writer handlerWriter(ExchangisSubJob job) {
        MysqlWriter writer = new MysqlWriter();
        Map params = new HashMap<String, String>();
        writer.setName("mysqlwriter");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        writer.setParameter(params);
        return writer;
    }
}
