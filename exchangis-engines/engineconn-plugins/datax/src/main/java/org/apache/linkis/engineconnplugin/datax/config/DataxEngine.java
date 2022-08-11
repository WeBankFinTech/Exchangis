package org.apache.linkis.engineconnplugin.datax.config;

import com.alibaba.datax.common.exception.CommonErrorCode;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.statistics.PerfTrace;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.core.AbstractContainer;
import com.alibaba.datax.core.job.JobContainer;
import com.alibaba.datax.core.taskgroup.TaskGroupContainer;
import com.alibaba.datax.core.util.container.CoreConstant;
import org.apache.linkis.protocol.engine.JobProgressInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DataxEngine {
    private static final Logger LOG = LoggerFactory.getLogger(DataxEngine.class);

    private Configuration configuration;

    public DataxEngine(Map<String, Object> params) {
        configuration = Configuration.from(params);
    }

    public void run() {
        AbstractContainer container;
        long instanceId = configuration.getLong(CoreConstant.DATAX_CORE_CONTAINER_JOB_ID, 0);
        int taskGroupId = -1;
        int channelNumber = 0;

        boolean isJob = !("taskGroup".equalsIgnoreCase(configuration.getString(CoreConstant.DATAX_CORE_CONTAINER_MODEL)));
        if (isJob) {
            container = new JobContainer(configuration);
        } else {
            container = new TaskGroupContainer(configuration);
            taskGroupId = configuration.getInt(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_ID);
            channelNumber = configuration.getInt(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_CHANNEL);
        }

        //缺省打开perfTrace
        boolean traceEnable = configuration.getBool(CoreConstant.DATAX_CORE_CONTAINER_TRACE_ENABLE, true);
        boolean perfReportEnable = configuration.getBool(CoreConstant.DATAX_CORE_REPORT_DATAX_PERFLOG, true);

        //standlone模式的datax shell任务不进行汇报
        if (instanceId == -1) {
            perfReportEnable = false;
        }

        int priority = 0;
        try {
            priority = Integer.parseInt(System.getenv("SKYNET_PRIORITY"));
        } catch (NumberFormatException e) {
            LOG.warn("prioriy set to 0, because NumberFormatException, the value is: " + System.getProperty("PROIORY"));
        }

        //初始化PerfTrace
        Configuration jobInfoConfig = configuration.getConfiguration(CoreConstant.DATAX_JOB_JOBINFO);
        PerfTrace perfTrace = PerfTrace.getInstance(isJob, instanceId, taskGroupId, priority, traceEnable);
        perfTrace.setJobInfo(jobInfoConfig, perfReportEnable, channelNumber);

        //开启container
        container.start();
    }

    public String getApplicationId() {
        return String.valueOf(this.configuration.get("jobId"));
    }

    public Float progress() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : progress");
    }

    public JobProgressInfo getProgressInfo() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : getProgressInfo");
    }

    public Map<String, Object> getMetrics() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : getMetrics");
    }

    public Map<String, Object> getDiagnosis() {
        throw new DataXException(CommonErrorCode.UNSUPPORTED_METHOD, "unsupported method : getDiagnosis");
    }

}
