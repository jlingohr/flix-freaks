package repository.interpreter

import common.domain.genres.{Genre, GenreId}
import main.scala.common.model.Genres._
import repository.GenreRepository
import slick.jdbc.PostgresProfile.api._


class GenreSlickRepository extends GenreRepository[DBIO] {
  import common.model.SlickColumnMapper._

  def findAll: DBIO[Seq[Genre]] = genres.result

  def create(genre: Genre): DBIO[GenreId] = genres.returning(genres.map(_.id)) += genre

  def findById(genreId: GenreId): DBIO[Genre] = genres.filter(_.id === genreId).result.head

  def delete(genreId: GenreId): DBIO[Int] = genres.filter(_.id === genreId).delete
}

