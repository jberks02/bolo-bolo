package webserver.routes

import models.AccountRequests._
import models.AuthLevel.Administrator
import models.{AuthLevel, BadAuthentication, BadRequest, NotMatchingParameters}
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.directives.RouteDirectives.complete
import org.apache.pekko.http.scaladsl.model.{Multipart, StatusCodes}
import webserver.controllers.AccountControls._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import org.apache.pekko.http.scaladsl.model.headers.RawHeader
import org.apache.pekko.stream.Materializer
import shared.SprayImplicits.*
import webserver.middleware.RouteAuthentication.{authenticateRoute, bytedCryptoToken, encryptWithEmbeddedIV}
import spray.json.*
import shared.AppConfiguration.authHeader
import webserver.controllers.AccountControls

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

def AccountRoutes()(implicit ec: ExecutionContext, mat: Materializer): Route = pathPrefix("account") {
  concat(
    // POST /account/login
    path("login") {
      post {
        entity(as[LoginBody]) { loginBody =>
          onComplete(login(loginBody)) {
            case Success(value) => complete(value)
            case Failure(err: Throwable) =>
              err match
                case ba: BadAuthentication => complete(StatusCodes.Unauthorized, "Passwords did not match")
                case ex: Throwable => complete(StatusCodes.InternalServerError, s"User Could not be authenticated due to internal error: ${ex.getMessage}")
          }
        }
      }
    },
    // POST account/logout
    path("logout") {
      post {
        authenticateRoute { token =>
          val invalidatedToken = encryptWithEmbeddedIV(token.copy(authorized = false).toJson.compactPrint, bytedCryptoToken)
          respondWithHeader(RawHeader(authHeader, invalidatedToken)) {
            complete(StatusCodes.OK, "Logged out")
          }
        }
      }
    },
    // PUT account/register
    path("register") {
      put {
        entity(as[NewUser]) { newUser =>
          onComplete(registerNewUser(newUser)) {
            case Failure(exception) =>
              exception match
                case ba: BadRequest => complete(StatusCodes.BadRequest, s"User could not be created due to bad request: ${ba.getMessage}")
                case ex: Throwable => complete(StatusCodes.InternalServerError, s"User could not be created due to internal error: ${ex.getMessage}")
            case Success(value) => complete(value)
          }
        }
      }
    },
    // PUT account/create-registration-token
    path("create-registration-token") {
      put {
        entity(as[CreationToken]) { newToken =>
          authenticateRoute { token =>
            token.user.auth match
              case AuthLevel.Administrator => onComplete(createNewRegistrationToken(newToken)) {
                case Success(value) => complete(value)
                case Failure(exception) => complete(StatusCodes.InternalServerError, s"Registration token could not be created due to internal error: ${exception.getMessage}")
              }
              case _ => complete(StatusCodes.Forbidden, "You do not have permission to create a registration token")
          }
        }
      }
    },
    // PUT account/update-user-image
    path("update-user-image") {
      put {
        entity(as[Multipart.FormData]) { formData =>
          authenticateRoute { token =>
            onComplete(updateUserProfileImage(token, formData)) {
              case Success(value) => complete(StatusCodes.NoContent)
              case Failure(exception) => complete(StatusCodes.InternalServerError, s"User image could not be updated due to internal error: ${exception.getMessage}")
            }
          }
        }
      }
    },
    // get account/get-user-image/{personId}
    path("get-user-image" / JavaUUID) { personId =>
      get {
        authenticateRoute { _ =>
          onComplete(getUserImage(personId)) {
            case Success(value) => value match
              case Some(image) => complete(StatusCodes.OK, image)
              case None => complete(StatusCodes.NotFound, "User image not found.")
            case Failure(exception) => complete(StatusCodes.InternalServerError, s"User image could not be retrieved due to internal error: ${exception.getMessage}")
          }
        }
      }
    },
    // POST account/promote-user
    path("promote-user") {
      post {
        entity(as[UserPromotion]) { promo =>
          authenticateRoute { token =>
            token.user.auth match
              case AuthLevel.Administrator => onComplete(promoteUser(promo)) {
                case Success(_) => complete(StatusCodes.NoContent)
                case Failure(exception) => complete(StatusCodes.InternalServerError, s"User could not be promoted due to internal error: ${exception.getMessage}")
              }
              case _ => complete(StatusCodes.Forbidden, "You do not have permission to promote a user.")
          }
        }
      }
    },
    // PUT account/update-user-password
    path("update-user-password") {
      put {
        entity(as[PasswordUpdate]) { update =>
          authenticateRoute { token =>
            onComplete(AccountControls.updateUserPassword(token, update)) {
              case Success(_) => complete(StatusCodes.NoContent)
              case Failure(exception) =>
                exception match
                  case e: NotMatchingParameters => complete(StatusCodes.BadRequest, s"Variables in request body did not match: ${e.getMessage}")
                  case e: Throwable => complete(StatusCodes.InternalServerError, s"User password could not be updated due to internal error: ${e.getMessage}")
            }
          }
        }
      }
    },
    path("update-user-token-used") {
      post {
        entity(as[UserPasswordTokenUpdate]) { tokenUpdate =>
          authenticateRoute { token =>
            onComplete(updateTokenUsed(token, tokenUpdate)) {
              case Success(_) => complete(StatusCodes.NoContent)
              case Failure(exception) =>
                exception match
                  case e: NotMatchingParameters => complete(StatusCodes.BadRequest, s"Variables in request body did not match: ${e.getMessage}")
                  case e: Throwable => complete(StatusCodes.InternalServerError, s"User password could not be updated due to internal error: ${e.getMessage}")
            }
          }
        }
      }
    },
    // PUT account/update-user-details
    path("update-user-details") {
      put {
        entity(as[UpdatePersonDetails]) { personDetails =>
          authenticateRoute { token =>
            if token.user.personId.equals(personDetails.personId) || token.user.auth == Administrator then
              onComplete(updatePersonalDetails(personDetails)) {
                case Success(value) => complete(StatusCodes.OK, value)
                case Failure(exception) => complete(StatusCodes.InternalServerError, s"User details could not be updated due to internal error: ${exception.getMessage}")
              }
            else complete(StatusCodes.Forbidden, "You do not have permission to update this user.")
          }
        }
      }
    }
  )
}
