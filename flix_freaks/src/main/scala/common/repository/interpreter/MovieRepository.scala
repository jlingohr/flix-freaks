package repository.interpreter

import config.DatabaseConfig
import domain.Movie
import main.scala.common.model.MovieGenre
import main.scala.common.model.MovieGenres._
import main.scala.common.model.Movies._
import repository.MovieRepository
import slick.jdbc.PostgresProfile.api._


class MovieSlickRepository extends MovieRepository[DBIO] {

  def findAll: DBIO[Seq[Movie]] = movies.result

  def create(movie: Movie): DBIO[String] = movies.returning(movies.map(_.movieId)) += movie

  //TODO what if movieId not exists?
  def findById(movieID: String): DBIO[Option[Movie]] = movies.filter(_.movieId === movieID).result.headOption

  def delete(movieId: String): DBIO[Int] = movies.filter(_.movieId === movieId).delete

  def movieWithGenres(movieId: String): DBIO[Seq[(Movie, MovieGenre)]] = {
    val query = (movies
      .filter(_.movieId === movieId) join movieGenres on (_.movieId === _.movieId))
      .result

    query
  }

  def moviesByGenre(genreId: Int): DBIO[Seq[Movie]] = {
    val movieIds = movieGenres.filter(_.genreId === genreId).map(_.movieId)
    val res = for {
      m <- movies.filter(_.movieId.in(movieIds))
    } yield m

    res.result
  }

  def moviesWithGenres(movieIds: Set[String]): DBIO[Seq[(Movie, MovieGenre)]] = {
    val query =
      (movies.filter(_.movieId.inSet(movieIds))
      join
      movieGenres.filter(_.movieId.inSet(movieIds))).result
    query
  }

  def findAllByIds(movieIds: Set[String]): DBIO[Seq[Movie]] =
    movies.filter(_.movieId.inSet(movieIds)).result
}

object MovieSlickRepository extends MovieSlickRepository