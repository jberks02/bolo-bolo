package shared

import models.{AuthLevel, Person}

import java.security.MessageDigest

object CommonFunctions {
  def sha256hash(input: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = digest.digest(input.getBytes("UTF-8"))
    hashedBytes.map("%02x".format(_)).mkString
  }
  def isUserAtLevel(user: Person, desiredLevel: AuthLevel): Boolean = {
    AuthLevel.toInteger(user.auth) >= AuthLevel.toInteger(desiredLevel)
  }
}
