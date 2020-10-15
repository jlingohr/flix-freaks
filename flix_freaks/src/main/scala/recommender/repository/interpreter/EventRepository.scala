package main.scala.recommender.repository.interpreter

import config.DatabaseConfig
import domain.{EventLog, EventType, UserId}
import main.scala.recommender.repository.EventRepository
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future
import main.scala.common.model.Events._
import main.scala.recommender.domain.EventCount
import main.scala.common.model.EventMapper._


class EventSlickRepository extends EventRepository with DatabaseConfig {


  def filterContentByEvent(eventType: EventType, take: Int): Future[Seq[EventCount]] = {
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

    db.run(query.result)
  }

  def getEventsForUser(userId: UserId): Future[Seq[String]] = {
    val query =
      events
        .filter(_.userId === userId.value)
        .sortBy(_.created)
        .map(_.contentId)
        .distinct
    db.run(query.result)
  }
}

object EventSlickRepository extends EventSlickRepository