package models

import java.time.LocalDateTime
import java.util.UUID

case class Item(
  itemId: UUID,
  ownerId: UUID,
  itemName: String,
  description: Option[String],
  category: String,
  location: Option[String],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)
