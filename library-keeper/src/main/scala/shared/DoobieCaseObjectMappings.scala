package shared

import doobie.util.meta.Meta
import models.AuthLevel
import org.postgresql.util.PGobject

object DoobieCaseObjectMappings {
  implicit val pgObjectMeta: Meta[PGobject] = Meta.Advanced.other[PGobject]("jsonb")
  implicit val authLevelMeta: Meta[AuthLevel] = Meta[Int].imap(AuthLevel.fromInteger)(AuthLevel.toInteger)
}