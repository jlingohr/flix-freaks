package main.scala.common.model

import common.domain.genres.GenreId
import io.estatico.newtype.ops.toCoercibleIdOps
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.common.domain.movies.{MovieId, Title}


case class MovieGenre(movieId: MovieId, genreId: GenreId)

class MovieGenreTable(tag: Tag) extends Table[MovieGenre](tag, "movie_genre") {
  import common.model.SlickColumnMapper._

  def movieId: Rep[MovieId] = column[MovieId]("movie_id")
  def genreId: Rep[GenreId] = column[GenreId]("genre_id")

  override def * = (movieId, genreId) <> (MovieGenre.tupled, MovieGenre.unapply)

  def pk = primaryKey("primaryKey", (movieId, genreId))

  def movieFK = foreignKey("fk_movie", movieId, TableQuery[MovieTable])(movie =>
    movie.movieId, onDelete = ForeignKeyAction.Cascade)

  def genreFK = foreignKey("fk_genre", genreId, TableQuery[GenreTable])(genre =>
    genre.id)
}

object MovieGenres {
  val movieGenres = TableQuery[MovieGenreTable]
}
