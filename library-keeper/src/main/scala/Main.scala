import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import webserver.HttpServerActor
import webserver.HttpServerActor.WebServerCommand

import scala.io.StdIn.readLine

@main def libraryServer(): Unit = {
  val root: Behavior[Nothing] = Behaviors.setup { context => 
    val supervisedWebServer: Behavior[HttpServerActor.WebServerCommand] = 
      Behaviors
        .supervise(HttpServerActor())
        .onFailure[Exception](SupervisorStrategy.restart)
    val webServerRef: ActorRef[HttpServerActor.WebServerCommand] = context.spawn(supervisedWebServer, "http-server")
    webServerRef ! HttpServerActor.StartWebServer
    Behaviors.empty
  }
  val system: ActorSystem[Nothing] = ActorSystem(root, "library-system")
  system.log.info("System Online. Ready for requests. Press Enter to kill program")
  readLine
  system.terminate()
}