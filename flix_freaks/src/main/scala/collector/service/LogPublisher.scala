package main.scala.collector.service

import java.time.Instant

import akka.actor.typed.scaladsl.Behaviors
import config.DatabaseConfig
import domain.{EventLog, EventType}
import main.scala.common.model.LogTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object LogSlickPublisherActor extends DatabaseConfig {
  val table = TableQuery[LogTable]

  sealed trait EventMessage
  case class Event(userId: String, contentId: String, eventType: String, sessionId: String) extends EventMessage

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
