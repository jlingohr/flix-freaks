package repository

import common.domain.auth.UserId
import common.domain.ratings.UserRating

import scala.common.domain.movies.MovieId


trait RatingRepository[F[_]] {

  def getByUser(userId: UserId): F[Seq[UserRating]]

  def getByMovie(movieId: MovieId): F[Seq[UserRating]]

}
