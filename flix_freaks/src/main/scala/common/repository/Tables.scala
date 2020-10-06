package repository

import java.sql.Timestamp
import java.time.Instant
import java.util.Date

import domain.{Genre, Movie, Rating}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

class GenreTable(tag: Tag) extends Table[Genre](tag, "genre") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")

  def * = (id.?, name) <> ((Genre.apply _).tupled, Genre.unapply)
}

class MovieTable(tag: Tag) extends Table[Movie](tag, "movie") {
  def movieId = column[String]("movie_id", O.PrimaryKey)
  def title = column[String]("title")
  def year = column[Option[Int]]("year")

  override def * = (movieId, title, year) <> ((Movie.apply _).tupled, Movie.unapply)

//  def genres = foreignKey("SUP_FK", movie_id, TableQuery[Genre])(_.id)
}

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

object DateMapper {
  implicit val dateToTimestamp = MappedColumnType.base[Date, Timestamp](
    { utilDate => new Timestamp(utilDate.getTime()) },
    { sqlTimestamp => new Date(sqlTimestamp.getTime()) }
  )
}

class RatingTable(tag: Tag) extends Table[Rating](tag, "rating") {
  def userId: Rep[String] = column[String]("user_id")
  def movieId: Rep[String] = column[String]("movie_id")
  def rating: Rep[Float] = column[Float]("rating")
  def ratingTimestamp: Rep[Instant] = column[Instant]("rating_timestamp") //(DateMapper.dateToTimestamp)
  def isExplicit: Rep[Boolean] = column[Boolean]("is_explicit", O.Default(true))

  def * = (userId, movieId, rating, ratingTimestamp, isExplicit) <> ((Rating.apply _).tupled, Rating.unapply)
}