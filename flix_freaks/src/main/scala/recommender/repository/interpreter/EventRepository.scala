package main.scala.recommender.repository.interpreter

import common.domain.auth.UserId
import common.domain.events.EventType
import main.scala.recommender.repository.EventRepository
import slick.jdbc.PostgresProfile.api._
import main.scala.common.model.Events._
import main.scala.recommender.domain.EventCount
import main.scala.common.model.EventMapper._
import common.model.SlickColumnMapper._

import scala.common.domain.movies.MovieId

class EventSlickRepository extends EventRepository[DBIO] {


  def filterContentByEvent(eventType: EventType, take: Int): DBIO[Seq[EventCount]] = {
    val query =
      events
        .filter(_.event === eventType)
        .groupBy(_.contentId)
        .map {
          case (id, events) => (id, events.map(_.userId).length)
        }
        .sortBy(_._2.desc.nullsLast)
        .take(take)
        .map {
          case (id, count) => ((id, count)) <> (EventCount.tupled, EventCount.unapply)
        }

    query.result
  }

  def getEventsForUser(userId: UserId): DBIO[Seq[MovieId]] = {
    val query =
      events
        .filter(_.userId === userId)
        .sortBy(_.created)
        .map(_.contentId)
        .distinct
    query.result
  }
}
