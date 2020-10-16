package repository

import domain.{ContentId, EventLog, EventType, UserId}


trait EventRepository[F[_]] {

  def insert(log: EventLog): F[Int]

  def getByUserId(userId: UserId): F[Seq[EventLog]]

  def getByContentId(contentId: ContentId): F[Seq[EventLog]]

  def getByEventType(eventType: EventType): F[Seq[EventLog]]

}
