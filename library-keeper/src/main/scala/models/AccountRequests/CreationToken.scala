package models.AccountRequests

import java.time.LocalDateTime

case class CreationToken(
                        createdAt: LocalDateTime,
                        expiration: LocalDateTime,
                        valid: Boolean
                        )
