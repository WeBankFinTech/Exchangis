package com.alibaba.datax.core.statistics.container.communicator.taskgroup;

import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.core.statistics.communication.Communication;
import com.alibaba.datax.core.statistics.container.report.AbstractReporter;
import com.alibaba.datax.core.statistics.container.report.ProcessInnerReporter;
import com.alibaba.datax.core.util.ClassUtil;
import com.alibaba.datax.core.util.container.CoreConstant;

public class StandaloneTGContainerCommunicator extends AbstractTGContainerCommunicator {

    public StandaloneTGContainerCommunicator(Configuration configuration) {
        super(configuration);
        // Set the reporter defined in configuration
        super.setReporter(ClassUtil.instantiate(configuration.getString(CoreConstant.DATAX_CORE_STATISTICS_REPORTER_PLUGIN_CLASS),
                AbstractReporter.class, configuration));
    }

    @Override
    public void report(Communication communication) {
        super.getReporter().reportTGCommunication(super.taskGroupId, communication);
    }

}
