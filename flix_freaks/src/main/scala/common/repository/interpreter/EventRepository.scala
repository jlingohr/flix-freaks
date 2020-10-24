package repository.interpreter

import common.domain.auth.UserId
import common.domain.events.{EventLog, EventType}
import main.scala.common.model.EventMapper
import main.scala.common.model.Events._
import repository.EventRepository
import slick.jdbc.PostgresProfile.api._

import scala.common.domain.movies.MovieId
import common.model.SlickColumnMapper._

class EventSlickRepository extends EventRepository[DBIO] {
  import EventMapper._

  def insert(log: EventLog): DBIO[Int] = events += log

  def getByUserId(userId: UserId): DBIO[Seq[EventLog]] = events.filter(_.userId === userId).result

  def getByContentId(contentId: MovieId): DBIO[Seq[EventLog]] = events.filter(_.contentId === contentId).result

  def getByEventType(eventType: EventType): DBIO[Seq[EventLog]] = events.filter(_.event === eventType).result
}

