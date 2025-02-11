package models

import java.time.LocalDateTime
import java.util.UUID

case class Communication(
                        messageId: UUID,
                        senderId: UUID,
                        recipientId: UUID,
                        checkoutId: UUID,
                        message: String,
                        sentAt: LocalDateTime,
                        readAt: LocalDateTime
                        )
