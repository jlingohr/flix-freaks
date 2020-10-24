package repository.interpreter

import common.domain.genres.GenreId
import main.scala.common.model.MovieGenre
import main.scala.common.model.MovieGenres._
import main.scala.common.model.Movies._
import repository.MovieRepository
import slick.jdbc.PostgresProfile.api._

import scala.common.domain.movies.{Movie, MovieId}


class MovieSlickRepository extends MovieRepository[DBIO] {
  import common.model.SlickColumnMapper._

  def findAll: DBIO[Seq[Movie]] = movies.result

  def create(movie: Movie): DBIO[MovieId] = movies.returning(movies.map(_.movieId)) += movie

  //TODO what if movieId not exists?
  def findById(movieID: MovieId): DBIO[Option[Movie]] = movies.filter(_.movieId === movieID).result.headOption

  def delete(movieId: MovieId): DBIO[Int] = movies.filter(_.movieId === movieId).delete

  def movieWithGenres(movieId: MovieId): DBIO[Seq[(Movie, MovieGenre)]] = {
    val query = (movies
      .filter(_.movieId === movieId) join movieGenres on (_.movieId === _.movieId))
      .result

    query
  }

  def moviesByGenre(genreId: GenreId): DBIO[Seq[Movie]] = {
    val movieIds = movieGenres.filter(_.genreId === genreId).map(_.movieId)
    val res = for {
      m <- movies.filter(_.movieId.in(movieIds))
    } yield m

    res.result
  }

  def moviesWithGenres(movieIds: Set[MovieId]): DBIO[Seq[(Movie, MovieGenre)]] = {
    val query =
      (movies.filter(_.movieId.inSet(movieIds))
      join
      movieGenres.filter(_.movieId.inSet(movieIds))).result
    query
  }

  def findAllByIds(movieIds: Set[MovieId]): DBIO[Seq[Movie]] =
    movies.filter(_.movieId.inSet(movieIds)).result
}
