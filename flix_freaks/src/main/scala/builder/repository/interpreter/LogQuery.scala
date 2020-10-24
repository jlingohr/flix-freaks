package main.scala.builder.repository.interpreter

import common.domain.auth.UserId
import common.domain.events.{EventLog, EventType}
import main.scala.builder.repository.LogQuery
import slick.jdbc.PostgresProfile.api._
import main.scala.common.model.Events._
import main.scala.common.model.EventMapper._
import common.model.SlickColumnMapper._

class LogSlickQuery extends LogQuery[DBIO, EventLog] {

  def queryLogForUsers: DBIO[Seq[UserId]] = {
    val action = events.map(_.userId).distinct
    action.result
  }

  def queryLogDataForUser(userId: UserId): DBIO[Seq[EventLog]] = {
    val action = events.filter(_.userId === userId)
    action.result
  }

  def queryAggregatedLogDataForUser(userId: UserId): DBIO[Seq[AggregateUserEvent]] = {
    val action =
      events
        .filter(_.userId === userId)
        .groupBy(table => (table.userId, table.contentId, table.event))
        .map {
          case (id, row) => (id._1, id._2, id._3, row.length)
        }

    action.result

  }

  override def queryEvent(eventType: EventType): DBIO[Seq[EventLog]] =
    events.filter(_.event === eventType).result
}

object LogQuery extends LogSlickQuery