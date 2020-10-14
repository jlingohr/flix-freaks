package main.scala.recommender.repository

import domain.{EventLog, EventType, UserId}
import main.scala.recommender.domain.EventCount

import scala.concurrent.Future

trait EventRepository {

  def filterContentByEvent(event: EventType, take: Int): Future[Seq[EventCount]]

  def getEventsForUser(userId: UserId): Future[Seq[String]]

}
