package main.scala.builder.repository

import domain.{EventLog, EventType, UserId}

import scala.concurrent.Future

trait LogQuery[EventQuery] {
  type AggregateUserEvent = (String, String, String, Int)

  def queryLogForUsers: Future[Seq[String]]

  def queryLogDataForUser(userId: UserId): Future[Seq[EventQuery]]

  def queryAggregatedLogDataForUser(userId: UserId): Future[Seq[AggregateUserEvent]]

  def queryEvent(eventType: EventType): Future[Seq[EventQuery]]

}
