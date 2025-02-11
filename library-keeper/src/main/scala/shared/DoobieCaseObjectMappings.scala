package shared

import doobie.util.meta.Meta
import models.AuthLevel

object DoobieCaseObjectMappings {
  Meta[Int].imap(auth => AuthLevel.fromInteger(auth))(auth => AuthLevel.toInteger(auth))
}