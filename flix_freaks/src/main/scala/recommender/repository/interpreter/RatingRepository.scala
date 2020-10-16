package main.scala.recommender.repository.interpreter

import domain.{Rating, UserId}
import slick.jdbc.PostgresProfile.api._

import main.scala.common.model.Ratings._
import main.scala.recommender.repository.RatingRepository

class RatingSlickRepository extends RatingRepository[DBIO, Rating]{

  def filterUser(userId: UserId): DBIO[Seq[Rating]] =
    ratings.filter(_.userId === userId.value).result

  def filterMoviesIn(movies: Set[String]): DBIO[Seq[(String, String)]] = {
    val query =
      ratings
        .filter(_.movieId.inSet(movies))
        .groupBy(t => (t.movieId, t.userId))
        .map {
          case ((movieId, userId), rating) => ((movieId, userId))
        }
    query.result
  }

  def filterUsersIn(users: Set[String]): DBIO[Seq[Rating]] = {
    val query =
      ratings
        .filter(_.userId.inSet(users))
    query.result
  }

  def getAvgRating(userId: UserId, itemId: String): DBIO[Option[BigDecimal]] = {
    ratings.filter(t => t.userId =!= userId.value && t.movieId === itemId).map(_.rating).avg.result

  }

  def getNotRatedBy(userId: UserId, take: Int): DBIO[Seq[(String, Int, Option[BigDecimal])]] = {
    val query = ratings
      .filter(_.userId =!= userId.value)
      .groupBy(_.movieId)
      .map {
        case (id, rating) => (id, rating.map(_.userId).length, rating.map(_.rating).avg)
      }
      .sortBy(_._3.desc.nullsLast)
      .take(take)

    query.result

  }

  def getAvgRatingForItem(itemId: String): DBIO[Option[Option[BigDecimal]]] = {
    val query =
      ratings
        .filter(_.movieId === itemId)
        .groupBy(_.movieId)
        .map {
          case (id, ratings) => ratings.map(_.rating).avg
        }
        .result
        .headOption

    query
  }
}

object RatingRepository extends RatingSlickRepository
