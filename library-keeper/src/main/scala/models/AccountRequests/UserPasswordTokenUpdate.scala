package models.AccountRequests

import java.util.UUID

case class UserPasswordTokenUpdate(
                                  previousToken: String,
                                  newToken: String,
                                  password: String,
                                  personId: UUID
                                  )
