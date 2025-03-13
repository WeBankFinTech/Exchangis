package com.webank.wedatasphere.exchangis.job.launcher.linkis.client

import com.webank.wedatasphere.exchangis.common.linkis.client.ExchangisHttpClient
import com.webank.wedatasphere.exchangis.common.linkis.client.config.ExchangisClientConfig
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.computation.client.once.LinkisManagerClient
import org.apache.linkis.computation.client.once.action.{AskEngineConnAction, CreateEngineConnAction, EngineConnOperateAction, GetEngineConnAction, KillEngineConnAction, LinkisManagerAction, ListEngineConnAction}
import org.apache.linkis.computation.client.once.result.{AskEngineConnResult, CreateEngineConnResult, EngineConnOperateResult, GetEngineConnResult, KillEngineConnResult, LinkisManagerResult, ListEngineConnResult}
import org.apache.linkis.httpclient.request.Action

/**
 * Exchangis launch client
 */
class ExchangisLaunchClient(clientConfig: ExchangisClientConfig) extends LinkisManagerClient{
  private val dwsHttpClient = new ExchangisHttpClient(clientConfig, "Linkis-Job-Execution-Thread")

  protected def execute[T <: LinkisManagerResult](linkisManagerAction: LinkisManagerAction): T =
    linkisManagerAction match {
      case action: Action => dwsHttpClient.execute(action).asInstanceOf[T]
    }

  override def createEngineConn(
                                 createEngineConnAction: CreateEngineConnAction
                               ): CreateEngineConnResult = execute(createEngineConnAction)

  override def getEngineConn(getEngineConnAction: GetEngineConnAction): GetEngineConnResult =
    execute(getEngineConnAction)

  override def killEngineConn(killEngineConnAction: KillEngineConnAction): KillEngineConnResult =
    execute(killEngineConnAction)

  override def executeEngineConnOperation(
                                           engineConnOperateAction: EngineConnOperateAction
                                         ): EngineConnOperateResult = {
    Utils.tryCatch {
      val rs = execute[EngineConnOperateResult](engineConnOperateAction)
      rs
    } { case e: Exception =>
      val rs = new EngineConnOperateResult
      rs.setIsError(true)
      rs.setErrorMsg(e.getMessage)
      rs
    }
  }

  override def close(): Unit = dwsHttpClient.close()

  override def askEngineConn(askEngineConnAction: AskEngineConnAction): AskEngineConnResult =
    execute(askEngineConnAction)

  override def listEngineConn(listEngineConnAction: ListEngineConnAction): ListEngineConnResult = {
    execute(listEngineConnAction)
  }
}
