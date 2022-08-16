package org.apache.linkis.engineconnplugin.datax.report

import com.alibaba.datax.core.statistics.communication.Communication

/**
 * Report receiver
 */
trait DataxReportReceiver extends DataxReportQuota {
    /**
     * Receive communication
     * @param communication communication
     */
    def receive(jobId: String, communication: Communication): Unit

}
