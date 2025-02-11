package models

import java.time.LocalDateTime
import java.util.UUID

//Person case class with a list or Items attached that the person owns.
case class OwnedItems(
                       personId: UUID,
                       auth: AuthLevel,
                       firstName: String,
                       lastName: String,
                       email: String,
                       createdAt: LocalDateTime,
                       updatedAt: LocalDateTime,
                       items: List[Item]
                     )
