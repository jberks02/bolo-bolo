package models

import java.time.LocalDateTime
import java.util.UUID

case class PersonHistory(
                          personId: UUID,
                          auth: AuthLevel,
                          firstName: String,
                          lastName: String,
                          email: String,
                          createdAt: LocalDateTime,
                          updatedAt: LocalDateTime,
                          operationType: String,
                          changedAt: LocalDateTime
                        )
