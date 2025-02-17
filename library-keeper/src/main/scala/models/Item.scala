package models

import java.time.LocalDateTime
import java.util.UUID

case class Item(
  itemId: UUID,
  ownerId: UUID,
  itemName: String,
  description: String,
  category: String,
  location: String,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)
