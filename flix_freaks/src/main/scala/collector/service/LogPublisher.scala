package main.scala.collector.service

import java.time.Instant

import akka.actor.typed.scaladsl.Behaviors
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.scaladsl.Source
import config.DatabaseConfig
import domain.{EventLog, EventType}
import repository.interpreter.EventSlickRepository.LogTable
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
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
