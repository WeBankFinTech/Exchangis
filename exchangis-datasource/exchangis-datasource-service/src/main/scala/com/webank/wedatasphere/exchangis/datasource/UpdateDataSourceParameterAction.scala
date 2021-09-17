package com.webank.wedatasphere.exchangis.datasource

import com.google.gson._
import com.webank.wedatasphere.linkis.datasource.client.request.DataSourceAction
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction

import java.{lang, util}
import java.lang.reflect.Type
import scala.collection.JavaConversions._

class UpdateDataSourceParameterAction(val id: Long) extends POSTAction with DataSourceAction {
  implicit val gson: Gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls
    .registerTypeAdapter(classOf[java.lang.Double], new JsonSerializer[java.lang.Double] {
      override def serialize(t: lang.Double, `type`: Type, jsonSerializationContext: JsonSerializationContext): JsonElement =
        if(t == t.longValue()) new JsonPrimitive(t.longValue()) else new JsonPrimitive(t)
    }).create

  override def getRequestPayload: String = gson.toJson(getRequestPayloads)

  private var user: String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("datasourcemanager", "parameter", id+"", "json")
}

object UpdateDataSourceParameterAction {
  def builder(): Builder = new Builder

  class Builder private[UpdateDataSourceParameterAction]() {
    private var user: String = _
    private var resourceId: Long = _
    private var payload: util.Map[String, Any] = new util.HashMap[String, Any]

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setDataSourceId(id: Long): Builder = {
      this.resourceId = id
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


    def build(): UpdateDataSourceParameterAction = {
      val action = new UpdateDataSourceParameterAction(this.resourceId)
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


