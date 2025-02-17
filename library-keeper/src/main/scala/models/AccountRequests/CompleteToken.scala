package models.AccountRequests

import java.time.LocalDateTime

case class CompleteToken(
                          token: String,
                          createdAt: LocalDateTime,
                          expiration: LocalDateTime,
                          valid: Boolean
                        )
                        
