package main.scala.flix_freaks.service


import common.domain.genres.GenreId

import scala.common.domain.movies.{Movie, MovieDetail, MovieId}

trait MovieService[F[_]] {

  def getMovieDetails(movieId: MovieId): F[Option[MovieDetail]]
  def getMoviesByGenre(genreId: GenreId): F[Seq[Movie]]

}
