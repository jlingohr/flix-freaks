package repository.interpreter

import config.DatabaseConfig
import domain.Genre
import main.scala.common.repository.Genres._
import repository.GenreRepository
import slick.dbio.NoStream
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction

import scala.concurrent.Future

trait GenreSlickRepository extends GenreRepository with DatabaseConfig {

  implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }

  def findAll: Future[Seq[Genre]] = genres.result

  def create(genre: Genre): Future[Int] = genres.returning(genres.map(_.id)) += genre

  def findById(genreId: Int): Future[Genre] = genres.filter(_.id === genreId).result.head

  def delete(genreId: Int): Future[Int] = genres.filter(_.id === genreId).delete
}

object GenreSlickRepository extends GenreSlickRepository