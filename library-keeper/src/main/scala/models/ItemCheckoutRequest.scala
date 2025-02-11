package models

import java.time.LocalDateTime
import java.util.UUID

case class ItemCheckoutRequest(
                              requestId: UUID,
                              requesterId: UUID,
                              itemId: UUID,
                              requestedAt: LocalDateTime
                              )
