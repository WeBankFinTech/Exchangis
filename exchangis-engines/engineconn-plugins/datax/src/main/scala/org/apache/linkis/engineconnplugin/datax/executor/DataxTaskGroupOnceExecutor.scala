package org.apache.linkis.engineconnplugin.datax.executor
import com.alibaba.datax.common.util.Configuration
import com.alibaba.datax.core.AbstractContainer
import com.alibaba.datax.core.taskgroup.TaskGroupContainer
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext

class DataxTaskGroupOnceExecutor(override val id: Long,
                                 override protected val dataxEngineConnContext: DataxEngineConnContext) extends DataxContainerOnceExecutor {
  /**
   * Container name
   *
   * @return
   */
  override def getContainerName: String = "TaskGroup-Container"

  /**
   * Container entity
   *
   * @param config              container configuration
   * @param engineCreateContext engine create context
   */
  override def createContainer(config: Configuration, engineCreateContext: EngineCreationContext): AbstractContainer = {
    new TaskGroupContainer(config)
  }

}
