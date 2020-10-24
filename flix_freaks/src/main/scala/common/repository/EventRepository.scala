package repository

import common.domain.auth.UserId
import common.domain.events.{EventLog, EventType}

import scala.common.domain.movies.MovieId


trait EventRepository[F[_]] {

  def insert(log: EventLog): F[Int]

  def getByUserId(userId: UserId): F[Seq[EventLog]]

  def getByContentId(contentId: MovieId): F[Seq[EventLog]]

  def getByEventType(eventType: EventType): F[Seq[EventLog]]

}
