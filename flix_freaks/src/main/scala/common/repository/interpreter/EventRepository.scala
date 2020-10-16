package repository.interpreter

import domain._
import main.scala.common.model.EventMapper
import main.scala.common.model.Events._
import repository.EventRepository
import slick.jdbc.PostgresProfile.api._


class EventSlickRepository extends EventRepository[DBIO] {
  import EventMapper._

  def insert(log: EventLog): DBIO[Int] = events += log

  def getByUserId(userId: UserId): DBIO[Seq[EventLog]] = events.filter(_.userId === userId.value).result

  def getByContentId(contentId: ContentId): DBIO[Seq[EventLog]] = events.filter(_.contentId === contentId.value).result

  def getByEventType(eventType: EventType): DBIO[Seq[EventLog]] = events.filter(_.event === eventType).result
}

object EventSlickRepository extends EventSlickRepository
