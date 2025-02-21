package webserver.controllers

import models.{BadRequest, NotMatchingParameters}
import webserver.middleware.RouteAuthentication.{bytedCryptoToken, encrypt, encryptWithEmbeddedIV}
import webserver.queries.AuthChecks.*
import dataConnectors.PostgersqlConnector.{executeInsert, executeQuery}
import models.AccountRequests.{AuthToken, CompleteToken, CreationToken, LoginBody, NewUser, PasswordUpdate, UpdatePersonDetails, UserPasswordTokenUpdate, UserPromotion}
import models.AuthLevel.{Administrator, Viewer}
import models.Person
import org.apache.pekko.http.scaladsl.model.Multipart.FormData
import org.apache.pekko.stream.Materializer
import org.apache.pekko.util.ByteString
import spray.json.*
import shared.SprayImplicits.*
import shared.CommonFunctions.{parseFormData, sha256hash}

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object AccountControls {
  private def generateEncryptedPassword(passWordEncryptionToken: String, password: String, email: String): String = {
    val iv: Array[Byte] = passWordEncryptionToken.getBytes
    encrypt(password, email.getBytes, iv)
  }
  def login(loginBody: LoginBody)(implicit ec: ExecutionContext): Future[String] = {
    val start = LocalDateTime.now
    val encryptedPassword: String = generateEncryptedPassword(loginBody.token, loginBody.password, loginBody.email)
    executeQuery(getUserEncPassword(loginBody.email)).flatMap{password =>
      if password.head == loginBody.password then
        executeQuery(getUserData(loginBody.email)).map{person =>
          val token = AuthToken(person.head, start, start, true)
          encryptWithEmbeddedIV(token.toJson.compactPrint, bytedCryptoToken)
        }
      else throw BadRequest("Invalid password, token, and user id combination.")
    }
  }
  def registerNewUser(newUser: NewUser)(implicit ec: ExecutionContext): Future[String] = {
    newUser.token match
      case Some(value) =>
        executeQuery(getToken(value)).flatMap{validated =>
          val now = LocalDateTime.now
          val newPersonRecord = newUser.person.copy(auth = Viewer)
          if validated.head.valid && validated.head.expiration.isAfter(now) then
            val encryptedPassword = generateEncryptedPassword(newUser.passwordEncryptionToken, newUser.password, newUser.person.email)
            //insert and invalidate token in parallel
            executeInsert(insertNewPerson(newPersonRecord, newUser.password)).zip(executeInsert(invalidateToken(value))).map(_ =>
              encryptWithEmbeddedIV(AuthToken(newPersonRecord, now, now, true).toJson.compactPrint, bytedCryptoToken)
            )
          else throw BadRequest("Token is invalid or expired. Contact your system administrator.")
        }
      case None =>
        executeQuery(getCountOfPersons()).flatMap{count =>
          if count.head == 0 then
            val now = LocalDateTime.now
            val newPersonRecord = newUser.person.copy(auth = Administrator)
            val encryptedPassword = generateEncryptedPassword(newUser.passwordEncryptionToken, newUser.password, newUser.person.email)
            executeInsert(insertNewPerson(newPersonRecord, newUser.password)).map(_ =>
              encryptWithEmbeddedIV(AuthToken(newPersonRecord, LocalDateTime.now, LocalDateTime.now, true).toJson.compactPrint, bytedCryptoToken)
            )
          else throw BadRequest("Token is required for new users of an occupied system.")
        }
  }
  def createNewRegistrationToken(token: CreationToken)(implicit ec: ExecutionContext): Future[CompleteToken] = {
    val newToken = sha256hash(token.toJson.compactPrint)
    val completeToken = CompleteToken(newToken, token.createdAt, token.expiration, token.valid)
    executeInsert(insertNewToken(completeToken)).map(_ => completeToken)
  }
  def updateUserProfileImage(token: AuthToken, image: FormData)(implicit ec: ExecutionContext, mat: Materializer): Future[Unit] = {
    parseFormData(image).flatMap{ bytes =>
      executeInsert(updatePersonImage(bytes, token.user.personId)).map(_ => ())
    }
  }
  def promoteUser(promotion: UserPromotion)(implicit ec: ExecutionContext): Future[Unit] = {
    executeInsert(updatePersonAuth(promotion)).map(_ => ())
  }
  def updateUserPassword(token: AuthToken, update: PasswordUpdate)(implicit ec: ExecutionContext): Future[Unit] = {
    if token.user.email.equals(update.email) then
      val previousPassword = generateEncryptedPassword(update.token, update.previousPassword, update.email)
      val newPassword = generateEncryptedPassword(update.token, update.newPassword, update.email)
      executeQuery(getUserEncPassword(update.email)).flatMap{encPassword =>
        if encPassword.head == previousPassword then
          executeInsert(updatePersonPassword(newPassword, update.personId)).map(_ => ())
        else throw NotMatchingParameters("Previous password does not match the sent password.")
      }
    else throw NotMatchingParameters("Person id does not match token person id. Only the authenticated user can update their own password.")
  }
  def updateTokenUsed(token: AuthToken, newToken: UserPasswordTokenUpdate)(implicit ec: ExecutionContext): Future[Unit] = {
    if token.user.personId.equals(newToken.email) then
      val previousEnc = generateEncryptedPassword(newToken.previousToken, newToken.password, newToken.email)
      val newEnc = generateEncryptedPassword(newToken.newToken, newToken.password, newToken.email)
      executeQuery(getUserEncPassword(newToken.email)).flatMap{encPass =>
        if encPass.head == previousEnc then
          executeInsert(updatePersonPassword(newEnc, newToken.personId)).map(_ => ())
        else throw NotMatchingParameters("Mismatch for previous token and password.")
      }
    else throw NotMatchingParameters("Person id does not match request body person id. Only the authenticated user can update the token used for their password decryption.")
  }
  def updatePersonalDetails(details: UpdatePersonDetails)(implicit ec: ExecutionContext): Future[Person] = {
    executeInsert(updateDetails(details)).flatMap(_ =>
      executeQuery(getUserDataById(details.personId)).map(pList => pList.head)
    )
  }
  def getUserImage(personId: UUID)(implicit ec: ExecutionContext): Future[Option[Array[Byte]]] = {
    executeQuery(getUserImageData(personId)).map(_.head)
  }
}
