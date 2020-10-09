package repository.interpreter

import config.DatabaseConfig
import domain.Movie
import main.scala.common.repository.MovieGenre
import main.scala.common.repository.MovieGenres._
import main.scala.common.repository.Movies._
import repository.MovieRepository
import slick.dbio.NoStream
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction

import scala.concurrent.Future

trait MovieSlickRepository extends MovieRepository with DatabaseConfig {


  implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }

  def findAll: Future[Seq[Movie]] = movies.result

  def create(movie: Movie): Future[String] = movies.returning(movies.map(_.movieId)) += movie

  //TODO what if movieId not exists?
  def findById(movieID: String): Future[Option[Movie]] = db.run(movies.filter(_.movieId === movieID).result.headOption)

  def delete(movieId: String): Future[Int] = movies.filter(_.movieId === movieId).delete

  def movieWithGenres(movieId: String): Future[Seq[(Movie, MovieGenre)]] =
    db.run(
      (movies
        .filter(_.movieId === movieId) join movieGenres on (_.movieId === _.movieId))
        .result
    )

  def moviesByGenre(genreId: Int): Future[Seq[Movie]] = {
    val movieIds = movieGenres.filter(_.genreId === genreId).map(_.movieId)
    val res = for {
      m <- movies.filter(_.movieId.in(movieIds))
    } yield m

    db.run(res.result)
  }

  def moviesWithGenres(movieIds: Set[String]): Future[Seq[(Movie, MovieGenre)]] = {
    db.run(
      (movies.filter(_.movieId.inSet(movieIds))
        join
        movieGenres.filter(_.movieId.inSet(movieIds))).result
    )
  }

  def findAllByIds(movieIds: Set[String]): Future[Seq[Movie]] =
    db.run(movies.filter(_.movieId.inSet(movieIds)).result)
}

object MovieSlickRepository extends MovieSlickRepository