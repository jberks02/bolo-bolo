package models.AccountRequests

import models.Person

case class NewUser(
                    token: Option[String],
                    password: String,
                    passwordEncryptionToken: String,
                    person: Person,
                  )
