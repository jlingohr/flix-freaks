package repository

import domain.{Rating, UserId}

import scala.concurrent.Future

trait RatingRepository {

  def getByUser(userId: UserId): Future[Seq[Rating]]

  def getByMovie(movieId: String): Future[Seq[Rating]]

}
