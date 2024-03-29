package com.alibaba.datax.core.statistics.container.communicator.job;

import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.core.statistics.communication.Communication;
import com.alibaba.datax.core.statistics.communication.CommunicationTool;
import com.alibaba.datax.core.statistics.container.collector.ProcessInnerCollector;
import com.alibaba.datax.core.statistics.container.communicator.AbstractContainerCommunicator;
import com.alibaba.datax.core.statistics.container.report.AbstractReporter;
import com.alibaba.datax.core.statistics.container.report.ProcessInnerReporter;
import com.alibaba.datax.core.util.ClassUtil;
import com.alibaba.datax.core.util.container.CoreConstant;
import com.alibaba.datax.dataxservice.face.domain.enums.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class StandAloneJobContainerCommunicator extends AbstractContainerCommunicator {
    private static final Logger LOG = LoggerFactory
            .getLogger(StandAloneJobContainerCommunicator.class);

    public StandAloneJobContainerCommunicator(Configuration configuration) {
        super(configuration);
        super.setCollector(new ProcessInnerCollector(configuration.getLong(
                CoreConstant.DATAX_CORE_CONTAINER_JOB_ID)));
        // Set the reporter defined in configuration
        super.setReporter(ClassUtil.instantiate(configuration.getString(CoreConstant.DATAX_CORE_STATISTICS_REPORTER_PLUGIN_CLASS),
                AbstractReporter.class, configuration));
    }

    @Override
    public void registerCommunication(List<Configuration> configurationList) {
        super.getCollector().registerTGCommunication(configurationList);
    }

    @Override
    public Communication collect() {
        return super.getCollector().collectFromTaskGroup();
    }

    @Override
    public State collectState() {
        return this.collect().getState();
    }

    /**
     * 和 DistributeJobContainerCollector 的 report 实现一样
     */
    @Override
    public void report(Communication communication) {
        super.getReporter().reportJobCommunication(super.getJobId(), communication);
        String info = CommunicationTool.Stringify.getSnapshot(communication);
        LOG.info(info);
        reportVmInfo();
    }

    @Override
    public Communication getCommunication(Integer taskGroupId) {
        return super.getCollector().getTGCommunication(taskGroupId);
    }

    @Override
    public Map<Integer, Communication> getCommunicationMap() {
        return super.getCollector().getTGCommunicationMap();
    }
}
