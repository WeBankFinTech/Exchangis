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

/**
 * Datax basic config
 */
object DataxConfiguration {

  val CONFIG_PREFIX: String = "_datax_."
  /**
   * Environment config name
   */
  val ENV_CONFIG_NAME: CommonVars[String] = CommonVars[String]("datax.env.config.name", "entry.environment")

  /**
   * Fetch interval
   */
  val STATUS_FETCH_INTERVAL: CommonVars[TimeType] = CommonVars("wds.linkis.engineconn.datax.fetch.status.interval", new TimeType("5s"))

  /**
   * Execution id
   */
  val JOB_EXECUTION_ID: CommonVars[String] = CommonVars[String]("wds.linkis.engineconn.datax.execution.id", "")

  /**
   * Plugin resources
   */
  val PLUGIN_RESOURCES: CommonVars[String] = CommonVars[String]("wds.linkis.engineconn.datax.bml.resources", "")

  /**
   * Security manager
   */
  val SECURITY_MANAGER_CLASSES: CommonVars[String] = CommonVars[String]("wds.linkis.engineconn.datax.security.manager", "")
}
