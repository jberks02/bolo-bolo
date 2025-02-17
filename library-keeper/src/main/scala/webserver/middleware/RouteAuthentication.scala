package webserver.middleware

import models.AccountRequests.AuthToken
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.model.headers.RawHeader
import shared.AppConfiguration.{authHeader, authTimeout, cyrptoToken}
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.{Directive1, RequestContext}
import java.util.Base64
import java.nio.ByteBuffer
import spray.json._
import shared.SprayImplicits._
import java.time.LocalDateTime
import javax.crypto.Cipher
import java.util.UUID
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

object RouteAuthentication {
  private val Transformation = "AES/CBC/PKCS5Padding"
  private val CharSet = java.nio.charset.StandardCharsets.UTF_8
  val bytedCryptoToken: Array[Byte] = cyrptoToken.getBytes(CharSet)
  private def ivToString(ivStr: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(ivStr)
  }
  private def ivFromString(ivStr: String): Array[Byte] = {
    Base64.getDecoder.decode(ivStr)
  }
  private def generateIVFromUUID: Array[Byte] = {
    val uuid = UUID.randomUUID()
    val buffer = ByteBuffer.allocate(16)
    buffer.putLong(uuid.getMostSignificantBits)
    buffer.putLong(uuid.getLeastSignificantBits)
    buffer.array
  }
  def encrypt(plaintext: String, key: Array[Byte], iv: Array[Byte]): String = {
    require(key.length == 32, "Key must be 32 bytes for AES-256.")
    require(iv.length == 16, "IV must be 16 bytes.")
    val secretKeySpec = new SecretKeySpec(key, "AES")
    val ivSpec = new IvParameterSpec(iv)
    val cipher = Cipher.getInstance(Transformation)
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
    val encryptedBytes = cipher.doFinal(plaintext.getBytes(CharSet))
    Base64.getEncoder.encodeToString(encryptedBytes)
  }
  private def decrypt(ciphertextBase64: String, key: Array[Byte], iv: Array[Byte]): String = {
    require(key.length == 32, "Key must be 32 bytes for AES-256.")
    require(iv.length == 16, "IV must be 16 bytes.")
    val secretKeySpec = new SecretKeySpec(key, "AES")
    val ivSpec = new IvParameterSpec(iv)
    val cipher = Cipher.getInstance(Transformation)
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
    val decryptedBytes = cipher.doFinal(Base64.getDecoder.decode(ciphertextBase64))
    new String(decryptedBytes, CharSet)
  }
  def encryptWithEmbeddedIV(plaintext: String, key: Array[Byte]): String = {
    val iv = generateIVFromUUID
    val ciphertextBase64 = encrypt(plaintext, key, iv)
    val ivBase64 = ivToString(iv)
    s"$ivBase64:$ciphertextBase64"
  }
  def decryptWithEmbeddedIV(combined: String, key: Array[Byte]): String = {
    val parts = combined.split(":", 2)
    require(parts.length == 2, "Invalid combined format. Expect 'IV:CIPHERTEXT'")
    val (ivPart, cipherPart) = (parts(0), parts(1))
    val iv = ivFromString(ivPart)
    decrypt(cipherPart, key, iv)
  }
  def authenticateRoute: Directive1[AuthToken] = {
    extractRequestContext.flatMap{(ctx: RequestContext) =>
      val reqPath = ctx.request.uri.path.toString
      ctx.request.headers.find(_.is(authHeader)) match
        case None => complete(StatusCodes.Unauthorized, "No auth token found")
        case Some(header) =>
          val decryptedString = decryptWithEmbeddedIV(header.value, bytedCryptoToken)
          val token = decryptedString.parseJson.convertTo[AuthToken]
          if token.lastCheck.isBefore(LocalDateTime.now.plusMinutes(authTimeout.toLong)) then
            val nextToken = token.copy(lastCheck = LocalDateTime.now).toJson.compactPrint
            val newHeader = RawHeader(authHeader, encryptWithEmbeddedIV(nextToken, bytedCryptoToken))
            val nextHeaders = ctx.request.headers.filterNot(_.is(authHeader)) :+ newHeader
            val updatedRequest = ctx.request.withHeaders(nextHeaders)
            mapRequestContext(_ => ctx.withRequest(updatedRequest)) & provide(token)
          else
            complete(StatusCodes.Unauthorized, "Token not in acceptable state.")
    }
  }
}
