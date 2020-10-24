package main.scala.recommender.repository.interpreter

import common.domain.auth.UserId
import common.domain.ratings.UserRating
import slick.jdbc.PostgresProfile.api._
import main.scala.common.model.Ratings._
import main.scala.recommender.repository.RatingRepository
import common.model.SlickColumnMapper._

import scala.common.domain.movies.MovieId

class RatingSlickRepository extends RatingRepository[DBIO, UserRating]{

  def filterUser(userId: UserId): DBIO[Seq[UserRating]] =
    ratings.filter(_.userId === userId).result

  def filterMoviesIn(movies: Set[MovieId]): DBIO[Seq[(MovieId, UserId)]] = {
    val query =
      ratings
        .filter(_.movieId.inSet(movies))
        .groupBy(t => (t.movieId, t.userId))
        .map {
          case ((movieId, userId), rating) => ((movieId, userId))
        }
    query.result
  }

  def filterUsersIn(users: Set[UserId]): DBIO[Seq[UserRating]] = {
    val query =
      ratings
        .filter(_.userId.inSet(users))
    query.result
  }

  def getAvgRating(userId: UserId, itemId: MovieId): DBIO[Option[BigDecimal]] = {
    ratings.filter(t => t.userId =!= userId && t.movieId === itemId).map(_.rating).avg.result
  }

  def getNotRatedBy(userId: UserId, take: Int): DBIO[Seq[(MovieId, Int, Option[BigDecimal])]] = {
    val query = ratings
      .filter(_.userId =!= userId)
      .groupBy(_.movieId)
      .map {
        case (id, rating) => (id, rating.map(_.userId).length, rating.map(_.rating).avg)
      }
      .sortBy(_._3.desc.nullsLast)
      .take(take)

    query.result
  }

  def getAvgRatingForItem(itemId: MovieId): DBIO[Option[Option[BigDecimal]]] = {
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
