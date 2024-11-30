package routes

import models.Item
import controllers.*
import shared.*
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import org.apache.pekko.http.scaladsl.model.*
import scala.util.{Failure, Success}
import spray.json.DefaultJsonProtocol.*
import scala.concurrent.ExecutionContext

implicit val ec: ExecutionContext = ExecutionContext.global

val itemRoutes: Route = pathPrefix("item") {
  concat(
    // GET /item/:id - Get item by ID
    path(JavaUUID) { id =>
      get {
        onComplete(getItemById(id)) {
          case Success(Some(item)) => complete(item)
          case Success(None) => complete(StatusCodes.NotFound, "Item not found")
          case Failure(ex) =>
            println(ex.getMessage)
            complete(StatusCodes.InternalServerError, "Transaction could not be processed.")
        }
      }
    },
    // GET /item/description/:desc - Search by description
    path("description" / Segment) { desc =>
      get {
        onComplete(searchByDescription(desc)) {
          case Success(foundItems) if foundItems.nonEmpty => complete(foundItems)
          case Success(_) => complete(StatusCodes.NotFound, "Results not found for that description")
          case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
        }
      }
    },
    // GET /item/commonName/:name - Search by common name
//    path("common-name" / Segment) { name =>
//      get {
//        onComplete(searchByCommonName(name)) {
//          case Success(Some(foundItems)) => complete(foundItems)
//          case Success(None) => complete(StatusCodes.NotFound, "No Items found with a matching common name.")
//          case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
//        }
//      }
//    },
//    // POST /item - Create a new item
//    pathEndOrSingleSlash {
//      post {
//        entity(as[Item]) { item =>
//          onComplete(createItem(item)) {
//            case Success(savedItem) => complete(StatusCodes.Created, savedItem)
//            case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
//          }
//        }
//      }
//    },
//    // PUT /item/documentation-url/:id - Update an existing item documentation urls
//    path("documentation-url" / JavaUUID) { id =>
//      put {
//        entity(as[List[String]]) { completeUrlSet =>
//          onComplete(updateDocumentationUrls(id, completeUrlSet)) {
//            case Success(Some(updatedItem)) => complete(updatedItem)
//            case Success(None) => complete(StatusCodes.NotFound, "Item not found")
//            case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
//          }
//        }
//      }
//    },
    // PUT /item/common-name/:id - Update an existing item documentation urls
//    path("common-name" / JavaUUID) { id =>
//      put {
//        entity(as[List[String]]) { commonNames =>
//          onComplete(updateCommonNames(id, commonNames)) {
//            case Success(Some(updatedItem)) => complete(updatedItem)
//            case Success(None) => complete(StatusCodes.NotFound, "Item does not exist")
//            case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
//          }
//        }
//      }
//    }
  )
}
