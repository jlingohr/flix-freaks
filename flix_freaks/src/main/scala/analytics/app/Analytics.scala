package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akkahttp.QueryRoute
import repository.interpreter.{EventSlickRepository, GenreSlickRepository, MovieSlickRepository, RatingSlickRepository}
import service.interpretor.AnalyticServiceInterpreter

import scala.io.StdIn

object Analytics extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty,"flix-analytics")
  implicit val executionContext = system.executionContext

  val service = new AnalyticServiceInterpreter(RatingSlickRepository, MovieSlickRepository, EventSlickRepository, GenreSlickRepository)

  val routes = new QueryRoute(service)

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes.routes)

  println(s"Server online at http://localhost:8080/\n Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
