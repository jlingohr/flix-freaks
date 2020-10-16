package main.scala.recommender.repository.interpreter

import domain.{EventType, UserId}
import main.scala.recommender.repository.EventRepository
import slick.jdbc.PostgresProfile.api._

import main.scala.common.model.Events._
import main.scala.recommender.domain.EventCount
import main.scala.common.model.EventMapper._


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
          case (id, count) => ((id, count)) <> (EventCount.tupled, EventCount.unapply _)
        }

    query.result
  }

  def getEventsForUser(userId: UserId): DBIO[Seq[String]] = {
    val query =
      events
        .filter(_.userId === userId.value)
        .sortBy(_.created)
        .map(_.contentId)
        .distinct
    query.result
  }
}

object EventSlickRepository extends EventSlickRepository