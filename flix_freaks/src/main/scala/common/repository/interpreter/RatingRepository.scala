package repository.interpreter

import config.DatabaseConfig
import domain.{Rating, UserId}
import main.scala.common.model.Ratings._
import repository.RatingRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

trait RatingSlickRepository extends RatingRepository with DatabaseConfig {

  override def getByUser(userId: UserId): Future[Seq[Rating]] = db.run(ratings.filter(_.userId === userId.value).result)

  override def getByMovie(movieId: String): Future[Seq[Rating]] = db.run(ratings.filter(_.movieId === movieId).result)
}

object RatingSlickRepository extends RatingSlickRepository