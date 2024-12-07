package shared

import scala.reflect.ClassTag
import models.*
import spray.json._
import spray.json.DefaultJsonProtocol.*
import org.postgresql.util.PGobject
import doobie.util.meta.Meta
import doobie.util.Put
import java.util.UUID
import java.time.LocalDateTime
import doobie._

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
// Doobie implicits start here - For database reads and writes.
private def fromStringToListString(str: String): List[String] =
  str.parseJson.convertTo[List[String]]

private def fromListStringToString(strList: List[String]): String =
  strList.toJson.toString

implicit val stringListGet: Get[List[String]] = Get[String].map(fromStringToListString)
implicit val pgObjectMeta: Meta[PGobject] = Meta.Advanced.other[PGobject]("jsonb")
implicit val listStringJsonbPut: Put[List[String]] = pgObjectMeta.put.contramap[List[String]] { list =>
  val pgObj = new PGobject
  pgObj.setType("jsonb")
  pgObj.setValue(list.toJson.compactPrint)
  pgObj
}
