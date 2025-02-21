package models.AccountRequests

import java.util.UUID

case class LoginBody(
                      email: String,
                      password: String,
                      token: String
                    )
