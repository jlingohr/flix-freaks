package repository.interpreter

import config.DatabaseConfig
import domain.{Rating, UserId}
import repository.{RatingRepository, RatingTable}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

trait RatingSlickRepository extends RatingRepository with DatabaseConfig {
  val table = TableQuery[RatingTable]

  override def getByUser(userId: UserId): Future[Seq[Rating]] = db.run(table.filter(_.userId === userId.value).result)

  override def getByMovie(movieId: String): Future[Seq[Rating]] = db.run(table.filter(_.movieId === movieId).result)
}

object RatingSlickRepository extends RatingSlickRepository