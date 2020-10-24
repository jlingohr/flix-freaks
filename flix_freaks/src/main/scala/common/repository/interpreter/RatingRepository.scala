package repository.interpreter

import common.domain.auth.UserId
import common.domain.ratings.UserRating
import main.scala.common.model.Ratings._
import repository.RatingRepository
import slick.jdbc.PostgresProfile.api._

import scala.common.domain.movies.MovieId


class RatingSlickRepository extends RatingRepository[DBIO] {
  import common.model.SlickColumnMapper._

  override def getByUser(userId: UserId): DBIO[Seq[UserRating]] = ratings.filter(_.userId === userId).result

  override def getByMovie(movieId: MovieId): DBIO[Seq[UserRating]] = ratings.filter(_.movieId === movieId).result
}
