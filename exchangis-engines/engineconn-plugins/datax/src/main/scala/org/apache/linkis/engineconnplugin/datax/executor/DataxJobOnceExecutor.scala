package org.apache.linkis.engineconnplugin.datax.executor

import com.alibaba.datax.common.util.Configuration
import com.alibaba.datax.core.AbstractContainer
import com.alibaba.datax.core.job.JobContainer
import com.alibaba.datax.core.util.container.CoreConstant
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext

import java.util
import scala.collection.JavaConverters.asScalaSetConverter

/**
 *
 * @param id id
 * @param dataxEngineConnContext datax engine conn context
 */
class DataxJobOnceExecutor(override val id: Long,
                           override protected val dataxEngineConnContext: DataxEngineConnContext) extends DataxContainerOnceExecutor {
  /**
   * Container name
   *
   * @return
   */
  override def getContainerName: String = "Job-Container"

  /**
   * Container entity
   *
   * @param config              container configuration
   * @param engineCreateContext engine create context
   */
  override def createContainer(config: Configuration, engineCreateContext: EngineCreationContext): AbstractContainer = {
    config.set(CoreConstant.DATAX_CORE_CONTAINER_JOB_MODE, "engineConn")
    new JobContainer(config)
  }
}