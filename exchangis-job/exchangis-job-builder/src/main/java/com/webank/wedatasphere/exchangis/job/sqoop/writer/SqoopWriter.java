package com.webank.wedatasphere.exchangis.job.sqoop.writer;

import com.webank.wedatasphere.exchangis.job.domain.Writer;

public class SqoopWriter extends Writer {

    private String writerString;

    public String getWriterString() {
        return writerString;
    }

    public void setWriterString(String writerString) {
        this.writerString = writerString;
    }
}
