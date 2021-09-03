package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.webank.wedatasphere.exchangis.job.datax.reader.HiveReader;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.HiveWriter;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

import java.util.HashMap;
import java.util.Map;

public class HiveJobHandler extends DataxJobHandler {
    @Override
    public Reader handlerReader(ExchangisSubJob job) {
        HiveReader reader = new HiveReader();
        Map params = new HashMap<String, String>();
        reader.setName("hivereader");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        reader.setParameter(params);
        return reader;
    }

    @Override
    public Writer handlerWriter(ExchangisSubJob job) {
        HiveWriter writer = new HiveWriter();
        Map params = new HashMap<String, String>();
        writer.setName("hivewriter");
        params.put("username", "root");
        params.put("password", "s34uhWG0Lv*");
        writer.setParameter(params);
        return writer;
    }
}
