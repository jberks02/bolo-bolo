package shared

import models.Item
import org.scalatest.funsuite.AnyFunSuite
import shared.*
import spray.json.{JsArray, JsString, JsValue, RootJsonFormat}

import java.time.LocalDateTime
import java.util.UUID

class JsonSupportSpec extends AnyFunSuite {
  //uuidFormat test
  test("Write for UUID returns String with correct value") {
    val uu = java.util.UUID.randomUUID()
    val res = uuidFormat.write(uu)
    res match
      case JsString(value) =>
        assert(value == uu.toString)
      case _ => assert(false)
  }
  test("read for UUID should give us a new UUID object with identical properties to the original") {
    val uu = java.util.UUID.randomUUID.toString
    val res = uuidFormat.read(JsString(uu))
    assert(res.toString.equals(uu))
  }
  //localDateTimeFormat
  test("Write for localdatetime to JsString") {
    val date = LocalDateTime.now()
    val res = localDateTimeFormat.write(date)
    res match
      case JsString(value) => assert(value == date.toString)
      case _ => assert(false)
  }
  test("read for localDatetime should receive jsString and return datetime") {
    val date = LocalDateTime.now()
    val write = localDateTimeFormat.write(date)
    val read = localDateTimeFormat.read(write)
    assert(read.equals(date))
  }
  
}
