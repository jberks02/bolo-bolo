package shared

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}

import scala.reflect.ClassTag
import models.*
import spray.json.*
import spray.json.DefaultJsonProtocol.*

import java.sql.ResultSet
import java.util.UUID
import java.time.LocalDateTime

implicit val uuidFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
  def write(uuid: UUID): JsValue = JsString(uuid.toString)
  def read(json: JsValue): UUID = json match {
    case JsString(s) => UUID.fromString(s)
    case _           => deserializationError("Expected UUID as JsString")
  }
}

implicit val localDateTimeFormat: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime] {
  def write(dt: LocalDateTime): JsValue = JsString(dt.toString)
  def read(json: JsValue): LocalDateTime = json match {
    case JsString(s) => LocalDateTime.parse(s)
    case _           => deserializationError("Expected LocalDateTime as JsString")
  }
}

implicit val itemFormat: RootJsonFormat[Item] = jsonFormat6(Item.apply)
implicit val itemListFormat: RootJsonFormat[List[Item]] = listFormat(itemFormat)

//def resultsTo

private val jsonMapper: JsonMapper with ClassTagExtensions = JsonMapper
  .builder
  .addModule(new JavaTimeModule())
  .addModule(DefaultScalaModule)
  .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  .serializationInclusion(Include.NON_EMPTY)
  .build() :: ClassTagExtensions

def writeToJsonString(value: Any): String = jsonMapper.writeValueAsString(value)
def resultSetToObject[T: ClassTag](value: ResultSet): T = jsonMapper.convertValue[T](value)
def jsonToObject[T: ClassTag](value: String): T = jsonMapper.readValue[T](value)
