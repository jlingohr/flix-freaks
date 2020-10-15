package main.scala.common.model

import domain.Movie
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag


class MovieTable(tag: Tag) extends Table[Movie](tag, "movie") {
  def movieId = column[String]("movie_id", O.PrimaryKey)
  def title = column[String]("title")
  def year = column[Option[Int]]("year")

  override def * = (movieId, title, year) <> ((Movie.apply _).tupled, Movie.unapply)

  //  def genres = foreignKey("SUP_FK", movie_id, TableQuery[Genre])(_.id)
}

object Movies {
  val movies = TableQuery[MovieTable]
}
