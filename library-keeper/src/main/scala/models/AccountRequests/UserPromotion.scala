package models.AccountRequests

import models.AuthLevel

import java.util.UUID

case class UserPromotion(personId: UUID, authLevel: AuthLevel)
