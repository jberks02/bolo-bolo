package models

import java.time.LocalDateTime
import java.util.UUID

case class ItemHistory(
                        itemId: UUID,
                        ownerId: UUID,
                        itemName: String,
                        description: String,
                        location: String,
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime,
                        operationType: String,
                        changedAt: LocalDateTime
                      )
