package models

case class NewUser(
                    token: Option[String],
                    password: String,
                    passwordEncryptionToken: String,
                    person: Person,
                  )
