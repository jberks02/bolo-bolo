package models

import java.time.LocalDateTime
import java.util.UUID

case class Checkout(
                   checkoutId: UUID,
                   itemId: UUID,
                   personId: UUID,
                   checkoutDate: LocalDateTime,
                   dueDate: LocalDateTime,
                   returnDate: LocalDateTime
                   )
