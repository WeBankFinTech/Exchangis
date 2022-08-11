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

import org.apache.linkis.common.conf.{CommonVars, TimeType}

object DataxEnvConfiguration {

  val DATAX_LINUX_SUDO_USER = "hadoop"

  val DATAX_ACTIVE_HEART_BEAT: CommonVars[Integer] = CommonVars("datax.active.heart.beat", 5)

  val IDLE_ACTIVE_HEART_BERT: CommonVars[Integer] = CommonVars("idle.active.heart.beat", 60)

  val WAIT_ALLOC_TIME_IN_SEC: CommonVars[Integer] = CommonVars("wait.alloc.timeInSec", 5)

  val DATAX_STATUS_FETCH_INTERVAL: CommonVars[TimeType] = CommonVars("datax.fetch.status.interval", new TimeType("5s"))

  val EXECUTOR_JOB_ENGINE_DATAX_HOME: CommonVars[String] = CommonVars("executor.job.engine.datax.home", "")

  val EXECUTOR_JOB_ENGINE_DATAX_PYTHON_SHELL = "python"

  val EXECUTOR_JOB_ENGINE_DATAX_PYTHON_SCRIPT = this.EXECUTOR_JOB_ENGINE_DATAX_HOME + "/bin/datax.py"

  val EXECUTOR_JOB_ENGINE_DATAX_JAVA_MAINCLASS = "com.alibaba.datax.core.Engine"

  val EXECUTOR_JOB_ENGINE_DATAX_JAVA_CLASSPATH = this.EXECUTOR_JOB_ENGINE_DATAX_HOME + "/lib/*"

  val EXECUTOR_JOB_ENGINE_DATAX_JAVA_LOGCONFFILE = this.EXECUTOR_JOB_ENGINE_DATAX_HOME + "/conf/log/logback.xml"

  val EXECUTOR_RESOURCE_THRESHOLD_CPU: CommonVars[Int] = CommonVars[Int]("executor.resource.threshold.cpu", 1)

  val EXECUTOR_RESOURCE_THRESHOLD_MEMORY: CommonVars[Int] = CommonVars[Int]("executor.resource.threshold.memory", 1)

}
