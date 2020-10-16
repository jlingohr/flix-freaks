package repository.interpreter

import domain.Genre
import main.scala.common.model.Genres._
import repository.GenreRepository
import slick.jdbc.PostgresProfile.api._


class GenreSlickRepository extends GenreRepository[DBIO] {

  def findAll: DBIO[Seq[Genre]] = genres.result

  def create(genre: Genre): DBIO[Int] = genres.returning(genres.map(_.id)) += genre

  def findById(genreId: Int): DBIO[Genre] = genres.filter(_.id === genreId).result.head

  def delete(genreId: Int): DBIO[Int] = genres.filter(_.id === genreId).delete
}

object GenreSlickRepository extends GenreSlickRepository