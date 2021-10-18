package com.webank.wedatasphere.exchangis.job.sqoop.handler;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.domain.Reader;
import com.webank.wedatasphere.exchangis.job.domain.Writer;
import com.webank.wedatasphere.exchangis.job.sqoop.writer.SqoopWriter;

public class HiveJobHandler extends SqoopJobHandler {

    @Override
    public void handleReader(ExchangisSubJob job, Long jobId, Reader reader) {

    }

    @Override
    public void handleWriter(ExchangisSubJob job, Long jobId, Writer writer) {
        ((SqoopWriter) writer).setWriterString("--hive-import --hive-overwrite --hive-database test hive-table sqooptest");
        super.handleWriter(job, jobId, writer);
    }
}
