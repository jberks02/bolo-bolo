package models.AccountRequests

import java.util.UUID

case class PasswordUpdate(previousPassword: String, newPassword: String, token: String, personId: UUID)
