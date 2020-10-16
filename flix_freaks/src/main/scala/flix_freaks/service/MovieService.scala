package main.scala.flix_freaks.service

import domain.{Movie, MovieDetail}

trait MovieService[F[_]] {

  def getMovieDetails(movieId: String): F[Option[MovieDetail]]
  def getMoviesByGenre(genreId: Int): F[Seq[Movie]]

}
