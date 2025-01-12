import scala.concurrent.ExecutionContextExecutor
import org.apache.pekko
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import routes.*
import systemactors.WebServer

@main
def RMSServer(): Unit = {
  val rootSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "RMS-Server")
  WebServer.startWebServer(rootSystem)
}
