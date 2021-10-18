package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.webank.wedatasphere.exchangis.job.datax.reader.DataxReader;
import com.webank.wedatasphere.exchangis.job.datax.writer.DataxWriter;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

public class HiveJobHandler extends DataxJobHandler {

    public void handleReader(ExchangisSubJob subjob, Long jobId, DataxReader reader) {
        reader.setName("hivereader");
        super.handleReader(subjob, jobId, reader);
    }

    public void handleWriter(ExchangisSubJob subjob, Long jobId, DataxWriter writer) {
        writer.setName("hivewriter");
        super.handleWriter(subjob, jobId, writer);
    }
}
