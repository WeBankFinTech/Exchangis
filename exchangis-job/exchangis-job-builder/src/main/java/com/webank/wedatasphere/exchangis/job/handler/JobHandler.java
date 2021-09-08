package com.webank.wedatasphere.exchangis.job.handler;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

public interface JobHandler {

    Reader handleReader(ExchangisSubJob job, Long jobId) throws ExchangisDataSourceException;

    Writer handleWriter(ExchangisSubJob job, Long jobId);
}
