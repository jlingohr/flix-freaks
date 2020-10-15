package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import main.scala.flix_freaks.akkahttp.akkahttp.QueryRoute
import main.scala.flix_freaks.service.interpreter.MovieService

import scala.io.StdIn

object FlixFreaks extends App {
  implicit val system = ActorSystem(Behaviors.empty,"flix-freaks")
  implicit val executionContext = system.executionContext

  val movieService = new MovieService()
  val routes = new QueryRoute(movieService)

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes.routes)

  println(s"Server online at http://localhost:8080/\n Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
