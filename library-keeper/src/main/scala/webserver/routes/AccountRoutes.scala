package webserver.routes

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.directives.RouteDirectives.complete
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity}

def AccountRoutes: Route = concat(
  path("login") {
    
  }
)
