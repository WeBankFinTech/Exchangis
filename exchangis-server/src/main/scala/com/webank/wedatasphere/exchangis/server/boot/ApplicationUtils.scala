package com.webank.wedatasphere.exchangis.server.boot

import java.io.IOException
import java.lang.management.ManagementFactory
import java.nio.file.{Files, Paths}

import org.apache.linkis.common.utils.Utils
import org.slf4j.LoggerFactory

/**
 * Utils for application
 */
object ApplicationUtils{
  val PROC_PID_FILE = "pid.file"

  private lazy val LOG = LoggerFactory.getLogger(getClass)
  /**
   * Save the progress id into file
   * @param mainProgram main program
   * @param args arguments
   */
    def savePidAndRun( mainProgram :  => Unit, args: String*): Unit ={
      Utils.tryCatch{
        mountPID(System.getProperty(PROC_PID_FILE))
        mainProgram
      }{
        case e: Exception =>
          LOG.info("The process has been shutdown: [" + e.getMessage + "]", e)
          System.exit(1)
      }
    }

    def mountPID(pidPath: String): Unit = {
       Option(pidPath).foreach( path => {
          val name: String = ManagementFactory.getRuntimeMXBean.getName
          val pid: String = name.split("@")(0).trim
          Files.write(Paths.get(path), pid.getBytes)
          Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
            override def run(): Unit = {
              try{
                 Files.delete(Paths.get(path))
              }catch{
                case e: IOException => //Ignore
              }
            }
          }))
       })
    }
}
