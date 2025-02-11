package models

import java.time.LocalDateTime
import java.util.UUID

// Checkout case class with an added communication set. 
case class CommunicationSet(
                             checkoutId: UUID,
                             itemId: UUID,
                             personId: UUID,
                             checkoutDate: LocalDateTime,
                             dueDate: LocalDateTime,
                             returnDate: LocalDateTime,
                             communications: List[Communication]
                           )
