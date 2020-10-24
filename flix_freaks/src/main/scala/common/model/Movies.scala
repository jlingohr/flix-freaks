package main.scala.common.model

import io.estatico.newtype.ops.toCoercibleIdOps
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.common.domain.movies.{Movie, MovieId, Title}



class MovieTable(tag: Tag) extends Table[Movie](tag, "movie") {
  import common.model.SlickColumnMapper._

  def movieId = column[MovieId]("movie_id", O.PrimaryKey)
  def title = column[Title]("title")
  def year = column[Option[Int]]("year")

  override def * = (movieId, title, year) <> (Movie.tupled, Movie.unapply)

  //  def genres = foreignKey("SUP_FK", movie_id, TableQuery[Genre])(_.id)
}

object Movies {
  val movies = TableQuery[MovieTable]
}
