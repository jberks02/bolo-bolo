package models

import java.time.LocalDateTime

case class AuthToken(
                    user: Person,
                    lastAuth: LocalDateTime, 
                    lastCheck: LocalDateTime,
                    authorized: Boolean
                    )
