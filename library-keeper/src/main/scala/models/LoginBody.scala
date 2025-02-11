package models

case class LoginBody(
                    firstName: String,
                    lastName: String,
                    password: String,
                    token: String
                    )
