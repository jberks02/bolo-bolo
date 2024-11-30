package models

import java.util.UUID
import java.time.LocalDateTime

case class Item(itemId: UUID,
                description: String,
                commonNames: List[String],
                documentationUrls: List[String],
                lastUpdate: LocalDateTime,
                created: LocalDateTime
               )
