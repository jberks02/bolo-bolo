package shared

import models.AccountRequests.*
import models.ItemRequests.UpdateItemDetails
import models.{AuthLevel, Item, Person}
import spray.json.DefaultJsonProtocol.*
import spray.json.*

import java.time.LocalDateTime
import java.util.UUID

object SprayImplicits {
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
      case _ => deserializationError("Expected LocalDateTime as JsString")
    }
  }
  implicit val authLevelFormat: JsonFormat[AuthLevel] = new JsonFormat[AuthLevel] {
    def write(auth: AuthLevel): JsValue = JsNumber(AuthLevel.toInteger(auth))
    def read(js: JsValue): AuthLevel = js match
      case JsNumber(value) => AuthLevel.fromInteger(value.toInt)
      case _ => deserializationError("Invalid json type received for AuthLevel")
  }
  implicit val loginBodyFormat: RootJsonFormat[LoginBody] = jsonFormat3(LoginBody.apply)
  implicit val personFormat: RootJsonFormat[Person] = jsonFormat8(Person.apply)
  implicit val authTokenFormat: RootJsonFormat[AuthToken] = jsonFormat4(AuthToken.apply)
  implicit val newUserFormat: RootJsonFormat[NewUser] = jsonFormat4(NewUser.apply)
  implicit val creationTokenFormat: RootJsonFormat[CreationToken] = jsonFormat3(CreationToken.apply)
  implicit val completeTokenFormat: RootJsonFormat[CompleteToken] = jsonFormat4(CompleteToken.apply)
  implicit val userPromotionFormat: RootJsonFormat[UserPromotion] = jsonFormat2(UserPromotion.apply)
  implicit val userPasswordUpdateFormat: RootJsonFormat[PasswordUpdate] = jsonFormat5(PasswordUpdate.apply)
  implicit val userPasswordTokenUpdateFormat: RootJsonFormat[UserPasswordTokenUpdate] = jsonFormat5(UserPasswordTokenUpdate.apply)
  implicit val personDetailsUpdateFormat: RootJsonFormat[UpdatePersonDetails] = jsonFormat5(UpdatePersonDetails.apply)
  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat8(Item.apply)
  implicit val updateItemDetailsFormat: RootJsonFormat[UpdateItemDetails] = jsonFormat5(UpdateItemDetails.apply)
  implicit val itemListFormat: RootJsonFormat[List[Item]] = listFormat(itemFormat)
  implicit val stringListFormat: RootJsonFormat[List[String]] = listFormat(StringJsonFormat)
}
