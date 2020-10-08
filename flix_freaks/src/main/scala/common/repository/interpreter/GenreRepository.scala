package repository.interpreter

import config.DatabaseConfig
import domain.Genre
import repository.{GenreRepository, GenreTable}
import slick.dbio.NoStream
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import slick.sql.SqlAction

import scala.concurrent.Future

trait GenreSlickRepository extends GenreRepository with DatabaseConfig {
  val table = TableQuery[GenreTable]

  implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }

  def findAll: Future[Seq[Genre]] = table.result

  def create(genre: Genre): Future[Int] = table.returning(table.map(_.id)) += genre

  def findById(genreId: Int): Future[Genre] = table.filter(_.id === genreId).result.head

  def delete(genreId: Int): Future[Int] = table.filter(_.id === genreId).delete
}

object GenreSlickRepository extends GenreSlickRepository