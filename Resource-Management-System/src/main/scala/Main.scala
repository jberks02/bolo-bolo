import scala.concurrent.ExecutionContextExecutor
import org.apache.pekko
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.Http
import routes.*

@main
def RMSServer(): Unit = {
  given system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "RMS-Server")
  given executionContext: ExecutionContextExecutor = system.executionContext
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(itemRoutes)
  println("Server online at http://localhost:8080/hello\nPress RETURN to stop...")
  scala.io.StdIn.readLine() // Keep the server running until user presses return
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
