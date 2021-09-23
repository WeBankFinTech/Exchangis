package com.webank.wedatasphere.exchangis.job.handler;

import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

public interface JobHandler {

    void handleReader(ExchangisSubJob job, Long jobId, Reader reader);

    void handleWriter(ExchangisSubJob job, Long jobId, Writer writer);
}
