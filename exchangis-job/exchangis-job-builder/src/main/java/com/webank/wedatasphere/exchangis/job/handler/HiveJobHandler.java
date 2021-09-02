package com.webank.wedatasphere.exchangis.job.handler;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisSourceJob;
import com.webank.wedatasphere.exchangis.job.reader.HiveReader;
import com.webank.wedatasphere.exchangis.job.reader.Reader;
import com.webank.wedatasphere.exchangis.job.writer.HiveWriter;
import com.webank.wedatasphere.exchangis.job.writer.Writer;

import java.util.HashMap;
import java.util.Map;

public class HiveJobHandler extends DataxJobHandler {
    @Override
    public Reader handlerreader(ExchangisSourceJob job) {
        HiveReader reader = new HiveReader();
        Map params = new HashMap<String, String>();
        reader.setName("hivereader");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        reader.setParameter(params);
        return reader;
    }

    @Override
    public Writer handlerwriter(ExchangisSourceJob job) {
        HiveWriter writer = new HiveWriter();
        Map params = new HashMap<String, String>();
        writer.setName("hivewriter");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        writer.setParameter(params);
        return writer;
    }
}
