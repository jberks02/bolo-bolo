package models

import java.time.LocalDateTime
import java.util.UUID

sealed trait AuthLevel;
object AuthLevel {
  case object Administrator extends AuthLevel
  case object Manager extends AuthLevel
  case object Editor extends AuthLevel
  case object Contributor extends AuthLevel
  case object Viewer extends AuthLevel
  def fromInteger(level: Int): AuthLevel = {
    level match
      case 0 => Administrator
      case 1 => Manager
      case 2 => Editor
      case 3 => Contributor
      case 4 => Viewer
      case _ => throw Exception(s"Level sent, $level, is out of range and does not map to an authorization level")
  }
  def toInteger(level: AuthLevel): Int = {
    level match
      case Administrator => 0
      case Manager => 1
      case Editor => 2
      case Contributor => 3
      case Viewer => 4
  }
}

case class Person(
                 personId: UUID,
                 auth: AuthLevel,
                 firstName: String,
                 lastName: String,
                 email: String,
                 createdAt: LocalDateTime,
                 updatedAt: LocalDateTime
                 )

