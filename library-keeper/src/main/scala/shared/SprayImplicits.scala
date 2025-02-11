package shared

import models.{AuthLevel, AuthToken, Person}
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.DefaultJsonProtocol.*
import spray.json.{JsArray, JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

import java.time.LocalDateTime
import java.util.UUID

object SprayImplicits {
  implicit val uuidFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)
    def read(json: JsValue): UUID = json match {
      case JsString(s) => UUID.fromString(s)
      case _ => deserializationError("Expected UUID as JsString")
    }
  }
  implicit val localDateTimeFormat: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime] {
    def write(dt: LocalDateTime): JsValue = JsString(dt.toString)
    def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => LocalDateTime.parse(s)
      case _ => deserializationError("Expected LocalDateTime as JsString")
    }
  }
  implicit val authLevelFormat: JsonFormat[AuthLevel] = new JsonFormat[AuthLevel] {
    def write(auth: AuthLevel): JsValue = JsNumber(AuthLevel.toInteger(auth))
    def read(js: JsValue): AuthLevel = js match
      case JsNumber(value) => AuthLevel.fromInteger(value.toInt)
      case _ => deserializationError("Invalid json type received for AuthLevel")
  }
  implicit val personFormat: RootJsonFormat[Person] = jsonFormat7(Person.apply)
  implicit val authTokenFormat: RootJsonFormat[AuthToken] = jsonFormat3(AuthToken.apply)
}
