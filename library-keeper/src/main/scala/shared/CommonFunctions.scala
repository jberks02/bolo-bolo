package shared

import models.{AuthLevel, Person}
import org.apache.pekko.http.scaladsl.model.Multipart.FormData
import org.apache.pekko.stream.Materializer
import org.apache.pekko.util.ByteString

import java.security.MessageDigest
import scala.concurrent.{ExecutionContext, Future}

object CommonFunctions {
  def sha256hash(input: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = digest.digest(input.getBytes("UTF-8"))
    hashedBytes.map("%02x".format(_)).mkString
  }
  def isUserAtLevel(user: Person, desiredLevel: AuthLevel): Boolean = {
    AuthLevel.toInteger(user.auth) >= AuthLevel.toInteger(desiredLevel)
  }
  def parseFormData(formData: FormData)(implicit ec: ExecutionContext, mat: Materializer): Future[Array[Byte]] = {
    formData.parts.mapAsync(1) { part =>
      part.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
    }.runFold(ByteString.empty)(_ ++ _).map(_.toArray)
  }
}
