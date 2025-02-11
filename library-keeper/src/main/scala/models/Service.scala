package models

import java.time.LocalDateTime
import java.util.UUID

case class Service(
                  serviceId: UUID,
                  ownerId: UUID,
                  itemName: String,
                  description: String,
                  location: String,
                  updatedAt: LocalDateTime,
                  createdAt: LocalDateTime
                  )
