package scala

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object FlixFreaksMain extends App {
  implicit val system: ActorSystem = ActorSystem("flix-freaks")
  implicit val materializer = ActorMaterializer
  implicit val ec = system.dispatcher

  private val config = ConfigFactory.load("application.conf")
  val module = new AkkaModule(config)

  val host = "localhost"
  val port = 8080
  val serverBinding = Http().bindAndHandle(module.routes, host, port)

  serverBinding.onComplete {
    case Success(b) =>
      println("Server launched at http://{}:{}/", b.localAddress.getHostString, b.localAddress.getPort)
    case Failure(e) =>
      println("Server could not start!", e)
      e.printStackTrace()
      system.terminate()
      module.close
  }

  Await.result(system.whenTerminated, Duration.Inf)

}
