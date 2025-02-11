package webserver

import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.util.Timeout
import webserver.routes.*

object HttpServerActor {
  sealed trait WebServerCommand
  case object StartWebServer extends WebServerCommand
  def apply(): Behavior[WebServerCommand] = {
    Behaviors.receive { (context, message) =>
      import context.executionContext
      message match
        case StartWebServer => 
          val routes = Route.toFlow(concat(BasicRoutes, ItemRoutes()))(context.system)
          Http()(context.system).newServerAt("0.0.0.0", 8080).bindFlow(routes)
          context.log.info("Server online on port 8080")
          Behaviors.same
    }
  }
}

