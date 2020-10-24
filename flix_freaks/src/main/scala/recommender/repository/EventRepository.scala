package main.scala.recommender.repository

import common.domain.auth.UserId
import common.domain.events.EventType
import main.scala.recommender.domain.EventCount

import scala.common.domain.movies.MovieId


trait EventRepository[F[_]] {

  def filterContentByEvent(event: EventType, take: Int): F[Seq[EventCount]]

  def getEventsForUser(userId: UserId): F[Seq[MovieId]]

}
