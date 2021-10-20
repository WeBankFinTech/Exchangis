package com.webank.wedatasphere.exchangis.job.sqoop.reader;

import com.webank.wedatasphere.exchangis.job.domain.Reader;

public class SqoopReader extends Reader {

    private String readerString;

    public String getReaderString() {
        return readerString;
    }

    public void setReaderString(String readerString) {
        this.readerString = readerString;
    }
}
