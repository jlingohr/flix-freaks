package repository.interpreter

import domain.{Rating, UserId}
import main.scala.common.model.Ratings._
import repository.RatingRepository
import slick.jdbc.PostgresProfile.api._


class RatingSlickRepository extends RatingRepository[DBIO] {

  override def getByUser(userId: UserId): DBIO[Seq[Rating]] = ratings.filter(_.userId === userId.value).result

  override def getByMovie(movieId: String): DBIO[Seq[Rating]] = ratings.filter(_.movieId === movieId).result
}

object RatingSlickRepository extends RatingSlickRepository