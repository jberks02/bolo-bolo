package webserver.routes

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.directives.RouteDirectives.complete
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity}

def BasicRoutes: Route = concat(
  path("ready") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Server is ready to take requests</h1>"))
    }
  },
  path("health") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Server Health is good</h1>"))
    }
  },
  path("swagger.json") {
    getFromResource("swagger.json", ContentTypes.`application/json`)
  },
  path("api-docs") {
    getFromBrowseableDirectory("src/main/resources/swagger-ui")
  }
)
 
