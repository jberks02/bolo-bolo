package models

import java.util.UUID

case class LoginBody(
                      personId: UUID,
                      password: String,
                      token: String
                    )
