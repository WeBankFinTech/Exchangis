package org.apache.linkis.engineconnplugin.datax.config

import org.apache.linkis.common.conf.CommonVars

/**
 * Datax setting configuration
 */
object DataxSettingConfiguration {

  /**
   * Sync meta
   */
  val SETTING_SYNC_META: CommonVars[Boolean] = CommonVars("setting.syncMeta", false)

  /**
   * Transport type
   */
  val SETTING_TRANSPORT_TYPE: CommonVars[String] = CommonVars("setting.transport.type", "record")

  /**
   * Key version for encrypt
   */
  val SETTING_KEY_VERSION: CommonVars[String] = CommonVars("setting.keyVersion", "")

  /**
   * Speed limit in byte(s)
   */
  val SETTING_SPEED_BYTE: CommonVars[Int] = CommonVars("setting.speed.byte", 1048576)

  /**
   * Speed limit in record(s)
   */
  val SETTING_SPEED_RECORD: CommonVars[Int] = CommonVars("setting.speed.record", 100000)

  /**
   * Speed limit in channel(s)
   */
  val SETTING_SPEED_CHANNEL: CommonVars[Int] = CommonVars("setting.speed.channel", 0)

  /**
   * Error limit in record
   */
  val SETTING_ERROR_LIMIT_RECORD: CommonVars[Int] = CommonVars("setting.errorLimit.record", 0)

  /**
   * If use processor
   */
  val SETTING_USE_PROCESSOR: CommonVars[Boolean] = CommonVars("setting.useProcessor", false)
}
