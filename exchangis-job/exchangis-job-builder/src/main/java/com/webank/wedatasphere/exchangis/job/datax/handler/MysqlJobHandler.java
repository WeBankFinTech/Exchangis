package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

public class MysqlJobHandler extends DataxJobHandler {

    public void handleReader(ExchangisSubJob subjob, Long jobId, Reader reader) {
        reader.setName("mysqlreader");
        super.handleReader(subjob, jobId, reader);
    }

    public void handleWriter(ExchangisSubJob subjob, Long jobId, Writer writer) {
        writer.setName("mysqlwriter");
        super.handleWriter(subjob, jobId, writer);
    }
}
