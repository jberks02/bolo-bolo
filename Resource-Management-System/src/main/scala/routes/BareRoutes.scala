package routes


import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model._

def BareRoutes: Route = concat( path("ready") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Server is ready to take requests.</h1>"))
    }
  },
  path("health") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Server health is good.</h1>"))
    }
  },
  path("swagger.json") {
    getFromResource("swagger.json", ContentTypes.`application/json`)
  },
  pathPrefix("api-docs") {
    getFromBrowseableDirectory("src/main/resources/swagger-ui/")
  }
)

