package repository.interpreter

import config.DatabaseConfig
import domain.Movie
import repository.{MovieGenre, MovieGenreTable, MovieRepository, MovieTable}
import slick.dbio.NoStream
import slick.lifted.TableQuery
import slick.sql.SqlAction
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

trait MovieSlickRepository extends MovieRepository with DatabaseConfig {
  val movieTable = TableQuery[MovieTable]
  val movieGenres = TableQuery[MovieGenreTable]


  implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }

  def findAll: Future[Seq[Movie]] = movieTable.result

  def create(movie: Movie): Future[String] = movieTable.returning(movieTable.map(_.movieId)) += movie

  //TODO what if movieId not exists?
  def findById(movieID: String): Future[Option[Movie]] = db.run(movieTable.filter(_.movieId === movieID).result.headOption)

  def delete(movieId: String): Future[Int] = movieTable.filter(_.movieId === movieId).delete

  def movieWithGenres(movieId: String): Future[Seq[(Movie, MovieGenre)]] =
    db.run(
      (movieTable
        .filter(_.movieId === movieId) join movieGenres on (_.movieId === _.movieId))
        .result
    )

  def moviesByGenre(genreId: Int): Future[Seq[Movie]] = {
    val movieIds = movieGenres.filter(_.genreId === genreId).map(_.movieId)
    val movies = for {
      m <- movieTable.filter(_.movieId.in(movieIds))
    } yield m

    db.run(movies.result)
  }

  def moviesWithGenres(movieIds: Set[String]): Future[Seq[(Movie, MovieGenre)]] = {
    db.run(
      (movieTable.filter(_.movieId.inSet(movieIds))
        join
        movieGenres.filter(_.movieId.inSet(movieIds))).result
    )
  }

  def findAllByIds(movieIds: Set[String]): Future[Seq[Movie]] =
    db.run(movieTable.filter(_.movieId.inSet(movieIds)).result)
}

object MovieSlickRepository extends MovieSlickRepository