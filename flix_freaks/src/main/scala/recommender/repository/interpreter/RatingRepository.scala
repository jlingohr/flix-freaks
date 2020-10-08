package main.scala.recommender.repository.interpreter

import config.DatabaseConfig
import domain.UserId
import repository.{RatingRepository, RatingTable}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

trait RatingSlickRepository extends RatingRepository with DatabaseConfig {
  val table = TableQuery[RatingTable]

  def getAvgRating(userId: UserId, itemId: String): Future[Option[BigDecimal]] = {
    db.run(
      table.filter(t => t.userId =!= userId.value && t.movieId === itemId).map(_.rating).avg.result
    )
  }

  def getNotRatedBy(userId: UserId, take: Int): Future[Seq[(String, Int, Option[BigDecimal])]] = {
    val query = table
      .filter(_.userId =!= userId.value)
      .groupBy(_.movieId)
      .map {
        case (id, rating) => (id, rating.map(_.userId).length, rating.map(_.rating).avg)
      }
      .sortBy(_._3.desc.nullsLast)
      .take(take)

    db.run(query.result)

  }

  def getAvgRatingForItem(itemId: String): Future[Option[Option[BigDecimal]]] = {
    val query =
      table
        .filter(_.movieId === itemId)
        .groupBy(_.movieId)
        .map {
          case (id, ratings) => ratings.map(_.rating).avg
        }
        .result
        .headOption

    db.run(query)
  }
}

object RatingRepository {

}
