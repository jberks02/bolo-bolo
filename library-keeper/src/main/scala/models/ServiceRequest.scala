package models

import java.time.LocalDateTime
import java.util.UUID

case class ServiceRequest(
                         serviceRequestId: UUID,
                         requesterId: UUID,
                         serviceId: UUID,
                         requestedAt: LocalDateTime
                         )
