package main.scala.common.model

import common.domain.genres.{Genre, GenreId, GenreName}
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

class GenreTable(tag: Tag) extends Table[Genre](tag, "genre") {
  import common.model.SlickColumnMapper._

  def id: Rep[GenreId] = column[GenreId]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[GenreName] = column[GenreName]("name")

  def * = (id, name) <> (Genre.tupled, Genre.unapply)
}

object Genres {
  val genres = TableQuery[GenreTable]
}
