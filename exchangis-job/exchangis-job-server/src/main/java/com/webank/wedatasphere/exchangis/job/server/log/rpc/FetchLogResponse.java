package com.webank.wedatasphere.exchangis.job.server.log.rpc;

import com.webank.wedatasphere.exchangis.job.log.LogResult;
import org.apache.linkis.protocol.message.RequestProtocol;

import java.util.List;

/**
 * Extend log result
 */
public class FetchLogResponse extends LogResult implements RequestProtocol {

    public FetchLogResponse(LogResult logResult){
        super(logResult.getEndLine(), logResult.isEnd(), logResult.getLogs());
    }

    public FetchLogResponse(int endLine, boolean isEnd, List<String> logs) {
        super(endLine, isEnd, logs);
    }
}
