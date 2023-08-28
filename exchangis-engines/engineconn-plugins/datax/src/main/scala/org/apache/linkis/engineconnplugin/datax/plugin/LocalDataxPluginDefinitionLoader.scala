package org.apache.linkis.engineconnplugin.datax.plugin
import com.alibaba.datax.common.util.Configuration
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.common.utils.{JsonUtils, Logging}
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconnplugin.datax.config.DataxConfiguration
import org.apache.linkis.engineconnplugin.datax.context.DataxPluginDefinition
import org.apache.linkis.engineconnplugin.datax.exception.DataxPluginLoadException
import org.apache.linkis.engineconnplugin.datax.plugin.LocalDataxPluginDefinitionLoader.{PLUGIN_JSON_NAME, PLUGIN_NAME, PLUGIN_PATH}
import org.apache.linkis.manager.engineplugin.common.launch.process.Environment

import java.io.File
import java.util
import java.util.Base64
/**
 * Local plugin definition loader
 */
class LocalDataxPluginDefinitionLoader extends DataxPluginDefinitionLoader with Logging{
  /**
   * Load plugin
   *
   * @param engineCreationContext engine create context
   * @return
   */
  override def loadPlugin(engineCreationContext: EngineCreationContext): util.List[DataxPluginDefinition] = {
    val options = engineCreationContext.getOptions
    val plugins = new util.ArrayList[DataxPluginDefinition]()
    val pluginDefineSet: util.Set[String] = new util.HashSet[String]()
    DataxConfiguration.PLUGIN_RESOURCES.getValue(options) match {
      case encryptRes: String =>
        if (StringUtils.isNotBlank(encryptRes)) {
          // First to decode the resources
          val resources = new String(Base64.getDecoder.decode(encryptRes), "utf-8");
          val mapper = JsonUtils.jackson
          val pluginResources: Array[PluginResource] = mapper.readValue(resources,
            mapper.getTypeFactory.constructArrayType(classOf[PluginResource]))
          val workDir = CommonVars(Environment.PWD.toString, "").getValue
          if (StringUtils.isBlank(workDir)) {
            throw new DataxPluginLoadException(s"Cannot get the working directory from variable: 'PWD' in datax engine conn environment", null)
          }
          Option(pluginResources).foreach(resources => resources.foreach(
            resource => Option(convertPluginResourceToDefine(pluginDefineSet, resource, workDir))
              .foreach(definition => plugins.add(definition))))
        }
      case _ =>
    }
    plugins
  }

  private def convertPluginResourceToDefine(pluginDefineSet: util.Set[String], resource: PluginResource, workDir: String): DataxPluginDefinition = {
    // Skip the path has value '.'
    resource.getPath match {
      case "." => null
      case _ =>
        // Search and load the resource definition at work directory
        val resLocalFile = new File(workDir, new File(resource.getPath).getName)
        if (resLocalFile.isDirectory) {
          val pluginConf: Configuration = Configuration.from(new File(resLocalFile.getPath, PLUGIN_JSON_NAME))
          val pluginName: String = pluginConf.getString(PLUGIN_NAME)
          var pluginPath: String = pluginConf.getString(PLUGIN_PATH)
          if (pluginDefineSet.contains(pluginName)) {
            throw new DataxPluginLoadException(s"Fail to load plugin [name: ${pluginName}, path: ${pluginPath}], duplicated plugin exists", null)
          }
          pluginDefineSet.add(pluginName)
          if (StringUtils.isBlank(pluginPath)) {
            pluginPath = resLocalFile.getPath
            pluginConf.set(PLUGIN_PATH, pluginPath)
          }
          new DataxPluginDefinition(pluginName, pluginPath, pluginConf)
        } else {
          warn(s"Cannot find the plugin resource in path: [${resLocalFile.getPath}], please examine the working directory: [${workDir}]")
          null
        }
    }
  }
}

object LocalDataxPluginDefinitionLoader{

  val PLUGIN_JSON_NAME = "plugin.json"

  val PLUGIN_PATH = "path"

  val PLUGIN_NAME = "name"
  def apply(): LocalDataxPluginDefinitionLoader = new LocalDataxPluginDefinitionLoader()

}
