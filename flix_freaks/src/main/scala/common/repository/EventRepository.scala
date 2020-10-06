package repository

import domain.{ContentId, EventType, EventLog, UserId}

import scala.concurrent.Future

trait EventRepository {

  def insert(log: EventLog): Future[Int]

  def getByUserId(userId: UserId): Future[Seq[EventLog]]

  def getByContentId(contentId: ContentId): Future[Seq[EventLog]]

  def getByEventType(eventType: EventType): Future[Seq[EventLog]]

}
