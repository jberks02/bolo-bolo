package webserver.routes

import models.{AuthLevel, BadAuthentication, BadRequest, CreationToken, LoginBody, NewUser}
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.directives.RouteDirectives.complete
import org.apache.pekko.http.scaladsl.model.StatusCodes
import webserver.controllers.AccountControls.{createNewRegistrationToken, login, registerNewUser}
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import shared.SprayImplicits.*
import webserver.middleware.RouteAuthentication.authenticateRoute

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

def AccountRoutes()(implicit ec: ExecutionContext): Route = concat(
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
  path("register") {
    post {
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
  path("create-registration-token") {
      post {
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
  }
)
