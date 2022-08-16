package org.apache.linkis.engineconnplugin.datax.report

import com.alibaba.datax.common.util.Configuration
import com.alibaba.datax.core.statistics.communication.{Communication, LocalTGCommunicationManager}
import com.alibaba.datax.core.statistics.container.report.AbstractReporter
import org.apache.linkis.engineconn.once.executor.creation.OnceExecutorManager
import org.apache.linkis.engineconnplugin.datax.executor.DataxContainerOnceExecutor

import java.lang

/**
 * Communication reporter for datax engine conn
 */
class DataxEngineConnCommunicateReporter(configuration: Configuration) extends AbstractReporter{


  /**
   * Report the job communication
   * @param jobId job id
   * @param communication communication
   */
  override def reportJobCommunication(jobId: lang.Long, communication: Communication): Unit = {
    OnceExecutorManager.getInstance.getReportExecutor match{
      case executor: DataxContainerOnceExecutor =>
        executor.getReportReceiver.receive(jobId.toString, communication)
      case _ =>
    }
  }

  /**
   * Report the task group communication
   * @param taskGroupId task group id
   * @param communication communication
   */
  override def reportTGCommunication(taskGroupId: Integer, communication: Communication): Unit = {
    LocalTGCommunicationManager.updateTaskGroupCommunication(taskGroupId, communication)
  }

  def getConfiguration: Configuration = this.configuration
}
