package repository

import common.domain.genres.GenreId
import main.scala.common.model.MovieGenre

import scala.common.domain.movies.{Movie, MovieId}

trait MovieRepository[F[_]] {

  def findAll: F[Seq[Movie]]

  def create(movie: Movie): F[MovieId]

  def findById(movieID: MovieId): F[Option[Movie]]

  def delete(movieId: MovieId): F[Int]

  def movieWithGenres(movieId: MovieId): F[Seq[(Movie, MovieGenre)]]

  def moviesWithGenres(movieIds: Set[MovieId]): F[Seq[(Movie, MovieGenre)]]

  def moviesByGenre(genreId: GenreId): F[Seq[Movie]]

  def findAllByIds(movieIds: Set[MovieId]): F[Seq[Movie]]

}
