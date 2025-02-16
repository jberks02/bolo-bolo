package webserver.controllers

import models.{AuthToken, BadRequest, CompleteToken, CreationToken, LoginBody, NewUser}
import webserver.middleware.RouteAuthentication.{bytedCryptoToken, encrypt, encryptWithEmbeddedIV}
import webserver.queries.AuthChecks.*
import dataConnectors.PostgersqlConnector.{executeInsert, executeQuery}
import spray.json.*
import shared.SprayImplicits.*

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object AccountControls {
  private def generateEncryptedPassword(passWordEncryptionToken: String, password: String, personId: UUID): String = {
    val iv: Array[Byte] = passWordEncryptionToken.getBytes
    encrypt(password, personId.toString.getBytes, iv)
  }
  def login(loginBody: LoginBody)(implicit ec: ExecutionContext): Future[String] = {
    val start = LocalDateTime.now
    val encryptedPassword: String = generateEncryptedPassword(loginBody.token, loginBody.password, loginBody.personId)
    executeQuery(getUserEncPassword(loginBody.personId)).flatMap{password =>
      if password.head == loginBody.password then
        executeQuery(getUserData(loginBody.personId)).map{person =>
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
          if validated.head.valid && validated.head.expiration.isAfter(now) then
            val encryptedPassword = generateEncryptedPassword(newUser.passwordEncryptionToken, newUser.password, newUser.person.personId)
            executeInsert(insertNewPerson(newUser.person, encryptedPassword)).map(_ =>
              encryptWithEmbeddedIV(AuthToken(newUser.person, now, now, true).toJson.compactPrint, bytedCryptoToken)
            )
          else throw BadRequest("Token is invalid or expired. Contact your system administrator.")
        }
      case None =>
        executeQuery(getCountOfPersons()).flatMap{count =>
          if count.head == 0 then
            val encryptedPassword = generateEncryptedPassword(newUser.passwordEncryptionToken, newUser.password, newUser.person.personId)
            executeInsert(insertNewPerson(newUser.person, newUser.password)).map(_ =>
              encryptWithEmbeddedIV(AuthToken(newUser.person, LocalDateTime.now, LocalDateTime.now, true).toJson.compactPrint, bytedCryptoToken)
            )
          else throw BadRequest("Token is required for new users of an occupied system.")
        }
  }
  def createNewRegistrationToken(token: CreationToken)(implicit ec: ExecutionContext): Future[CompleteToken] = ???
}
