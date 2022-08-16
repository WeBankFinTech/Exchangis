package org.apache.linkis.engineconnplugin.datax.config

import com.alibaba.datax.core.util.container.CoreConstant
import org.apache.linkis.common.conf.CommonVars

/**
 * Core configuration in datax
 */
object DataxCoreConfiguration {

  /**
   * Format for 'datetime' column
   */
  val COMMON_COLUMN_DATETIME_FORMAT: CommonVars[String] = CommonVars("common.column.datetimeFormat", "yyyy-MM-dd HH:mm:ss");

  /**
   * Format for 'time' column
   */
  val COMMON_COLUMN_TIME_FORMAT: CommonVars[String] = CommonVars("common.column.timeFormat", "HH:mm:ss")

  /**
   * Format for 'date' column
   */
  val COMMON_COLUMN_DATE_FORMAT: CommonVars[String] = CommonVars("common.column.dateFormat", "yyyy-MM-dd")

  /**
   * Extra format for 'date','datetime' and 'time'
   */
  val COMMON_COLUMN_EXTRA_FORMATS: CommonVars[String] = CommonVars("common.column.extraFormats", "yyyy-MM-dd")

  /**
   * TimeZone
   */
  val COMMON_COLUMN_TIMEZONE: CommonVars[String] = CommonVars("common.column.timeZone", "GMT+8")

  /**
   * Encoding
   */
  val COMMON_COLUMN_ENCODING: CommonVars[String] = CommonVars("common.column.encoding", "utf-8")

  /**
   * Container model
   */
  val CORE_CONTAINER_MODEL: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_MODEL, "job")

  /**
   * Transport type
   */
  val CORE_TRANSPORT_TYPE: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_TYPE, "record")

  /**
   * Channel speed in byte
   */
  val CORE_TRANSPORT_CHANNEL_SPEED_BYTE: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_CHANNEL_SPEED_BYTE, 5242880)

  /**
   * Channel speed in record
   */
  val CORE_TRANSPORT_CHANNEL_SPEED_RECORD: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_CHANNEL_SPEED_RECORD, 10000)

  /**
   * Flow control interval
   */
  val CORE_TRANSPORT_CHANNEL_FLOW_CONTROL_INTERNAL: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_CHANNEL_FLOWCONTROLINTERVAL, 20)

  /**
   * Channel capacity in record(s)
   */
  val CORE_TRANSPORT_CHANNEL_CAPACITY: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_CHANNEL_CAPACITY, 512)

  /**
   * Channel capacity in byte(s)
   */
  val CORE_TRANSPORT_CHANNEL_BYTE_CAPACITY: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_CHANNEL_CAPACITY_BYTE, 67108864)

  /**
   * Record channel class
   */
  val CORE_TRANSPORT_RECORD_CHANNEL_CLASS: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_RECORD_CHANNEL_CLASS, "com.alibaba.datax.core.transport.channel.memory.MemoryRecordChannel")

  /**
   * Record exchanger class
   */
  val CORE_TRANSPORT_RECORD_EXCHANGER_CLASS: CommonVars[String] = CommonVars("core.transport.record.exchanger.class", "com.alibaba.datax.core.plugin.BufferedRecordExchanger")

  /**
   * Buffer size of record exchanger
   */
  val CORE_TRANSPORT_RECORD_EXCHANGER_BUFFER_SIZE: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_RECORD_EXCHANGER_BUFFERSIZE, 32)

  /**
   * Stream channel class
   */
  val CORE_TRANSPORT_STREAM_CHANNEL_CLASS: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_TRANSPORT_STREAM_CHANNEL_CLASS, "com.webank.wedatasphere.exchangis.datax.core.transport.channel.memory.MemoryStreamChannel")

  /**
   * Block size of stream channel
   */
  val CORE_TRANSPORT_STREAM_CHANNEL_BLOCK_SIZE: CommonVars[Int] = CommonVars("core.transport.stream.channel.bufferSize", 8192)

  /**
   * Job report interval
   */
  val CORE_CONTAINER_JOB_REPORT_INTERVAL: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_JOB_REPORTINTERVAL, 5000)

  /**
   * Job sleep interval
   */
  val CORE_CONTAINER_JOB_SLEEP_INTERNAL: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_JOB_SLEEPINTERVAL, 5000)

  /**
   * Task group report interval
   */
  val CORE_CONTAINER_TASK_GROUP_REPORT_INTERVAL: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_REPORTINTERVAL, 5000)

  /**
   * Task group sleep interval
   */
  val CORE_CONTAINER_TASK_GROUP_SLEEP_INTERNAL: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_SLEEPINTERVAL, 100)

  /**
   * Channel number for task group
   */
  val CORE_CONTAINER_TASK_GROUP_CHANNEL: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_CHANNEL, 5)

  /**
   * Trace switch
   */
  val CORE_CONTAINER_TRACE_ENABLE: CommonVars[Boolean] = CommonVars(CoreConstant.DATAX_CORE_CONTAINER_TRACE_ENABLE, false)

  /**
   * Plugin collector task class
   */
  val CORE_STATISTICS_COLLECTOR_PLUGIN_TASK_CLASS: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_STATISTICS_COLLECTOR_PLUGIN_TASKCLASS, "com.alibaba.datax.core.statistics.plugin.task.StdoutPluginCollector")

  /**
   * Max dirty record number
   */
  val CORE_STATISTICS_COLLECTOR_PLUGIN_MAX_DIRTY_NUMBER: CommonVars[Int] = CommonVars(CoreConstant.DATAX_CORE_STATISTICS_COLLECTOR_PLUGIN_MAXDIRTYNUM, 10)

  /**
   * Reporter class (EC use DataxEngineConnCommunicateReporter)
   */
  val CORE_STATISTICS_REPORTER_PLUGIN_CLASS: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_STATISTICS_REPORTER_PLUGIN_CLASS, "org.apache.linkis.engineconnplugin.datax.report.DataxEngineConnCommunicateReporter")
  /**
   * Processor loader plugin class
   */
  val CORE_PROCESSOR_LOADER_PLUGIN_CLASS: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_PROCESSOR_LOADER_PLUGIN_CLASS, "com.webank.wedatasphere.exchangis.datax.core.processor.loader.plugin.DefaultPluginProcessorLoader")

  /**
   * Package name of processor loader plugin
   */
  val CORE_PROCESSOR_LOADER_PLUGIN_PACKAGE: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_PROCESSOR_LOADER_PLUGIN_PACKAGE, "com.webank.wedatasphere.exchangis.datax.core.processor.impl")

  /**
   * Source path for processor loader plugin
   */
  val CORE_PROCESSOR_LOADER_PLUGIN_SOURCE_PATH: CommonVars[String] = CommonVars(CoreConstant.DATAX_CORE_PROCESSOR_LODAER_PLUGIN_SOURCEPATH, "proc/src")

}
