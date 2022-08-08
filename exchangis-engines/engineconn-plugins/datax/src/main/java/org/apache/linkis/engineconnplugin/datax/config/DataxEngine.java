package org.apache.linkis.engineconnplugin.datax.config;

import com.alibaba.datax.common.exception.CommonErrorCode;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.util.Configuration;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.linkis.protocol.engine.JobProgressInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DataxEngine {
    private static final Logger LOG = LoggerFactory.getLogger(DataxEngine.class);

    private static CommandLine cl;
    private static Configuration configuration;
    private static BasicParser parser = new BasicParser();

    public DataxEngine(String[] args) {
        Options options = new Options();
        options.addOption("job", true, "Job config.");
        options.addOption("jobId", true, "Job unique id.");
        options.addOption("mode", true, "Job runtime mode.");

        try {
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
            throw new DataXException(CommonErrorCode.CONFIG_ERROR, e.getMessage());
        }
    }

    public static String getApplicationId() {
        return cl.getOptionValue("jobId");
    }

    public static Float progress() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : progress");
    }

    public static JobProgressInfo getProgressInfo() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : getProgressInfo");
    }

    public static Map<String, Object> getMetrics() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : getMetrics");
    }

    public static Map<String, Object> getDiagnosis() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : getDiagnosis");
    }

}
