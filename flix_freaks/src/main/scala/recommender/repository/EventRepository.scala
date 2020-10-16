package main.scala.recommender.repository

import domain.{EventType, UserId}
import main.scala.recommender.domain.EventCount


trait EventRepository[F[_]] {

  def filterContentByEvent(event: EventType, take: Int): F[Seq[EventCount]]

  def getEventsForUser(userId: UserId): F[Seq[String]]

}
