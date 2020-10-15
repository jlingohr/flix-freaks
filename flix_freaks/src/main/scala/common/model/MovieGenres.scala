package main.scala.common.model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag


case class MovieGenre(movieId: String, genreId: Int)

class MovieGenreTable(tag: Tag) extends Table[MovieGenre](tag, "movie_genre") {
  def movieId: Rep[String] = column[String]("movie_id")
  def genreId: Rep[Int] = column[Int]("genre_id")

  override def * = (movieId, genreId) <> ((MovieGenre.apply _).tupled, MovieGenre.unapply)

  def pk = primaryKey("primaryKey", (movieId, genreId))

  def movieFK = foreignKey("fk_movie", movieId, TableQuery[MovieTable])(movie =>
    movie.movieId, onDelete = ForeignKeyAction.Cascade)

  def genreFK = foreignKey("fk_genre", genreId, TableQuery[GenreTable])(genre =>
    genre.id)
}

object MovieGenres {
  val movieGenres = TableQuery[MovieGenreTable]
}
