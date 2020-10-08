package main.scala.recommender.repository.interpreter

import config.DatabaseConfig
import domain.EventType
import main.scala.recommender.repository.EventRepository
import repository.interpreter.EventSlickRepository.LogTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

trait EventSlickRepository extends EventRepository with DatabaseConfig {
  val table = TableQuery[LogTable]

  def filterContentByEvent(event: EventType, take: Int): Future[Seq[(String, Int)]] = {
    val query =
      table
        .filter(_.event.equals(event))
        .groupBy(_.contentId)
        .map {
          case (id, events) => (id, events.map(_.userId).length)
        }
        .sortBy(_._2.desc.nullsLast)
        .take(take)

    db.run(query.result)
  }
}

object EventRepository {

}
