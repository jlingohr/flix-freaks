package main.scala.collector.app

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{parameters, _}
import main.scala.collector.service.LogSlickPublisherActor

import scala.io.StdIn


object Collector extends App {

  implicit val system: ActorSystem[LogSlickPublisherActor.EventMessage] = ActorSystem(LogSlickPublisherActor(),"flix-collector")
  implicit val executionContext = system.executionContext

  val route =
    path("log") {
      concat(
        post {
          parameters("user_id", "content_id", "event_type", "session_id") {
            (userId, contentId, eventType, sessionId) =>
              system ! LogSlickPublisherActor.Event(userId, contentId, eventType, sessionId)
              complete(StatusCodes.Accepted, "Event logged")
          }
        }
      )
    }

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
