package systemactors

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.stream.Materializer
import org.apache.pekko.util.Timeout

import scala.concurrent.duration.*
//import org.apache.pekko.http.impl.util.JavaMapping.Implicits.convertToScala
//import org.apache.pekko.http.javadsl.server.RoutingJavaMapping.Implicits.convertToScal
import routes._

object WebServer {
  def startWebServer(rootSystem: ActorSystem[Nothing]): Unit = {
    import rootSystem.executionContext
    implicit val timeout: Timeout = 3.seconds
    implicit val mat: Materializer = org.apache.pekko.stream.Materializer(rootSystem)
    val routes = Route.toFlow(concat(BareRoutes, getItemRoutes()))(rootSystem)
    Http()(rootSystem).newServerAt("0.0.0.0", 8080).bindFlow(routes)
    println("Server online at http://localhost:8080/")
  }
}
