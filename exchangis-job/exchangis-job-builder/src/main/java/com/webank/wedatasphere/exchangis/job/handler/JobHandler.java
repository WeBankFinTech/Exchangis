package com.webank.wedatasphere.exchangis.job.handler;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisSourceJob;
import com.webank.wedatasphere.exchangis.job.reader.Reader;
import com.webank.wedatasphere.exchangis.job.writer.Writer;

public interface JobHandler {

    Reader handlerreader(ExchangisSourceJob job);

    Writer handlerwriter(ExchangisSourceJob job);
}
