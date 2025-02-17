package webserver.routes

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.model.StatusCodes
import webserver.middleware.RouteAuthentication.authenticateRoute
import webserver.controllers.ItemControls.*
import shared.SprayImplicits.*
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

def ItemRoutes()(implicit ec: ExecutionContext): Route = pathPrefix("items") {
  concat(
    path("search" / Segment) { searchTerm =>
      get {
        authenticateRoute { token =>
          onComplete(searchItemsByName(searchTerm)) {
            case Failure(exception) => complete(StatusCodes.InternalServerError, s"Items could not be retrieved due to internal error: ${exception.getMessage}")
            case Success(value) => complete(value)
          }
        }
      }
    },
    path("user-items" / JavaUUID) { personId =>
      get {
        authenticateRoute{token =>
          onComplete(getUserItems(personId)) {
            case Failure(exception) => complete(StatusCodes.InternalServerError, s"Items could not be retrieved due to internal error: ${exception.getMessage}")
            case Success(value) => complete(value)
          }
        }
      }
    },
    path("item-by-categories" / Segment) {category =>
      get {
        authenticateRoute{token =>
          onComplete(getItemsByCategory(category)) {
            case Failure(exception) => complete(StatusCodes.InternalServerError, s"Items could not be retrieved due to internal error: ${exception.getMessage}")
            case Success(value) => complete(value)
          }
        }
      }
    },
    path("get-category-list") {
      get {
        authenticateRoute{token =>
          onComplete(getCategoryList()) {
            case Failure(exception) => complete(StatusCodes.InternalServerError, s"Items could not be retrieved due to internal error: ${exception.getMessage}")
            case Success(value) => complete(value)
          }
        }
      }
    }
    // TODO: update item details, image, request items, turn in items, facilitate chats
  )
}