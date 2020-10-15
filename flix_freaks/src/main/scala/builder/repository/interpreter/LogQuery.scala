package main.scala.builder.repository.interpreter

import config.DatabaseConfig
import domain.{EventLog, EventType, UserId}
import main.scala.builder.repository.LogQuery

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import main.scala.common.model.Events._
import main.scala.common.model.EventMapper._

import scala.concurrent.ExecutionContext.Implicits.global

//case class AggregateUserEvent(userId: String, contentId: String, event: String, count: Int)

class LogSlickQuery extends LogQuery[EventLog] with DatabaseConfig {

  def queryLogForUsers: Future[Seq[String]] = {
    val action = events.map(_.userId).distinct
    db.run(action.result)
  }

  def queryLogDataForUser(userId: UserId): Future[Seq[EventLog]] = {
    val action = events.filter(_.userId === userId.value)
    db.run(action.result)
  }

  def queryAggregatedLogDataForUser(userId: UserId): Future[Seq[AggregateUserEvent]] = {
    val action =
      events
        .filter(_.userId === userId.value)
        .groupBy(table => (table.userId, table.contentId, table.event))
        .map {
          case (id, row) => (id._1, id._2, id._2, row.length)
        }

    db.run(action.result)

  }

  override def queryEvent(eventType: EventType): Future[Seq[EventLog]] =
    db.run(events.filter(_.event === eventType).result)
}

object LogQuery extends LogSlickQuery