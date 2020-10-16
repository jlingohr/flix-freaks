package repository

import domain.Movie
import main.scala.common.model.MovieGenre

trait MovieRepository[F[_]] {

  def findAll: F[Seq[Movie]]

  def create(movie: Movie): F[String]

  def findById(movieID: String): F[Option[Movie]]

  def delete(movieId: String): F[Int]

  def movieWithGenres(movieId: String): F[Seq[(Movie, MovieGenre)]]

  def moviesWithGenres(movieIds: Set[String]): F[Seq[(Movie, MovieGenre)]]

  def moviesByGenre(genreId: Int): F[Seq[Movie]]

  def findAllByIds(movieIds: Set[String]): F[Seq[Movie]]

}
