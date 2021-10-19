package com.webank.wedatasphere.exchangis.job.handler;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.domain.Reader;
import com.webank.wedatasphere.exchangis.job.domain.Writer;

public interface JobHandler {

    void handleReader(ExchangisSubJob job, Long jobId, Reader reader);

    void handleWriter(ExchangisSubJob job, Long jobId, Writer writer);
}
