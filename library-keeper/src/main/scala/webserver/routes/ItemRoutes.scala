package webserver.routes

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model.StatusCodes
import scala.concurrent.ExecutionContext

def ItemRoutes()(implicit ec: ExecutionContext): Route = pathPrefix("items") {
  concat(
    path("search") {
      complete(StatusCodes.OK, "Found")
    } 
  )
}