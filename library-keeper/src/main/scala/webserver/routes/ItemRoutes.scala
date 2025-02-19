package webserver.routes

import models.{BadRequest, NotMatchingParameters}
import models.ItemRequests.*
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.model.{Multipart, StatusCodes}
import webserver.middleware.RouteAuthentication.authenticateRoute
import webserver.controllers.ItemControls.*
import shared.SprayImplicits.*
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import org.apache.pekko.stream.Materializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

def ItemRoutes()(implicit ec: ExecutionContext, mat: Materializer): Route = pathPrefix("items") {
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
            case Failure(exception) =>
              exception match
                case ec: NoSuchElementException => complete(StatusCodes.NotFound, "Items not found for this category.")
                case ec: Throwable => complete(StatusCodes.InternalServerError, s"Items could not be retrieved due to internal error: ${exception.getMessage}")
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
    },
    path("update-item-details") {
      put {
        entity(as[UpdateItemDetails]){ detailsUpdate =>
          authenticateRoute {token =>
            onComplete(updateItemDetails(token, detailsUpdate)) {
              case Success(value) => complete(value)
              case Failure(ec) =>
                ec match
                  case ec: NotMatchingParameters => complete(StatusCodes.BadRequest, "Item details could not be updated you do not own this item.")
                  case exception: Throwable => complete(StatusCodes.InternalServerError, s"Item details could not be updated due to internal error: ${exception.getMessage}")
            }
          }
        }
      }
    },
    path("update-item-image" / JavaUUID) { itemId =>
      put {
        entity(as[Multipart.FormData]) { formData =>
          authenticateRoute { token =>
            onComplete(updateImage(token, itemId, formData)) {
              case Success(_) => complete(StatusCodes.NoContent)
              case Failure(ec) =>
                ec match
                  case ec: NotMatchingParameters => complete(StatusCodes.BadRequest, s"Item image could not be updated you do not own this item: ${ec.getMessage}")
                  case ec: Throwable => complete(StatusCodes.InternalServerError, s"Item image could not be updated due to internal error: ${ec.getMessage}")
            }
          }
        }
      }
    },
    path("get-item-image" / JavaUUID) { itemId =>
      get {
        authenticateRoute{_ =>
          onComplete(getImage(itemId)) {
            case Success(value) => complete(value)
            case Failure(ec) =>
              ec match
                case ec: NoSuchElementException => complete(StatusCodes.NotFound, "Item Not image not found.")
                case ec: Throwable => complete(StatusCodes.InternalServerError, s"Item image could not be retrieved due to internal error: ${ec.getMessage}")
          }
        }
      }
    }
    // TODO: request items, turn in items, facilitate chats
  )
}