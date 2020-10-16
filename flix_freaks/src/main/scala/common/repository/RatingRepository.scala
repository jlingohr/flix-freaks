package repository

import domain.{Rating, UserId}

import scala.concurrent.Future

trait RatingRepository[F[_]] {

  def getByUser(userId: UserId): F[Seq[Rating]]

  def getByMovie(movieId: String): F[Seq[Rating]]

}
