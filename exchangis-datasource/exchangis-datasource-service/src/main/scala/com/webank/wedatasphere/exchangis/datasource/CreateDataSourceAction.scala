package com.webank.wedatasphere.exchangis.datasource

import com.google.gson.{Gson, GsonBuilder, JsonElement, JsonPrimitive, JsonSerializationContext, JsonSerializer}
import com.webank.wedatasphere.linkis.datasource.client.request.DataSourceAction
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction

import java.lang.reflect.Type
import java.{lang, util}
import scala.collection.JavaConversions._

class CreateDataSourceAction extends POSTAction with DataSourceAction {
//  override def getRequestPayload: String = "{\"dataSourceName\": \"123\"}"

  implicit val gson: Gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls
    .registerTypeAdapter(classOf[java.lang.Double], new JsonSerializer[java.lang.Double] {
      override def serialize(t: lang.Double, `type`: Type, jsonSerializationContext: JsonSerializationContext): JsonElement =
        if(t == t.longValue()) new JsonPrimitive(t.longValue()) else new JsonPrimitive(t)
    }).create

  override def getRequestPayload: String = gson.toJson(getRequestPayloads)

  private var user: String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("datasourcesmanager", "info", "json")
}


object CreateDataSourceAction {
  def builder(): Builder = new Builder

  class Builder private[CreateDataSourceAction]() {
    private var user: String = _
    private var payload: util.Map[String, Any] = new util.HashMap[String, Any]

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def addRequestPayload(key: String, value: Any): Builder = {
      if(value != null) this.payload.put(key, value)
      this
    }

    def addRequestPayloads(map: util.Map[String, Any]): Builder = {
      if (null != map) this.payload = map
      this
    }


    def build(): CreateDataSourceAction = {
      val action = new CreateDataSourceAction
      action.setUser(user)

//      for (ele <- payload)
//      var a = scala.collection.mutable.Map[String, Any]
//      val filesHere = (new java.io.File(".")).listFiles
//      for (i <- filesHere) println(i)

      for((k, v) <- this.payload) {
        action.addRequestPayload(k, v)
      }


      action
    }

  }
}