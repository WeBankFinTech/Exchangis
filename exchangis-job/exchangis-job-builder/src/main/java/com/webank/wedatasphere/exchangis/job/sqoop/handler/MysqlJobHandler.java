package com.webank.wedatasphere.exchangis.job.sqoop.handler;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.domain.Reader;
import com.webank.wedatasphere.exchangis.job.domain.Writer;
import com.webank.wedatasphere.exchangis.job.sqoop.reader.SqoopReader;

public class MysqlJobHandler extends SqoopJobHandler {
    @Override
    public void handleReader(ExchangisSubJob job, Long jobId, Reader reader) {
        ((SqoopReader) reader).setReaderString("--connect jdbc:mysql://172.24.2.61:3306/test --username root --password 123456 --table sqooptest");
        super.handleReader(job, jobId, reader);
    }

    @Override
    public void handleWriter(ExchangisSubJob job, Long jobId, Writer writer) {

    }

}
