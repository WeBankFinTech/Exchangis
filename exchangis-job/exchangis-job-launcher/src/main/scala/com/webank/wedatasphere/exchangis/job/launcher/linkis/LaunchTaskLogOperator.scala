package com.webank.wedatasphere.exchangis.job.launcher.linkis

import org.apache.linkis.computation.client.once.action.EngineConnOperateAction
import org.apache.linkis.computation.client.operator.impl.EngineConnLogOperator

/**
 * Enable to reverse read log file
 */
class LaunchTaskLogOperator extends EngineConnLogOperator{

  private var enableTail: Boolean = false

  def setEnableTail(enableTail: Boolean): Unit = {
    this.enableTail = enableTail
  }

  def isEnableTail: Boolean = {
    this.enableTail
  }

  protected override def addParameters(builder: EngineConnOperateAction.Builder): Unit = {
    super.addParameters(builder)
    builder.addParameter("enableTail", enableTail)
  }
}
object LaunchTaskLogOperator {
  val OPERATOR_NAME = "launchTaskLog"
}