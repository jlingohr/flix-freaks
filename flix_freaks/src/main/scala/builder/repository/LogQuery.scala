package main.scala.builder.repository

import common.domain.auth.UserId
import common.domain.events.EventType

import scala.common.domain.movies.MovieId

trait LogQuery[F[_], EventQuery] {
  type AggregateUserEvent = (UserId, MovieId, EventType, Int)

  def queryLogForUsers: F[Seq[UserId]]

  def queryLogDataForUser(userId: UserId): F[Seq[EventQuery]]

  def queryAggregatedLogDataForUser(userId: UserId): F[Seq[AggregateUserEvent]]

  def queryEvent(eventType: EventType): F[Seq[EventQuery]]

}
