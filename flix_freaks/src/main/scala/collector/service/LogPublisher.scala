package main.scala.collector.service

import java.time.Instant

import akka.actor.typed.scaladsl.Behaviors
import common.domain.auth.UserId
import common.domain.events.{EventLog, EventType, SessionId}
import config.DatabaseConfig
import main.scala.common.model.LogTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.common.domain.movies.MovieId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object LogSlickPublisherActor extends DatabaseConfig {
  val table = TableQuery[LogTable]

  sealed trait EventMessage
  case class Event(userId: UserId, contentId: MovieId, eventType: String, sessionId: SessionId) extends EventMessage

  def apply(): Behaviors.Receive[EventMessage] = Behaviors.receive {
    case (ctx, event @ Event(userId, contentId, eventType, sessionId)) => {
      val event = EventLog(0, Instant.now(), userId, contentId, EventType.apply(eventType), sessionId)
      db.run(table += event)
        .onComplete {
          case Success(value) => ???
          case Failure(exception) => println("Error writing event to database", exception)
        }
      Behaviors.same
    }
  }

}
