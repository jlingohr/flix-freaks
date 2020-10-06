package repository

import domain.Movie

import scala.concurrent.Future

trait MovieRepository {

  def findAll: Future[Seq[Movie]]

  def create(movie: Movie): Future[String]

  def findById(movieID: String): Future[Option[Movie]]

  def delete(movieId: String): Future[Int]

  def movieWithGenres(movieId: String): Future[Seq[(Movie, MovieGenre)]]

  def moviesWithGenres(movieIds: Set[String]): Future[Seq[(Movie, MovieGenre)]]

  def moviesByGenre(genreId: Int): Future[Seq[Movie]]

  def findAllByIds(movieIds: Set[String]): Future[Seq[Movie]]

}
