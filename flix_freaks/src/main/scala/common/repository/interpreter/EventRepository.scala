package repository.interpreter

import java.time.Instant

import config.DatabaseConfig
import domain._
import main.scala.common.repository.EventMapper
import repository.EventRepository
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{Query, Tag}

import scala.concurrent.{ExecutionContext, Future}
import main.scala.common.repository.Events._


class EventSlickRepository()(implicit ec: ExecutionContext) extends EventRepository with DatabaseConfig {
  import EventMapper._

  def insert(log: EventLog): Future[Int] = db.run(events += log)

  def getByUserId(userId: UserId): Future[Seq[EventLog]] = db.run(events.filter(_.userId === userId.value).result)

  def getByContentId(contentId: ContentId): Future[Seq[EventLog]] = db.run(events.filter(_.contentId === contentId.value).result)

  def getByEventType(eventType: EventType): Future[Seq[EventLog]] = db.run(events.filter(_.event === eventType).result)
}
