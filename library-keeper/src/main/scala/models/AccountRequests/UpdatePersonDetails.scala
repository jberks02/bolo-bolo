package models.AccountRequests

import java.util.UUID

case class UpdatePersonDetails(
                              personId: UUID,
                              firstName: String,
                              middleName: Option[String],
                              lastName: String,
                              email: String
                              )
