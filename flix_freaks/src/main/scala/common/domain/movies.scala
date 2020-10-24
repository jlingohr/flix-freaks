package scala.common.domain

import common.domain.genres.Genre
import io.estatico.newtype.macros.newtype

object movies {

  @newtype case class MovieId(value: String)
  @newtype case class Title(value: String)

  final case class Movie(movieId: MovieId, title: Title, year: Option[Int])

  final case class MovieDetail(movieId: Movie, genres: Seq[Genre])

}
