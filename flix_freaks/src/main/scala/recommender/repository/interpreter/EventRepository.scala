package main.scala.recommender.repository.interpreter

import config.DatabaseConfig
import domain.EventType
import main.scala.recommender.repository.EventRepository
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future
import main.scala.common.repository.Events._
import main.scala.recommender.domain.EventCount

import main.scala.common.repository.EventMapper._


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
}

object EventSlickRepository extends EventSlickRepository