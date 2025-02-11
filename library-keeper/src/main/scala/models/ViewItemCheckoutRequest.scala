package models

import java.time.LocalDateTime
import java.util.UUID

//Checkout request with the item in question Added.
case class ViewItemCheckoutRequest(
                                    requestId: UUID,
                                    requesterId: UUID,
                                    itemId: UUID,
                                    requestedAt: LocalDateTime,
                                    details: Item
                                  )
