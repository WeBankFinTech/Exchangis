/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.linkis.engineconnplugin.datax.config

import org.apache.linkis.common.conf.CommonVars

class DataxResourceConfiguration {

  private val ENTRY_ENVIRONMENT: Any = CommonVars[Map]("entry.environment", {})

  private val COMMON_COLUMN_DATATIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

  private val COMMON_COLUMN_TIME_FORMAT = "HH:mm:ss"

  private val COMMON_COLUMN_DATE_FORMAT = "yyyy-MM-dd"

  private val COMMON_COLUMN_EXTRA_FORMAT = "yyyyMMdd"

  private val COMMON_COLUMN_TIMEZONE = "GMT+8"

  private val COMMON_COLUMN_ENCODING = "utf-8"

  private val CORE_TRANSPORT_TYPE: CommonVars[String] = CommonVars[String]("core.transport.type", "record")

  private val CORE_TRANSPORT_CHANNEL_SPEED_BYTE: CommonVars[Integer] = CommonVars[Integer]("core.transport.channel.speed.byte", 5242880)

  private val CORE_TRANSPORT_CHANNEL_SPEED_RECORD: CommonVars[Integer] = CommonVars[Integer]("core.transport.channel.speed.record", 10000)

  private val CORE_TRANSPORT_CHANNEL_FLOW_CONTROL_INTERNAL: CommonVars[Integer] = CommonVars[Integer]("core.transport.channel.flowControlInterprivate val", 20)

  private val CORE_TRANSPORT_CHANNEL_CAPACITY: CommonVars[Integer] = CommonVars[Integer]("core.transport.channel.capacity", 512)

  private val CORE_TRANSPORT_CHANNEL_BYTE_CAPACITY: CommonVars[Integer] = CommonVars[Integer]("core.transport.channel.byteCapacity", 67108864)

  private val CORE_TRANSPORT_RECORD_CHANNEL_CLASS: CommonVars[String] = CommonVars[String]("core.transport.record.channel.class", "com.alibaba.datax.core.transport.channel.memory.MemoryRecordChannel")

  private val CORE_TRANSPORT_RECORD_EXCHANGER_CLASS: CommonVars[String] = CommonVars[String]("core.transport.record.exchanger.class", "com.alibaba.datax.core.plugin.BufferedRecordExchanger")

  private val CORE_TRANSPORT_RECORD_EXCHANGER_BUFFERSIZE: CommonVars[Integer] = CommonVars[Integer]("core.transport.record.exchanger.bufferSize", 32)

  private val CORE_TRANSPORT_STREAM_CHANNEL_CLASS: CommonVars[String] = CommonVars[String]("core.transport.stream.channel.class", "com.webank.wedatasphere.exchangis.datax.core.transport.channel.memory.MemoryStreamChannel")

  private val CORE_TRANSPORT_STREAM_CHANNEL_BLOCKSIZE: CommonVars[Integer] = CommonVars[Integer]("core.transport.stream.channel.bufferSize", 8192)

  private val CORE_CONTAINER_JOB_REPORTINTERVAL: CommonVars[Integer] = CommonVars[Integer]("core.container.job.reportInterprivate val", 5000)

  private val CORE_CONTAINER_JOB_SLEEPINTERNAL: CommonVars[Integer] = CommonVars[Integer]("core.container.job.sleepInterprivate val", 5000)

  private val CORE_CONTAINER_TASKGROUP_REPORTINTERVAL: CommonVars[Integer] = CommonVars[Integer]("core.container.taskGroup.reportInterprivate val", 5000)

  private val CORE_CONTAINER_TASKGROUP_SLEEPINTERNAL: CommonVars[Integer] = CommonVars[Integer]("core.container.taskGroup.sleepInterprivate val", 100)

  private val CORE_CONTAINER_TASKGROUP_CHANNEL: CommonVars[Integer] = CommonVars[Integer]("core.container.taskGroup.channel", 5)

  private val CORE_CONTAINER_TRACE_ENABLE: CommonVars[Boolean] = CommonVars[Boolean]("core.container.trace.enable", false)

  private val CORE_STATISTICS_COLLECTOR_PLUGIN_TASKCLASS: CommonVars[String] = CommonVars[String]("core.statistics.collector.plugin.taskClass", "com.alibaba.datax.core.statistics.plugin.task.StdoutPluginCollector")

  private val CORE_STATISTICS_COLLECTOR_PLUGIN_MAXDIRTYNUMBER: CommonVars[Integer] = CommonVars[Integer]("core.statistics.collector.plugin.maxDirtyNumber", 10)

  private val CORE_PROCESSOR_LOADER_PLUGIN_CLASS: CommonVars[String] = CommonVars[String]("core.processor.loader.plugin.class", "com.webank.wedatasphere.exchangis.datax.core.processor.loader.plugin.DefaultPluginProcessorLoader")

  private val CORE_PROCESSOR_LOADER_PLUGIN_PACKAGE: CommonVars[String] = CommonVars[String]("core.processor.loader.plugin.package", "com.webank.wedatasphere.exchangis.datax.core.processor.impl")

  private val CORE_PROCESSOR_LOADER_PLUGIN_SOURCEPATH: CommonVars[String] = CommonVars[String]("core.processor.loader.plugin.sourcePath", "proc/src")

}
