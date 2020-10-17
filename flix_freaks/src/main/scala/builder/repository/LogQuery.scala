package main.scala.builder.repository

import domain.{EventType, UserId}

trait LogQuery[F[_], EventQuery] {
  type AggregateUserEvent = (String, String, String, Int)

  def queryLogForUsers: F[Seq[String]]

  def queryLogDataForUser(userId: UserId): F[Seq[EventQuery]]

  def queryAggregatedLogDataForUser(userId: UserId): F[Seq[AggregateUserEvent]]

  def queryEvent(eventType: EventType): F[Seq[EventQuery]]

}
